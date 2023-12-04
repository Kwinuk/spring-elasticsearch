package com.arkime.elasticsearch.util;

import com.arkime.elasticsearch.common.ArkimeFields;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Component
public class EsUtil {

    private static DebugLogger debugger;

    @Autowired
    public EsUtil(DebugLogger debugger) {
        EsUtil.debugger = debugger;
    }


    public static void setRangeQuery(BoolQueryBuilder boolQueryBuilder, String bounding, Long startTime, Long stopTime) {
        String field = null;

        if (bounding != null) {
            if (bounding.equals("firstPacket")) field = ArkimeFields.FIELD_START_TIME;
            else if (bounding.equals("lastPacket")) field = ArkimeFields.FIELD_STOP_TIME;
        } else {
            debugger.warnMessage("Invalid Bounding Value ", EsUtil.class);
            field = ArkimeFields.FIELD_START_TIME;
        }

        if (startTime == 0 || stopTime == 0) {
            Instant now = Instant.now();
            Instant oneDayAgo = now.minus(1, ChronoUnit.DAYS);

            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery(field)
                    .gte(oneDayAgo.toEpochMilli())
                    .lte(now.toEpochMilli());
            boolQueryBuilder.filter(rangeQuery);
        } else if (startTime < stopTime) {
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery(field)
                    .gte(startTime)
                    .lte(stopTime);
            boolQueryBuilder.filter(rangeQuery);
        } else {
            List<Long> startAndStopTime = List.of(new Long[]{startTime, stopTime});
            debugger.error("Invalid startTime And stopTime value: ", startAndStopTime, EsUtil.class);

            throw new IllegalArgumentException("Invalid startTime value: " + startTime + " And stopTime Value: " + stopTime);
        }
    }

    public static void setTermQuery(BoolQueryBuilder boolQueryBuilder, String field, Object value) {
        try {
            validateField(field);

            if (field != null && value != null) {
                boolQueryBuilder.must(QueryBuilders.termQuery(field, value));
            } else {
                debugger.warnMessage("Null value provided for field or value in setTermQuery", EsUtil.class);
            }
        } catch (IllegalArgumentException e) {
            debugger.errorException("IllegalArgumentException occurred during field validation in setTermQuery", e, EsUtil.class);
            throw new IllegalArgumentException(e);
        } catch (Exception e) {
            debugger.errorException("Exception occurred during field validation in setTermQuery", e, EsUtil.class);
            throw new RuntimeException(e);
        }
    }

    public static void setBoolQuery(NativeSearchQueryBuilder searchQueryBuilder, BoolQueryBuilder boolQueryBuilder) {
        try {
            searchQueryBuilder.withQuery(boolQueryBuilder);

            debugger.info("SEARCH_QUERY : {} ", searchQueryBuilder.build().getQuery(), EsUtil.class);

        } catch (Exception e) {
            debugger.errorException("Error setting bool query", e, EsUtil.class);
            throw new RuntimeException(e);
        }
    }

    public static void setPageableQuery(NativeSearchQueryBuilder searchQueryBuilder, int offset, int limit) {
        try {
//            offset = (offset != -1) ? offset : 0;
//            limit = (limit == 0) ? 50 : Math.min(limit, 5000);
            offset = (offset == 0) ? 0 : (offset != -1) ? offset : 0;
            limit = (limit == 0) ? 50 : (limit != -1) ? Math.min(limit, 5000) : 50;

            searchQueryBuilder.withPageable(PageRequest.of(offset, limit));
        } catch (Exception e) {
            debugger.errorException("Invalid offset or limit values ", e, EsUtil.class);
            throw new IllegalArgumentException(e);
        }
    }

