package com.arkime.elasticsearch.session.repository;

import com.arkime.elasticsearch.common.ArkimeFields;
import com.arkime.elasticsearch.session.model.SessionVO;
import com.arkime.elasticsearch.util.EsUtil;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import com.arkime.elasticsearch.session.model.SearchResult;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Repository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class SessionRepository {
    private final ElasticsearchOperations operations;
    private static final Logger log = LogManager.getLogger(SessionRepository.class);

    NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder(); // 쿼리 빌더
    BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery(); // bool 쿼리 빌더
    NativeSearchQuery query;    // 쿼리

    List<SessionVO> results = null; // 검색 결과를 담을 Result
    long recordsTotal;  // arkime_sessions* 전체 인덱스 Total 값
    long recordsFiltered = 0;   // 검색 filtered 결과 값

    /**
     * sessions 목록 검색
     * @param expression    : 검색 값
     * @param includeFields : 인덱스에서 조회할 필드 목록
     * @param sortInfo      : 정렬 속송
     * @param startTime     : 검색 날짜 범위(시작)
     * @param stopTime      : 검색 날짜 범위(중단)
     * @param bounding      : 검색 날짜 기준
     * @param from          : 페이지 번호
     * @param size          : 페이지에 보여줄 목록 개수
     * @return : Elastic Search Sessions 인덱스 조회 데이터
     */
    public SearchResult<SessionVO> searchSessions(Map<String, Object> expression, String[] includeFields,
                                                  List<Map<String, Object>> sortInfo, long startTime,
                                                  long stopTime, String bounding, int from, int size) {

        // 쿼리 빌드
        EsUtil.setPageableQuery(searchQueryBuilder, from, size);
        EsUtil.setRangeQuery(boolQueryBuilder, bounding, startTime, stopTime);
        expression.forEach((field, value) -> EsUtil.setTermQuery(boolQueryBuilder, field, value));
        EsUtil.setFieldsQuery(searchQueryBuilder, includeFields, ArkimeFields.defaultFields);
        EsUtil.setBoolQuery(searchQueryBuilder, boolQueryBuilder);
        EsUtil.setSortQuery(searchQueryBuilder, sortInfo);
        // 쿼리 생성
        query = searchQueryBuilder.build();
        // 검색 실행
        SearchHits<SessionVO> hits = EsUtil.search(query, SessionVO.class, operations);
        results = EsUtil.searchResultData(hits);    // 검색 데이터를 List<SessionsVO> 객체에 저장
        recordsFiltered = hits.getTotalHits();  // 검색 filtered 값

        // arkime_sessions* 전체 인덱스 문서 값 추출
        recordsTotal = EsUtil.getRecordsTotal(operations, SessionVO.class, "arkime_sessions*");

        return new SearchResult<>(results, recordsTotal, recordsFiltered);
    }

    /**
     * 10000개 이상의 데이터 조회에 쓰이는 search after
     * @param expression    : 검색 값
     * @param includeFields : 인덱스에서 조회할 필드 목록
     * @param sortInfo      : 정렬 속송
     * @param startTime     : 검색 날짜 범위(시작)
     * @param stopTime      : 검색 날짜 범위(중단)
     * @param bounding      : 검색 날짜 기준
     * @param from          : 페이지 번호
     * @param size          : 페이지에 보여줄 목록 개수
     * @return : Elastic Search Sessions 인덱스 조회 데이터
     */
    public SearchResult<SessionVO> searchAfterSessions(Map<String, Object> expression, String[] includeFields,
                                                       List<Map<String, Object>> sortInfo, long startTime,
                                                       long stopTime, String bounding, int from, int size) {

        Object[] searchAfter = null;

        try {
            while (true) {
                if (searchAfter != null) {
                    searchQueryBuilder.withSearchAfter(Arrays.asList(searchAfter));
                }

                // 쿼리 빌드
                EsUtil.setPageableQuery(searchQueryBuilder, from, size);
                EsUtil.setRangeQuery(boolQueryBuilder, bounding, startTime, stopTime);
                expression.forEach((field, value) -> EsUtil.setTermQuery(boolQueryBuilder, field, value));
                EsUtil.setFieldsQuery(searchQueryBuilder, includeFields, ArkimeFields.defaultFields);
                EsUtil.setBoolQuery(searchQueryBuilder, boolQueryBuilder);
                EsUtil.setSortQuery(searchQueryBuilder, sortInfo);
                // 쿼리 생성
                query = searchQueryBuilder.build();
                // 검색 실행
                SearchHits<SessionVO> hits = EsUtil.search(query, SessionVO.class, operations);
                recordsFiltered = recordsFiltered + hits.getTotalHits();    // 검색 filtered 값

                // search after 설정
                if (hits.getTotalHits() > 0) {
                    List<SearchHit<SessionVO>> hitList = hits.getSearchHits();
                    SearchHit<SessionVO> lastHit = hitList.get(hitList.size() - 1);

                    searchAfter = Arrays.copyOf(lastHit.getSortValues().toArray(), 2);
                } else {
                    break;
                }

                results = EsUtil.searchResultData(hits);    // 검색 데이터를 List<SessionsVO> 객체에 저장
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Total Document Count 추출
        recordsTotal = EsUtil.getRecordsTotal(operations, SessionVO.class, "arkime_sessions*");

        return new SearchResult<>(results, recordsTotal, recordsFiltered);
    }

}

