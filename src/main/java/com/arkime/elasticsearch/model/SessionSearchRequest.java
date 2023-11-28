package com.arkime.elasticsearch.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import lombok.Data;
import org.elasticsearch.search.sort.SortBuilder;

import java.util.List;
import java.util.Map;

@Data
public class SessionSearchRequest {

    // 검색식 필드
    private Map<String, Object> expression;
    // 조회 필드 목록
    private List<String> fields;
    // 정렬 속성
    private List<SortBuilder<?>> sorts;
    // 조회 목록 크기
    private int length;
    // 패킷(세션) 수집 시작 시간
    private long startTime;
    // 패킷(세션) 수집 중단 시간
    private long stopTime;
    
}