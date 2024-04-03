package com.xcurenet.arkime.service.impl;

import com.xcurenet.arkime.service.SessionService;
import com.xcurenet.arkime.vo.session.SessionVO;
import com.xcurenet.common.util.ElasticsearchUtil;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import com.xcurenet.arkime.vo.ResultVO;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@Qualifier("sessionServiceImpl")
public class SessionServiceImpl implements SessionService {
    private final ElasticsearchOperations operations;
    private final NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
    private NativeSearchQuery query;
    List<SessionVO> results = new ArrayList<>(); // 검색 Sessions 결과를 담을 Result
    private long recordsFiltered;   // 검색 filtered 결과 값
    private long recordsTotal;  // 전체 인덱스 Total 값

    public SessionServiceImpl(ElasticsearchOperations operations) {
        this.operations = operations;
    }

    /**
     * sessions 목록 검색
     * @param expression    : 검색 값
     * @param includeFields : 인덱스에서 조회할 필드 목록
     * @param sortInfo      : 정렬 속송
     * @param startTime     : 검색 날짜 범위(시작)
     * @param stopTime      : 검색 날짜 범위(중단)
     * @param bounding      : 검색 날짜 기준
     * @param offset        : 페이지 번호
     * @param limit         : 페이지에 보여줄 목록 개수
     * @return : Elastic Search Sessions 인덱스 조회 데이터
     */
    @Override
    public ResultVO<SessionVO> search(Map<String, Object> expression, String[] includeFields,
                                      String[] excludeFields, List<Map<String, Object>> sortInfo,
                                      long startTime, long stopTime, String bounding, int offset, int limit) {

        final BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery(); // bool 쿼리 빌더

        // 쿼리 빌드
        ElasticsearchUtil.setFieldsQuery(searchQueryBuilder, includeFields, excludeFields);
        if (expression != null) expression.forEach((field, value) -> ElasticsearchUtil.setTermQuery(boolQueryBuilder, field, value));
        ElasticsearchUtil.setRangeQuery(boolQueryBuilder, bounding, startTime, stopTime);
        ElasticsearchUtil.setSortQuery(searchQueryBuilder, sortInfo);
        ElasticsearchUtil.setPageableQuery(searchQueryBuilder, offset, limit);
        ElasticsearchUtil.setBoolQuery(searchQueryBuilder, boolQueryBuilder);
        // 쿼리 생성
        query = searchQueryBuilder.build();

        // 검색 실행
        SearchHits<SessionVO> hits = ElasticsearchUtil.search(query, SessionVO.class, operations);
        results = ElasticsearchUtil.searchResultData(hits);    // 검색 데이터를 List<SessionsVO> 객체에 저장
        recordsFiltered = Objects.requireNonNull(hits).getTotalHits();  // 검색 filtered 값
        // arkime_sessions* 전체 인덱스 문서 값 추출
        recordsTotal = ElasticsearchUtil.getRecordsTotal(operations, SessionVO.class, "arkime_sessions*");

        return new ResultVO<>(results, recordsTotal, recordsFiltered);
    }

