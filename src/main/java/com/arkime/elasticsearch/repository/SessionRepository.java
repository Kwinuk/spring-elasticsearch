package com.arkime.elasticsearch.repository;

import com.arkime.elasticsearch.model.Session;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.SourceFilter;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class SessionRepository {

    private final ElasticsearchOperations elasticsearchOperations;

    public List<Session> findSessionsByDynamicConditions(Map<String, Object> expression, List<String> fields, int length, List<SortBuilder<?>> sorts, long startTime, long stopTime) {
        List<Session> sessions = new ArrayList<>();

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        SourceFilter sourceFilter = new FetchSourceFilter(fields.toArray(new String[0]), null);

        expression.forEach((field, value) -> boolQueryBuilder.must(QueryBuilders.matchQuery(field, value)));

        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder)
                .withPageable(org.springframework.data.domain.PageRequest.of(0, length))
                .withSourceFilter(sourceFilter);

        if (!sorts.isEmpty()) {
            sorts.forEach(searchQueryBuilder::withSort);
        }

        if (startTime > 0 && stopTime > 0) {
            RangeQueryBuilder timeRangeFilter = QueryBuilders.rangeQuery("timestamp")
                    .gte(startTime)
                    .lte(stopTime);
            searchQueryBuilder.withFilter(QueryBuilders.boolQuery().must(timeRangeFilter));
        }

        NativeSearchQuery searchQuery = searchQueryBuilder.build();

        SearchHits<Session> searchHits = elasticsearchOperations.search(searchQuery, Session.class);

        int retrievedDocuments = 0;

        for (SearchHit<Session> hit : searchHits.getSearchHits()) {
            Session session = hit.getContent();
            sessions.add(session);

            if (++retrievedDocuments >= length) {
                return sessions;
            }
        }

        return sessions;
    }

}

