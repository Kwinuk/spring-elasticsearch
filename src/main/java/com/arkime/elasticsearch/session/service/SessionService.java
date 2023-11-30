package com.arkime.elasticsearch.session.service;
import com.arkime.elasticsearch.session.model.SearchResult;
import com.arkime.elasticsearch.common.SearchRequest;
import com.arkime.elasticsearch.session.model.SessionVO;
import com.arkime.elasticsearch.session.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SessionService {

    private final SessionRepository sessionRepository;

    public SearchResult<SessionVO> searchSessions(SearchRequest request) {
        return sessionRepository.searchSessions(request);
    }

}