package com.arkime.elasticsearch.controller;

import com.arkime.elasticsearch.model.SessionSearchRequest;
import com.arkime.elasticsearch.service.SessionService;
import com.arkime.elasticsearch.model.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Description;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/arkime")
public class SessionController {

    @Resource(name = "sessionService")
    private final SessionService sessionService;

    @Description("arkime 세션 목록 검색")
    @PostMapping("/sessions")
    public ResponseEntity<List<Session>> searchSessions(@RequestBody SessionSearchRequest request) {

        List<Session> sessions = sessionService.findSessionsByDynamicConditions(
                request.getExpression(),
                request.getFields(),
                request.getLength(),
                request.getSorts(),
                request.getStartTime(),
                request.getStopTime()
        );
        return ResponseEntity.ok(sessions);
    }

}