    public static void setFieldsQuery(NativeSearchQueryBuilder searchQueryBuilder, String[] includeFields, String[] excludeFields) {
        // 필드 목록 유효성 검사
        if (includeFields != null) validateFields(includeFields);
        if (excludeFields != null) validateFields(excludeFields);

        if (includeFields != null && includeFields.length > 0 && excludeFields != null) {
            searchQueryBuilder.withSourceFilter(new FetchSourceFilter(includeFields, excludeFields));
        } else if (includeFields != null && includeFields.length > 0) {
            if (Arrays.asList(includeFields).contains("all")) {
                searchQueryBuilder.withSourceFilter(new FetchSourceFilter(ArkimeFields.getAllFields(), null));
            } else {
                searchQueryBuilder.withSourceFilter(new FetchSourceFilter(includeFields, null));
            }
        } else if (excludeFields != null && excludeFields.length > 0) {
            searchQueryBuilder.withSourceFilter(new FetchSourceFilter(ArkimeFields.defaultFields, excludeFields));
        } else if (includeFields == null && excludeFields == null) {
            searchQueryBuilder.withSourceFilter(new FetchSourceFilter(ArkimeFields.defaultFields, null));
        }

    }

    public static void setSortQuery(NativeSearchQueryBuilder searchQueryBuilder, List<Map<String, Object>> sortInfo) {
        if (sortInfo != null && !sortInfo.isEmpty()) {
            for (Map<String, Object> sortField : sortInfo) {
                if (sortField != null && !sortField.isEmpty()) {
                    String field = sortField.keySet().iterator().next();
                    Object orderObject = sortField.get(field);

                    validateField(field);

                    if (field != null && orderObject instanceof String) {
                        String order = (String) orderObject;

                        if ("asc".equals(order)) {
                            searchQueryBuilder.withSorts(new FieldSortBuilder(SortBuilders.fieldSort(field).order(SortOrder.ASC)));
                        } else {
                            searchQueryBuilder.withSorts(new FieldSortBuilder(SortBuilders.fieldSort(field).order(SortOrder.DESC)));
                        }
                    } else {
                        debugger.errorMessage("Invalid sort field or order type", EsUtil.class);
                    }
                }
            }
        }
    }

    public static <T> SearchHits<T> search(NativeSearchQuery query, Class<T> VoType, ElasticsearchOperations operations) {
        int retryCount = 0;

        while (true) {
            try {
                return operations.search(query, VoType);
            } catch (Exception e) {
                retryCount++;
                debugger.errorException("Error during Elasticsearch search operation (Retry " + retryCount + " of " + 5 + ")", e, EsUtil.class);

                if (retryCount <= 5) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                } else {
                    debugger.warnMessage("Maximum retry attempts reached", EsUtil.class);
                    return null;
                }
            }
        }
    }

    public static <T> List<T> searchResultData(SearchHits<T> hits) {
        List<T> results = new ArrayList<>();

        if (hits != null) {
            for (SearchHit<T> hit : hits.getSearchHits()) {
                try {
                    T result = hit != null ? hit.getContent() : null;

                    if (result != null) {
                        results.add(result);
                    } else {
                        debugger.warnMessage("Null content in SearchHit", EsUtil.class);
                    }
                } catch (Exception e) {
                    debugger.errorMessage("Error getting content from SearchHit", EsUtil.class);
                    return results;
//                    throw new RuntimeException(e);
                }
            }
        } else {
            debugger.warnMessage("Null SearchHits provided", EsUtil.class);
            return results;
        }

        return results;
    }

    public static <T> long getRecordsTotal(ElasticsearchOperations operations, Class<T> VoType, String index) {
        int retryCount = 0;

        while (true) {
            try {
                return operations.count(Query.findAll(), VoType, IndexCoordinates.of(index));
            } catch (ElasticsearchException e) {
                retryCount++;
                debugger.warnMessage("Error during Elasticsearch count operation (Retry " + retryCount + " of " + 5 + ")", EsUtil.class);

                if (retryCount <= 5) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                } else {
                    debugger.errorMessage("Maximum retry attempts reached", EsUtil.class);
                    return 0;
                }
            }
        }
    }

    private static void validateFields(String[] fields) {
        if (fields == null || fields.length == 0) {
            debugger.warnMessage("No field provided for validation.", EsUtil.class);
        }

        Set<String> validFields = new HashSet<>(Arrays.asList(ArkimeFields.validateFields));

        for (String field : Objects.requireNonNull(fields)) {
            if (!validFields.contains(field)) {
                debugger.error("Invalid Field: ", field, EsUtil.class);
            }
        }
    }

    private static void validateField(String field) {
        if (field == null) {
            debugger.warnMessage("No field provided for validation.", EsUtil.class);
        }

        Set<String> validFields = new HashSet<>(Arrays.asList(ArkimeFields.validateFields));

        if (!validFields.contains(field)) {
            debugger.error("Invalid Field: ", field, EsUtil.class);
        }
    }