    /**
     * 10000개 이상의 데이터 검색
     * @param expression    : 검색 값
     * @param includeFields : 인덱스에서 조회할 필드 목록
     * @param sortInfo      : 정렬 속성
     * @param startTime     : 검색 날짜 범위(시작)
     * @param stopTime      : 검색 날짜 범위(중단)
     * @param bounding      : 검색 날짜 기준
     * @param offset        : 페이지 번호
     * @param limit         : 목록 개수
     * @return : Elastic Search Sessions 인덱스 조회 데이터
     */
    @Override
    public ResultVO<SessionVO> searchAfter(Map<String, Object> expression, String[] includeFields,
                                           String[] excludeFields, List<Map<String, Object>> sortInfo,
                                           long startTime, long stopTime, String bounding, int offset, int limit) {

        final BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery(); // bool 쿼리 빌더
        final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        List<SessionVO> results = new ArrayList<>();
        List<Object> searchAfter = null;

        try {
            do {
                if (searchAfter != null) {
                    searchSourceBuilder.searchAfter(searchAfter.toArray());
                }


                // 쿼리 빌드
                ElasticsearchUtil.setRangeQuery(boolQueryBuilder, bounding, startTime, stopTime);
                if (expression != null) expression.forEach((field, value) -> ElasticsearchUtil.setTermQuery(boolQueryBuilder, field, value));
                ElasticsearchUtil.setFieldsQuery(searchQueryBuilder, includeFields, excludeFields);
                ElasticsearchUtil.setBoolQuery(searchQueryBuilder, boolQueryBuilder);
                ElasticsearchUtil.setSortQuery(searchQueryBuilder, sortInfo);
                query = searchQueryBuilder.build();

                // 검색 실행
                SearchHits<SessionVO> hits = ElasticsearchUtil.search(query, SessionVO.class, operations);
                recordsFiltered += Objects.requireNonNull(hits).getTotalHits();    // 검색 filtered 값

                // Extract search_after values
                if (hits.getTotalHits() > 0) {
                    List<SearchHit<SessionVO>> hitList = hits.getSearchHits();
                    SearchHit<SessionVO> lastHit = hitList.get(hitList.size() - 1);
//                    Object[] searchAfterArray = lastHit.getSortValues().toArray(); // Convert to array
                    searchAfter = Collections.singletonList(lastHit.getId());
//                    searchAfter = Arrays.asList(searchAfterArray);
                } else {
                    break;
                }

                // Add current page results to the overall result set
                results.addAll(ElasticsearchUtil.searchResultData(hits));
            } while (true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ResultVO<>(results, recordsFiltered, recordsFiltered, searchAfter);
    }

    public ResultVO<SessionVO> searchSessionBatch(Map<String, Object> expression, long startTime, long stopTime, String bounding, int limit) {
        final String[] includeFields = new String[]{"_id", "packetPos", "fileId", "node", "firstPacket"};
        List<Map<String, Object>> sortInfo = new ArrayList<>();
        Map<String, Object> sortField = new HashMap<>();
        sortField.put("_id", "desc");
        sortInfo.add(sortField);

        int batchSize = 10000; // 1000개씩 요청
        int offset = 0;
        int iterations = (int) Math.ceil((double) limit / batchSize); // limit과 batchSize에 기반하여 반복 횟수 계산

        for (int i = 0; i < iterations; i++) {
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery(); // bool 쿼리 빌더

            // 쿼리 빌드
            ElasticsearchUtil.setFieldsQuery(searchQueryBuilder, includeFields);
            if (expression != null) expression.forEach((field, value) -> ElasticsearchUtil.setTermQuery(boolQueryBuilder, field, value));
            ElasticsearchUtil.setRangeQuery(boolQueryBuilder, bounding, startTime, stopTime);
            ElasticsearchUtil.setSortQuery(searchQueryBuilder, sortInfo);
            ElasticsearchUtil.setBoolQuery(searchQueryBuilder, boolQueryBuilder);

            int currentBatchSize = (i == iterations - 1) ? limit - offset : batchSize;

            ElasticsearchUtil.setPageableQuery(searchQueryBuilder, offset, currentBatchSize);
            // 쿼리 생성
            query = searchQueryBuilder.build();

            // 검색 실행
            SearchHits<SessionVO> hits = ElasticsearchUtil.search(query, SessionVO.class, operations);
            List<SessionVO> batchResults = ElasticsearchUtil.searchResultData(hits); // 검색 데이터를 List<SessionsVO> 객체에 저장
            results.addAll(batchResults);

            recordsFiltered += Objects.requireNonNull(hits).getTotalHits(); // 검색 filtered 값
            offset += currentBatchSize;
        }

        // arkime_sessions* 전체 인덱스 문서 값 추출
        recordsTotal = ElasticsearchUtil.getRecordsTotal(operations, SessionVO.class, "arkime_sessions*");

        return new ResultVO<>(results, recordsTotal, recordsFiltered);
    }

}

