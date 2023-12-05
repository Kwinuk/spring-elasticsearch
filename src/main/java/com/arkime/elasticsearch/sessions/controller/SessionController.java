package com.arkime.elasticsearch.sessions.controller;

import com.arkime.common.request.requestBody;
import com.arkime.common.response.StatusCode;
import com.arkime.common.response.StatusResponse;
import com.arkime.elasticsearch.sessions.vo.SessionListVO;
import com.arkime.elasticsearch.sessions.service.SessionService;
import com.arkime.elasticsearch.sessions.vo.SessionVO;
import io.swagger.annotations.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Description;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Api(tags = "Sessions")
@RequiredArgsConstructor
@RequestMapping("/arkime")
@RestController
public class SessionController {

    private final SessionService sessionService;

    @Description("JWT Auth Bearer 인증 API")
    @ApiOperation(value = "JWT Auth Bearer 인증")
    @GetMapping
    public ResponseEntity<Object> authHeaderChecker(HttpServletRequest request) {
        String authorizationHeaderValue = request.getHeader("Authorization");
        Map<String, String> response = new HashMap<>(){{
            put("Authorization", authorizationHeaderValue);
        }};
        return ResponseEntity.ok(response);
    }


    @Description("arkime 세션 목록 검색 API")
    @ApiOperation(value = "세션 목록 검색", notes = "arkime_sessions* 인덱스에 색인되어 있는 데이터를 검색합니다.", response = StatusResponse.class)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 성공"),
            @ApiResponse(responseCode = "201", description = "성공적으로 요청되었으며 서버가 새 리소스를 작성"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "서버에 요청이 전달되었으나 권한 때문에 거절"),
            @ApiResponse(responseCode = "404", description = "요청 URL 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "인터넷 서버 오류")})
    @PostMapping("/sessions")
    public ResponseEntity<StatusResponse> searchSessions(@RequestBody requestBody request) {

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
