package com.xcurenet.arkime.service;

import com.xcurenet.arkime.vo.ResultVO;
import com.xcurenet.arkime.vo.session.SessionVO;

import java.util.List;
import java.util.Map;

public interface SessionService {
    ResultVO<SessionVO> search(Map<String, Object> expression, String[] includeFields,
                               String[] excludeFields, List<Map<String, Object>> sortInfo,
                               long startTime, long stopTime, String bounding, int offset, int limit);
    ResultVO<SessionVO> searchAfter(Map<String, Object> expression, String[] includeFields,
                                    String[] excludeFields, List<Map<String, Object>> sortInfo,
                                    long startTime, long stopTime, String bounding, int offset, int limit);

    ResultVO<SessionVO> searchSessionBatch(Map<String, Object> expression, long startTime, long stopTime, String bounding, int limit);
}