package com.arkime.elasticsearch.session.controller;

import com.arkime.elasticsearch.common.SearchRequest;
import com.arkime.elasticsearch.session.model.SearchResult;
import com.arkime.elasticsearch.session.service.SessionService;
import com.arkime.elasticsearch.session.model.SessionVO;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Description;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/arkime")
public class SessionController {

    private final SessionService sessionService;

    @Description("arkime 세션 목록 검색")
    @PostMapping("/sessions")
    public ResponseEntity<SearchResult<SessionVO>> searchSessions(@RequestBody SearchRequest request) {
        SearchResult<SessionVO> result = sessionService.searchSessions(request);

        return ResponseEntity.ok(result);
    }

}
