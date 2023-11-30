package com.arkime.elasticsearch.session.repository;

import com.arkime.elasticsearch.common.ArkimeFields;
import com.arkime.elasticsearch.session.model.SearchResult;
import com.arkime.elasticsearch.util.EsQueryUtil;
import com.arkime.elasticsearch.session.model.SessionVO;
import com.arkime.elasticsearch.common.SearchRequest;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.SearchHits;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;

@Repository
@RequiredArgsConstructor
@Slf4j
public class SessionRepository {

    private final ElasticsearchOperations operations;
    private static final Logger logger = LoggerFactory.getLogger(SessionRepository.class);

    /**
     * @param request : 요청 객체(검색 조건)
     * @return : Elastic Search Sessions 인덱스 조회 데이터
     */
    public SearchResult<SessionVO> searchSessions(SearchRequest request) {
        final Map<String, Object> expression = request.getExpression(); // 검색식
        final String[] fields = request.getIncludeFields(); // 조회 필드 목록
        final List<Map<String, Object>> sortInfo = request.getSortInfo(); // 정렬 속성
        final int offset = request.getOffset(); // 페이지 번호
        final int limit = request.getLimit();   // 목록 개수
        final long from = request.getFrom();  // 패킷 수집 시작 시간
        final long to = request.getTo();    // 패킷 수집 중단 시간
        final String bounding = request.getBounding();
        final Object[] searchAfter = request.getSearchAfter();

        // 쿼리 빌더 객체 생성
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        NativeSearchQuery query;    // 쿼리 객체 생성

        if (searchAfter != null && searchAfter.length > 0) {
            searchQueryBuilder.withSearchAfter(Arrays.asList(searchAfter));
        }

        // 쿼리 빌드
        EsQueryUtil.setPageableQuery(searchQueryBuilder, offset, limit);
        EsQueryUtil.setRangeQuery(boolQueryBuilder, bounding, from, to);
        expression.forEach((field, value) -> EsQueryUtil.setTermQuery(boolQueryBuilder, field, value));
        EsQueryUtil.setFieldsQuery(searchQueryBuilder, fields, ArkimeFields.defaultFields);
        EsQueryUtil.setBoolQuery(searchQueryBuilder, boolQueryBuilder);
        EsQueryUtil.setSortQuery(searchQueryBuilder, sortInfo);

        // 쿼리 생성
        query = searchQueryBuilder.build();

        // 엘라스틱 서치 Sessions 인덱스(arkime_sessions*) 조회 Hits
        SearchHits<SessionVO> hits = EsQueryUtil.search(query, SessionVO.class, operations);

//        Object[] lastSearchAfter = EsQueryUtil.SearchAfter(hits);
        // Hits 데이터 처리
        List<SessionVO> results = EsQueryUtil.searchResultData(hits);
        // Filtered Count 추출
        long recordsFiltered = EsQueryUtil.getRecordsFiltered(hits);

        // 반환 객체
        return new SearchResult<>(results, 0, recordsFiltered);
    }

}

