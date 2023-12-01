package com.arkime.elasticsearch.util;

import com.arkime.elasticsearch.common.ArkimeFields;
import com.arkime.elasticsearch.session.repository.SessionRepository;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;

import java.util.*;

@NoArgsConstructor
public class EsUtil {
    private static final Logger log = LogManager.getLogger(SessionRepository.class);

    public static void setRangeQuery(BoolQueryBuilder boolQueryBuilder, String bounding, long startTime, long stopTime) {
        String field = null;

        if (bounding.equals("firstPacket")) field = ArkimeFields.FIELD_START_TIME;
        else if (bounding.equals("lastPacket")) field = ArkimeFields.FIELD_STOP_TIME;

        if (startTime > 0 && stopTime > 0) {
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery(field)
                    .gte(startTime)
                    .lte(stopTime);
            boolQueryBuilder.filter(rangeQuery);
        }
    }

    public static void setTermQuery(BoolQueryBuilder boolQueryBuilder, String field, Object value) {
        boolQueryBuilder.must(QueryBuilders.termQuery(field, value));
    }

    public static void setBoolQuery(NativeSearchQueryBuilder searchQueryBuilder, BoolQueryBuilder boolQueryBuilder) {
        searchQueryBuilder.withQuery(boolQueryBuilder);
    }

    public static void setPageableQuery(NativeSearchQueryBuilder searchQueryBuilder, int from, int size) {
        searchQueryBuilder.withPageable(PageRequest.of(from, size));
    }

    public static void setFieldsQuery(NativeSearchQueryBuilder searchQueryBuilder, String[] includeFields, String[] defaultFields) {
        // 필드 목록 유효성 검사
        validateFields(includeFields);

        if (includeFields != null) {
            searchQueryBuilder.withSourceFilter(new FetchSourceFilter(includeFields, null));
        } else {
            // 필드가 없으면 디폴트 필드 설정
            searchQueryBuilder.withSourceFilter(new FetchSourceFilter(defaultFields, null));
        }
    }

    public static void setSortQuery(NativeSearchQueryBuilder searchQueryBuilder, List<Map<String, Object>> sortInfo) {
//        List<SortBuilder<?>> sorts = new ArrayList<>();

        if (sortInfo != null && !sortInfo.isEmpty()) {
            for (Map<String, Object> sortField : sortInfo) {
                String field = sortField.keySet().iterator().next();
                String order = (String) sortField.get(field);

                if ("desc".equals(order)) {
                    searchQueryBuilder.withSorts(new FieldSortBuilder(SortBuilders.fieldSort(field).order(SortOrder.DESC)));
//                    sorts.add(SortBuilders.fieldSort(field).order(SortOrder.DESC));
                } else if ("asc".equals(order)) {
                    searchQueryBuilder.withSorts(new FieldSortBuilder(SortBuilders.fieldSort(field).order(SortOrder.ASC)));
//                    sorts.add(SortBuilders.fieldSort(field).order(SortOrder.ASC));
                }
            }

//            if (!sorts.isEmpty()) {
//                searchQueryBuilder.withSorts(new FieldSortBuilder(SortBuilders.fieldSort()));
//            }
        }
    }

    public static <T> SearchHits<T> search(NativeSearchQuery query, Class<T> VoType, ElasticsearchOperations operations) {
        return operations.search(query, VoType);
    }

    public static <T> List<T> searchResultData(SearchHits<T> hits) {
        List<T> results = new ArrayList<>();

        for (SearchHit<T> hit : hits.getSearchHits()) {
            T result = hit.getContent();
            results.add(result);
        }

        return results;
    }

    public static <T> long getRecordsFiltered(SearchHits<T> hits) {
        return hits.getTotalHits();
    }

    public static <T> long getRecordsTotal(ElasticsearchOperations operations, Class<T> VoType, String index) {
        return operations.count(Query.findAll(), VoType, IndexCoordinates.of(index));
    }

    private static void validateFields(String[] fields) {
        if (fields == null || fields.length == 0) {
            return;
        }
        Set<String> validFields = new HashSet<>(Arrays.asList(ArkimeFields.validateFields));

        for (String field : fields) {
            if (!validFields.contains(field)) {
                throw new IllegalArgumentException("Invalid field: " + field);
            }
        }
    }

}
