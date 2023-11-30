package com.arkime.elasticsearch.util;

import com.arkime.elasticsearch.common.ArkimeFields;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import java.util.*;

@RequiredArgsConstructor
public class EsQueryUtil {

    public static void setRangeQuery(BoolQueryBuilder boolQueryBuilder, String bounding, long from, long to) {
        String field = null;

        if (bounding.equals("firstPacket")) field = ArkimeFields.FIELD_START_TIME;
        else if (bounding.equals("lastPacket")) field = ArkimeFields.FIELD_STOP_TIME;

        if (from > 0 && to > 0) {
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery(field)
                    .gte(from)
                    .lte(to);
            boolQueryBuilder.filter(rangeQuery);
        }
    }

    public static void setTermQuery(BoolQueryBuilder boolQueryBuilder, String field, Object value) {
        boolQueryBuilder.must(QueryBuilders.termQuery(field, value));
    }

    public static void setBoolQuery(NativeSearchQueryBuilder searchQueryBuilder, BoolQueryBuilder boolQueryBuilder) {
        searchQueryBuilder.withQuery(boolQueryBuilder);
    }

    public static void setPageableQuery(NativeSearchQueryBuilder searchQueryBuilder, int offset, int limit) {
        searchQueryBuilder.withPageable(PageRequest.of(offset, limit));
    }

    public static void setFieldsQuery(NativeSearchQueryBuilder searchQueryBuilder, String[] fields, String[] defaultFields) {
        // 필드 목록 유효성 검사
        validateFields(fields);

        if (fields != null) {
            searchQueryBuilder.withSourceFilter(new FetchSourceFilter(fields, null));
        } else {
            // 필드가 없으면 디폴트 필드 설정
            searchQueryBuilder.withSourceFilter(new FetchSourceFilter(defaultFields, null));
        }
    }

    public static void setSortQuery(NativeSearchQueryBuilder searchQueryBuilder, List<Map<String, Object>> sortInfo) {
        List<SortBuilder<?>> sorts = new ArrayList<>();

        if (sortInfo != null && !sortInfo.isEmpty()) {
            for (Map<String, Object> sortField : sortInfo) {
                String field = sortField.keySet().iterator().next();
                String order = (String) sortField.get(field);

                if ("desc".equals(order)) {
                    sorts.add(SortBuilders.fieldSort(field).order(SortOrder.DESC));
                } else if ("asc".equals(order)) {
                    sorts.add(SortBuilders.fieldSort(field).order(SortOrder.ASC));
                }
            }

            if (!sorts.isEmpty()) {
                searchQueryBuilder.withSorts(sorts);
            }
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

    public static <T> Object[] SearchAfter(SearchHits<T> hits) {
        if (hits.getSearchHits().isEmpty()) {
            return null;
        }

        SearchHit<T> lastHit = hits.getSearchHits().get(hits.getSearchHits().size() - 1);
        return new List[]{lastHit.getSortValues()};
    }

}
