package com.arkime.elasticsearch.util;

import com.arkime.common.ArkimeFields;
import com.arkime.common.log.Debugger;
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

    private static Debugger debugger;

    @Autowired
    public EsUtil(Debugger debugger) {
        EsUtil.debugger = debugger;
    }

    public static void setRangeQuery(BoolQueryBuilder boolQueryBuilder, String bounding, Long startTime, Long stopTime) {
        String field;

        if (bounding != null) {
            if (bounding.equals("firstPacket")) field = ArkimeFields.FIELD_START_TIME;
            else if (bounding.equals("lastPacket")) field = ArkimeFields.FIELD_STOP_TIME;
            else field = ArkimeFields.FIELD_START_TIME;
        } else {
            debugger.warn("Bounding value cannot be null.", EsUtil.class);
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
            debugger.error("Invalid startTime And stopTime value : ", startAndStopTime, EsUtil.class);

            throw new IllegalArgumentException("Invalid startTime value : " + startTime + " And stopTime Value: " + stopTime);
        }
    }

    public static void setTermQuery(BoolQueryBuilder boolQueryBuilder, String field, Object value) {
        try {
            validateField(field);

            if (field != null && value != null) {
                boolQueryBuilder.must(QueryBuilders.termQuery(field, value));
            } else {
                debugger.warn("Null value provided for field or value in setTermQuery", EsUtil.class);
            }
        } catch (IllegalArgumentException e) {
            debugger.error("IllegalArgumentException occurred during field validation in setTermQuery", e, EsUtil.class);
            throw new IllegalArgumentException(e);
        } catch (RuntimeException e) {
            debugger.error("Validation failed : {}", e.getMessage(), EsUtil.class);

        } catch (Exception e) {
            debugger.error("Exception occurred during field validation in setTermQuery", e, EsUtil.class);
            throw new RuntimeException(e);
        }
    }

    public static void setBoolQuery(NativeSearchQueryBuilder searchQueryBuilder, BoolQueryBuilder boolQueryBuilder) {
        try {
            searchQueryBuilder.withQuery(boolQueryBuilder);

            debugger.info("[SEARCH_QUERY] : {}", searchQueryBuilder.build().getQuery(), EsUtil.class);
        } catch (Exception e) {
            debugger.error("Error setting bool query", e, EsUtil.class);
            throw new RuntimeException(e);
        }
    }

    public static void setPageableQuery(NativeSearchQueryBuilder searchQueryBuilder, int offset, int limit) {
        try {
            offset = (offset == 0) ? 0 : (offset != -1) ? offset : 0;
            limit = (limit == 0) ? 50 : (limit != -1) ? Math.min(limit, 5000) : 50;

            debugger.info("[PAGEABLE_SETTINGS] offset : {}, limit : {}",offset, limit, EsUtil.class);

            searchQueryBuilder.withPageable(PageRequest.of(offset, limit));
        } catch (Exception e) {
            debugger.error("Invalid offset or limit values ", e, EsUtil.class);
            throw new IllegalArgumentException(e);
        }
    }

    public static void setFieldsQuery(NativeSearchQueryBuilder searchQueryBuilder, String[] includeFields, String[] excludeFields) {
        // 필드 유효성 검사
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
                        debugger.info("[SORT_SETTINGS] Field : {}, Order : {}", field, order, EsUtil.class);
                    } else {
                        debugger.error("Invalid Sort Field or Order Type", EsUtil.class);
                    }
                }
            }
        }
    }

    public static <T> SearchHits<T> search(NativeSearchQuery query, Class<T> voType, ElasticsearchOperations operations) {
        int retryCount = 0;

        while (true) {
            try {
                return operations.search(query, voType);
            } catch (Exception e) {
                retryCount++;
                debugger.error("Error during Elasticsearch search operation (Retry " + retryCount + " of 5)", e, EsUtil.class);

                if (retryCount <= 5) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                } else {
                    debugger.warn("Maximum retry attempts reached", EsUtil.class);
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
                        debugger.warn("Null content in SearchHit", EsUtil.class);
                    }
                } catch (Exception e) {
                    debugger.error("Error getting content from SearchHit", EsUtil.class);
                    throw new RuntimeException("Error getting content from SearchHit", e);
                }
            }
        } else {
            debugger.warn("Null SearchHits provided", EsUtil.class);
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
                debugger.warn("Error during Elasticsearch count operation (Retry " + retryCount + " of " + 5 + ")", EsUtil.class);

                if (retryCount <= 5) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                } else {
                    debugger.error("Maximum retry attempts reached", EsUtil.class);
                    throw new RuntimeException("Maximum retry attempts reached", e);
                }
            }
        }
    }

    private static void validateFields(String[] fields) {
        if (fields == null || fields.length == 0) {
            debugger.warn("No field provided for validation.", EsUtil.class);
        }

        Set<String> validFields = new HashSet<>(Arrays.asList(ArkimeFields.validateFields));

        for (String field : Objects.requireNonNull(fields)) {
            if (!validFields.contains(field)) {
                debugger.error("Invalid Field : {}", field, EsUtil.class);
                throw new RuntimeException("Invalid Field");
            }
        }
    }

    private static void validateField(String field) {
        if (field == null) {
            debugger.warn("No field provided for validation.", EsUtil.class);
        }

        Set<String> validFields = new HashSet<>(Arrays.asList(ArkimeFields.validateFields));

        if (!validFields.contains(field)) {
            debugger.error("[Invalid Field] field : {}", field, EsUtil.class);
            throw new RuntimeException("Invalid Field");
        }
    }

}
