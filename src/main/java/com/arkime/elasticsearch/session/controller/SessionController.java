package com.arkime.elasticsearch.session.controller;

import com.arkime.elasticsearch.common.SearchRequest;
import com.arkime.elasticsearch.common.StatusCode;
import com.arkime.elasticsearch.common.StatusResponse;
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
    public ResponseEntity<StatusResponse> searchSessions(@RequestBody SearchRequest request) {

        SearchResult<SessionVO> data = sessionService.searchSessions(
                request.getExpression(),
                request.getIncludeFields(),
                request.getExcludeFields(),
                request.getSortInfo(),
                request.getStartTime(),
                request.getStopTime(),
                request.getBounding(),
                request.getOffset(),
                request.getLimit()
        );
        StatusResponse result = new StatusResponse(StatusCode.OK, data.getData(), data.getRecordsTotal());

        return ResponseEntity.ok(result);
    }

}
