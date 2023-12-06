package com.arkime.elasticsearch.sessions.service;

import com.arkime.elasticsearch.sessions.vo.SessionListVO;
import com.arkime.elasticsearch.sessions.vo.SessionVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public interface SessionService {

    @Transactional(readOnly = true)
    SessionListVO<SessionVO> searchSessions(Map<String, Object> expression, String[] includeFields,
                                            String[] excludeFields, List<Map<String, Object>> sortInfo,
                                            long startTime, long stopTime, String bounding, int offset, int limit);
    @Transactional(readOnly = true)
    SessionListVO<SessionVO> searchAfterSessions(Map<String, Object> expression, String[] includeFields,
                                                 String[] excludeFields, List<Map<String, Object>> sortInfo,
                                                 long startTime, long stopTime, String bounding, int offset, int limit);
}