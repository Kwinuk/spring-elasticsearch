package com.arkime.elasticsearch.common;

import lombok.*;

import java.util.List;
import java.util.Map;

@Data
public class SearchRequest {

    // 검색식 필드
    private Map<String, Object> expression;
    // 조회 필드 목록
    private String[] includeFields;
    // 정렬 속성
    private List<Map<String, Object>> sortInfo;
    // 목록 조회 범위
    private long from;
    private long to;
    private String bounding;  // 패킷 수집 지점
    // 페이징
    private int offset; // 페이지 번호
    private int limit;  // 목록 개수

    private Object[] searchAfter;

}