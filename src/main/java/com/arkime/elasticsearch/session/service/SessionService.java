package com.arkime.elasticsearch.session.service;

import com.arkime.elasticsearch.session.model.SearchResult;
import com.arkime.elasticsearch.session.model.SessionVO;
import com.arkime.elasticsearch.session.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SessionService {

    private final SessionRepository sessionRepository;

    public SearchResult<SessionVO> searchSessions(Map<String, Object> expression, String[] includeFields,
                                                  String[] excludeFields, List<Map<String, Object>> sortInfo,
                                                  long startTime, long stopTime, String bounding, int offset, int limit) {

        return sessionRepository.searchSessions(expression, includeFields, excludeFields, sortInfo, startTime, stopTime, bounding, offset, limit);

    }

}