package com.xcurenet.arkime.service.impl;

import com.xcurenet.arkime.service.PacketService;
import com.xcurenet.arkime.vo.ResultVO;
import com.xcurenet.arkime.vo.file.FileVO;
import com.xcurenet.arkime.vo.session.SessionVO;
import com.xcurenet.common.util.ElasticsearchUtil;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
@Qualifier("packetServiceImpl")
public class PacketServiceImpl implements PacketService {
    private final ElasticsearchOperations operations;
    private final NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
    private NativeSearchQuery query;
    private List<FileVO> results; // 검색 Sessions 결과를 담을 Result
    private long recordsFiltered;   // 검색 filtered 결과 값
    private long recordsTotal;  // 전체 인덱스 Total 값

    public PacketServiceImpl(ElasticsearchOperations operations) {
        this.operations = operations;
    }

    @Override
    public ResultVO<FileVO> search(Map<String, Object> expression, String[] includeFields,
                                   String[] excludeFields, List<Map<String, Object>> sortInfo,
                                   long startTime, long stopTime, String bounding, int offset, int limit) {

        final BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery(); // bool 쿼리 빌더

        // 쿼리 빌드
        if (expression != null) {
            expression.forEach((field, value) -> ElasticsearchUtil.setTermQuery(boolQueryBuilder, field, value));
        }
        ElasticsearchUtil.setRangeQuery(boolQueryBuilder, bounding, startTime, stopTime);
        ElasticsearchUtil.setSortQuery(searchQueryBuilder, sortInfo);
        ElasticsearchUtil.setPageableQuery(searchQueryBuilder, offset, limit);
        ElasticsearchUtil.setBoolQuery(searchQueryBuilder, boolQueryBuilder);
        // 쿼리 생성
        query = searchQueryBuilder.build();

        // 검색 실행
        SearchHits<FileVO> hits = ElasticsearchUtil.search(query, FileVO.class, operations);
        results = ElasticsearchUtil.searchResultData(hits);    // 검색 데이터를 List<SessionsVO> 객체에 저장
        recordsFiltered = Objects.requireNonNull(hits).getTotalHits();  // 검색 filtered 값
        // arkime_sessions* 전체 인덱스 문서 값 추출
        recordsTotal = ElasticsearchUtil.getRecordsTotal(operations, FileVO.class, "arkime_files_v*");

        return new ResultVO<>(results, recordsTotal, recordsFiltered);
    }
}
