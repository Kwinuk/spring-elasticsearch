package com.xcurenet.common.util;

import com.xcurenet.common.arkime.ArkimeField;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
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
@Slf4j
public class ElasticsearchUtil {

    public static void setRangeQuery(BoolQueryBuilder boolQueryBuilder, String bounding, Long startTime, Long stopTime) {
        String field;

        if (bounding != null) {
            if (bounding.equals("firstPacket")) field = ArkimeField.FIELD_START_TIME;
            else if (bounding.equals("lastPacket")) field = ArkimeField.FIELD_STOP_TIME;
            else field = ArkimeField.FIELD_START_TIME;
        } else {
            log.warn("Bounding value cannot be null.");
            field = ArkimeField.FIELD_START_TIME;
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
            throw new IllegalArgumentException("Invalid startTime value : " + startTime + " And stopTime Value: " + stopTime);
        }
    }

    public static void setTermQuery(BoolQueryBuilder boolQueryBuilder, String field, Object value) {
        try {
            validateField(field);

            if (field != null && value != null) {
                log.debug("Adding term query: field={}, value={}", field, value);
                boolQueryBuilder.must(QueryBuilders.termQuery(field, value));
            } else {
                log.warn("Null value provided for field or value in setTermQuery");
            }
        } catch (IllegalArgumentException e) {
            log.error("IllegalArgumentException occurred during field validation in setTermQuery", e);
            throw new IllegalArgumentException("IllegalArgumentException occurred during field validation in setTermQuery", e);
        } catch (RuntimeException e) {
            log.error("Validation failed: {}", e.getMessage());
            throw new RuntimeException("Validation failed ", e);
        } catch (Exception e) {
            log.error("Exception occurred during field validation in setTermQuery", e);
            throw new RuntimeException("Validation failed ", e);
        }
    }

    public static void setBoolQuery(NativeSearchQueryBuilder searchQueryBuilder, BoolQueryBuilder boolQueryBuilder) {
        try {
            searchQueryBuilder.withQuery(boolQueryBuilder);

            log.debug("[SEARCH_QUERY] : {}", searchQueryBuilder.build().getQuery());
        } catch (Exception e) {
            log.error("Error setting bool query", e);
            throw new RuntimeException("Error setting bool query", e);
        }
    }

    public static void setPageableQuery(NativeSearchQueryBuilder searchQueryBuilder, int offset, int limit) {
        try {
            offset = (offset == 0) ? 0 : (offset != -1) ? offset : 0;
            limit = (limit == 0) ? 50 : (limit != -1) ? Math.min(limit, 3000000) : 50;

            log.debug("[PAGEABLE_SETTINGS] offset : {}, limit : {}", offset, limit);

            searchQueryBuilder.withPageable(PageRequest.of(offset, limit));
        } catch (Exception e) {
            log.error("Invalid offset or limit values ", e);
            throw new IllegalArgumentException("Invalid offset or limit values ", e);
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
                searchQueryBuilder.withSourceFilter(new FetchSourceFilter(ArkimeField.getAllFields(), null));
            } else {
                searchQueryBuilder.withSourceFilter(new FetchSourceFilter(includeFields, null));
            }
        } else if (excludeFields != null && excludeFields.length > 0) {
            searchQueryBuilder.withSourceFilter(new FetchSourceFilter(ArkimeField.sessionsDefaultFields, excludeFields));
        } else if (includeFields == null && excludeFields == null) {
            searchQueryBuilder.withSourceFilter(new FetchSourceFilter(ArkimeField.sessionsDefaultFields, null));
        }
        log.debug("[FILED_FILTER]: includeFields={}, excludeFields={}", includeFields, excludeFields);
    }

    public static void setFieldsQuery(NativeSearchQueryBuilder searchQueryBuilder, String[] includeFields) {
        // 필드 유효성 검사
        if (includeFields != null) validateFields(includeFields);

        if (includeFields != null && includeFields.length > 0) {
            if (Arrays.asList(includeFields).contains("all")) {
                searchQueryBuilder.withSourceFilter(new FetchSourceFilter(ArkimeField.getAllFields(), null));
            } else {
                searchQueryBuilder.withSourceFilter(new FetchSourceFilter(includeFields, null));
            }
        }
        log.debug("[FILED_FILTER]: includeFields={}", (Object) includeFields);
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
                        log.debug("[SORT_SETTINGS] Field : {}, Order : {}", field, order);
                    } else {
                        log.error("Invalid Sort Field or Order Type");
                        throw new IllegalArgumentException("Invalid Sort Field or Order Type");
                    }
                }
            }
        }
    }

    public static <T> SearchHits<T> search(NativeSearchQuery query, Class<T> voType, ElasticsearchOperations operations) {
        int maxRetries = 5;
        int retryCount = 0;

        while (retryCount < maxRetries) {
            try {
                return operations.search(query, voType);
            } catch (Exception e) {
                retryCount++;
                handleSearchException(e, retryCount, maxRetries);
            }
        }

        throw new RuntimeException("Elasticsearch 최대 재시도 횟수 도달");
    }

    private static void handleSearchException(Exception e, int retryCount, int maxRetries) {
        log.error("검색 작업 중 오류 발생 (Retry " + retryCount + " of " + maxRetries + ")", e);
        if (retryCount < maxRetries) {
            sleep();
        }
    }

    private static void sleep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    public static <T> List<T> searchResultData(SearchHits<T> hits) {
        List<T> results = new ArrayList<>();

        if (hits != null) {
            for (SearchHit<T> hit : hits.getSearchHits()) {
                try {
                    if (hit != null) { // Null 체크 추가
                        T result = hit.getContent();
                        results.add(result);
                    }
                } catch (Exception e) {
                    log.error("Error getting content from SearchHit");
                    throw new RuntimeException("Error getting content from SearchHit", e);
                }
            }
        } else {
            log.warn("Null SearchHits provided");
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
                log.warn("Error during Elasticsearch count operation (Retry " + retryCount + " of " + 5 + ")");

                if (retryCount <= 5) {
                    sleep();
                } else {
                    log.error("Maximum retry attempts reached");
                    throw new ElasticsearchException("Maximum retry attempts reached", e);
                }
            }
        }
    }

    private static void validateFields(String[] fields) {
        if (fields.length == 0) {
            log.warn("No field provided for validation.");
//            throw new IllegalArgumentException("Invalid Field");
            return;
        }

        Set<String> validFields = new HashSet<>(Arrays.asList(ArkimeField.validateFields));

        for (String field : Objects.requireNonNull(fields)) {
            if (!validFields.contains(field)) {
                log.error("Invalid Field: {}", field);
                throw new IllegalArgumentException("Invalid Field");
            }
        }
    }

    private static void validateField(String field) {
        if (field == null) {
            log.warn("No field provided for validation.");
//            throw new IllegalArgumentException("Invalid Field");
        }

        Set<String> validFields = new HashSet<>(Arrays.asList(ArkimeField.validateFields));

        if (!validFields.contains(field)) {
            log.error("[Invalid Field] field : {}", field);
            throw new IllegalArgumentException("Invalid Field");
        }
    }

}