//    public void setRangeQuery(BoolQueryBuilder boolQueryBuilder, String bounding, long startTime, long stopTime) {
//        String field;
//
//        if (bounding.equals("firstPacket")) field = ArkimeFields.FIELD_START_TIME;
//        else if (bounding.equals("lastPacket")) field = ArkimeFields.FIELD_STOP_TIME;
//        else {
//            debugger.error("Invalid Bounding Value: ", bounding, EsUtil.class);
//            throw new IllegalArgumentException("Invalid Bounding Value: " + bounding);
//        }
//
//        if (startTime > 0 && stopTime > 0) {
//            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery(field)
//                    .gte(startTime)
//                    .lte(stopTime);
//            boolQueryBuilder.filter(rangeQuery);
//        } else {
//            throw new IllegalArgumentException("Invalid startTime value: " + startTime + " And stopTime Value: " + stopTime);
//        }
//
//    }
//
//    public void setTermQuery(BoolQueryBuilder boolQueryBuilder, String field, Object value) {
//        try {
//            validateField(field);
//
//            if (field != null && value != null) {
//                boolQueryBuilder.must(QueryBuilders.termQuery(field, value));
//            } else {
//                debugger.warnMessage("Null value provided for field or value in setTermQuery", EsUtil.class);
//            }
//        } catch (IllegalArgumentException e) {
//            debugger.errorException("IllegalArgumentException occurred during field validation in setTermQuery", e, EsUtil.class);
//            throw new IllegalArgumentException(e);
//        } catch (Exception e) {
//            debugger.errorException("Exception occurred during field validation in setTermQuery", e, EsUtil.class);
//            throw new RuntimeException(e);
//        }
//    }
//
//    public void setBoolQuery(NativeSearchQueryBuilder searchQueryBuilder, BoolQueryBuilder boolQueryBuilder) {
//        try {
//            searchQueryBuilder.withQuery(boolQueryBuilder);
//
//            debugger.info("SEARCH_QUERY : {} ", searchQueryBuilder.build().getQuery(), EsUtil.class);
//
//        } catch (Exception e) {
//            debugger.errorException("Error setting bool query", e, EsUtil.class);
//            throw new RuntimeException(e);
//        }
//    }
//
//    public void setPageableQuery(NativeSearchQueryBuilder searchQueryBuilder, Integer offset, Integer limit) {
//        try {
//            offset = (offset != null) ? offset : 0;
//            limit = (limit != null) ? Math.min(limit, 5000) : 100;
//
//            searchQueryBuilder.withPageable(PageRequest.of(offset, limit));
//        } catch (Exception e) {
//            debugger.errorException("Invalid offset or limit values", e, EsUtil.class);
//            throw new IllegalArgumentException(e);
//        }
//    }
//
//    public void setFieldsQuery(NativeSearchQueryBuilder searchQueryBuilder, String[] includeFields, String[] excludeFields) {
//        // 필드 목록 유효성 검사
//        if (includeFields != null) validateFields(includeFields);
//        if (excludeFields != null) validateFields(excludeFields);
//
//        if (includeFields != null && includeFields.length > 0 && excludeFields != null) {
//            searchQueryBuilder.withSourceFilter(new FetchSourceFilter(includeFields, excludeFields));
//        } else if (includeFields != null && includeFields.length > 0) {
//            if (Arrays.asList(includeFields).contains("all")) {
//                searchQueryBuilder.withSourceFilter(new FetchSourceFilter(ArkimeFields.getAllFields(), null));
//            } else {
//                searchQueryBuilder.withSourceFilter(new FetchSourceFilter(includeFields, null));
//            }
//        } else if (excludeFields != null && excludeFields.length > 0) {
//            searchQueryBuilder.withSourceFilter(new FetchSourceFilter(ArkimeFields.defaultFields, excludeFields));
//        } else if (includeFields == null && excludeFields == null) {
//            searchQueryBuilder.withSourceFilter(new FetchSourceFilter(ArkimeFields.defaultFields, null));
//        }
//
//    }
//
//    public void setSortQuery(NativeSearchQueryBuilder searchQueryBuilder, List<Map<String, Object>> sortInfo) {
//        if (sortInfo != null && !sortInfo.isEmpty()) {
//            for (Map<String, Object> sortField : sortInfo) {
//                if (sortField != null && !sortField.isEmpty()) {
//                    String field = sortField.keySet().iterator().next();
//                    Object orderObject = sortField.get(field);
//
//                    validateField(field);
//
//                    if (field != null && orderObject instanceof String) {
//                        String order = (String) orderObject;
//
//                        if ("asc".equals(order)) {
//                            searchQueryBuilder.withSorts(new FieldSortBuilder(SortBuilders.fieldSort(field).order(SortOrder.ASC)));
//                        } else {
//                            searchQueryBuilder.withSorts(new FieldSortBuilder(SortBuilders.fieldSort(field).order(SortOrder.DESC)));
//                        }
//                    } else {
//                        debugger.errorMessage("Invalid sort field or order type", EsUtil.class);
//                    }
//                }
//            }
//        }
//    }
//
//    public <T> SearchHits<T> search(NativeSearchQuery query, Class<T> VoType, ElasticsearchOperations operations) {
//        int retryCount = 0;
//
//        while (true) {
//            try {
//                return operations.search(query, VoType);
//            } catch (Exception e) {
//                retryCount++;
//                debugger.errorException("Error during Elasticsearch search operation (Retry " + retryCount + " of " + 5 + ")", e, EsUtil.class);
//
//                if (retryCount <= 5) {
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException ex) {
//                        Thread.currentThread().interrupt();
//                    }
//                } else {
//                    debugger.errorMessage("Maximum retry attempts reached", EsUtil.class);
//                    return null;
//                }
//            }
//        }
//    }
//
//    public <T> List<T> searchResultData(SearchHits<T> hits) {
//        List<T> results = new ArrayList<>();
//
//        if (hits != null) {
//            for (SearchHit<T> hit : hits.getSearchHits()) {
//                try {
//                    T result = hit != null ? hit.getContent() : null;
//
//                    if (result != null) {
//                        results.add(result);
//                    } else {
//                        debugger.warnMessage("Null content in SearchHit", EsUtil.class);
//                    }
//                } catch (Exception e) {
//                    debugger.errorMessage("Error getting content from SearchHit", EsUtil.class);
//                    throw new RuntimeException(e);
//                }
//            }
//        } else {
//            debugger.warnMessage("Null SearchHits provided", EsUtil.class);
//            return results;
//        }
//
//        return results;
//    }
//
//    public <T> long getRecordsTotal(ElasticsearchOperations operations, Class<T> VoType, String index) {
//        int retryCount = 0;
//
//        while (true) {
//            try {
//                return operations.count(Query.findAll(), VoType, IndexCoordinates.of(index));
//            } catch (ElasticsearchException e) {
//                retryCount++;
//                debugger.errorMessage("Error during Elasticsearch count operation (Retry " + retryCount + " of " + 5 + ")", EsUtil.class);
//
//                if (retryCount <= 5) {
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException ex) {
//                        Thread.currentThread().interrupt();
//                    }
//                } else {
//                    debugger.errorMessage("Maximum retry attempts reached", EsUtil.class);
//                    return 0;
//                }
//            }
//        }
//    }
//
//    private void validateFields(String[] fields) {
//        if (fields == null || fields.length == 0) {
//            debugger.errorMessage("No field provided for validation.", EsUtil.class);
//            throw new IllegalArgumentException();
//        }
//
//        Set<String> validFields = new HashSet<>(Arrays.asList(ArkimeFields.validateFields));
//
//        for (String field : Objects.requireNonNull(fields)) {
//            if (!validFields.contains(field)) {
//                debugger.error("Invalid Field: ", field, EsUtil.class);
//                throw new IllegalArgumentException();
//            }
//        }
//    }
//
//    private void validateField(String field) {
//        if (field == null) {
//            debugger.errorMessage("No field provided for validation.", EsUtil.class);
//            throw new IllegalArgumentException();
//        }
//
//        Set<String> validFields = new HashSet<>(Arrays.asList(ArkimeFields.validateFields));
//
//        if (!validFields.contains(field)) {
//            debugger.error("Invalid Field: ", field, EsUtil.class);
//            throw new IllegalArgumentException();
//        }
//    }

}
