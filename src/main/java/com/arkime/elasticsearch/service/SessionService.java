package com.arkime.elasticsearch.service;
import com.arkime.elasticsearch.model.Session;
import com.arkime.elasticsearch.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.search.sort.SortBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SessionService {

    private final SessionRepository sessionRepository;

    public List<Session> findSessionsByDynamicConditions(Map<String, Object> expression, List<String> fields, int length, List<SortBuilder<?>> sorts, long startTime, long stopTime) {
        return sessionRepository.findSessionsByDynamicConditions(expression, fields, length, sorts, startTime, stopTime);
    }

}