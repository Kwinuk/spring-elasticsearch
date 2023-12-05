package com.arkime.elasticsearch.sessions.controller;

import com.arkime.common.request.Request;
import com.arkime.common.response.StatusCode;
import com.arkime.common.response.StatusResponse;
import com.arkime.elasticsearch.sessions.vo.SessionListVO;
import com.arkime.elasticsearch.sessions.service.SessionService;
import com.arkime.elasticsearch.sessions.vo.SessionVO;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Description;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/arkime")
public class SessionController {

    private final SessionService sessionService;

    @Description("JWT Auth Bearer 인증 요청")
    @ApiOperation(value = "JWT Auth Bearer 인증 요청")
    @GetMapping
    public ResponseEntity<Object> authHeaderChecker(HttpServletRequest request) {
        String authorizationHeaderValue = request.getHeader("Authorization");
        Map<String, String> response = new HashMap<>(){{
            put("Authorization", authorizationHeaderValue);
        }};
        return ResponseEntity.ok(response);
    }

    @Description("arkime 세션 목록 검색 API")
    @ApiOperation(value = "Arkime 세션 목록 검색 API")
    @PostMapping("/sessions")
    public ResponseEntity<StatusResponse> searchSessions(@RequestBody Request request) {

        SessionListVO<SessionVO> data = sessionService.searchSessions(
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
