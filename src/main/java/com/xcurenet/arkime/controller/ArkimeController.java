package com.xcurenet.arkime.controller;

import com.xcurenet.arkime.service.PacketService;
import com.xcurenet.arkime.service.SessionService;
import com.xcurenet.arkime.vo.RequestVO;
import com.xcurenet.common.vo.XcnRspCode;
import com.xcurenet.common.vo.XcnResponseVO;
import com.xcurenet.arkime.vo.ResultVO;
import com.xcurenet.arkime.vo.session.SessionVO;
import io.swagger.annotations.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.context.annotation.Description;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api(tags = "Sessions")
@RequestMapping("/api")
@RestController
public class ArkimeController {
    private final SessionService sessionService;
    private final PacketService packetService;

    public ArkimeController(SessionService sessionService, PacketService packetService) {
        this.sessionService = sessionService;
        this.packetService = packetService;
    }

    @Description("arkime 세션 목록 검색 API")
    @ApiOperation(value = "세션 목록 검색", notes = "arkime_sessions* 인덱스에 색인되어 있는 데이터를 검색합니다.", response = XcnResponseVO.class)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 성공"),
            @ApiResponse(responseCode = "201", description = "성공적으로 요청되었으며 서버가 새 리소스를 작성"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "서버에 요청이 전달되었으나 권한 때문에 거절"),
            @ApiResponse(responseCode = "404", description = "요청 URL 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "인터넷 서버 오류")})
    @PostMapping("/sessions")
    public ResponseEntity<XcnResponseVO> getSessions(@RequestBody RequestVO body) {
        ResultVO<SessionVO> data = sessionService.search(
                body.getExpression(),
                body.getIncludeFields(),
                body.getExcludeFields(),
                body.getSortInfo(),
                body.getStartTime(),
                body.getStopTime(),
                body.getBounding(),
                body.getOffset(),
                body.getLimit()
        );

        XcnResponseVO result = new XcnResponseVO(XcnRspCode.OK, data.getData(), data.getRecordsTotal(), data.getRecordsFiltered());

        return ResponseEntity.ok(result);
    }

    @Description("arkime 세션 목록 검색 API")
    @ApiOperation(value = "더 많은 세션 목록 검색", notes = "arkime_sessions* 인덱스에 색인되어 있는 10000개 이상의 데이터를 검색합니다.", response = XcnResponseVO.class)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 성공"),
            @ApiResponse(responseCode = "201", description = "성공적으로 요청되었으며 서버가 새 리소스를 작성"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "서버에 요청이 전달되었으나 권한 때문에 거절"),
            @ApiResponse(responseCode = "404", description = "요청 URL 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "인터넷 서버 오류")})
    @PostMapping("/sessions/searchafter")
    public ResponseEntity<XcnResponseVO> searchAfterSessions(@RequestBody RequestVO body) {
        ResultVO<SessionVO> data = sessionService.searchAfter(
                body.getExpression(),
                body.getIncludeFields(),
                body.getExcludeFields(),
                body.getSortInfo(),
                body.getStartTime(),
                body.getStopTime(),
                body.getBounding(),
                body.getOffset(),
                body.getLimit()
        );
        XcnResponseVO result = new XcnResponseVO(XcnRspCode.OK, data.getData(), data.getRecordsTotal(), data.getRecordsFiltered(), data.getSearchAfter());

        return ResponseEntity.ok(result);
    }

    @PostMapping("/sessions/batch")
    public ResponseEntity<XcnResponseVO> getSessionsBatch(@RequestBody RequestVO body) {
        ResultVO<SessionVO> data = sessionService.searchSessionBatch(
                body.getExpression(),
                body.getStartTime(),
                body.getStopTime(),
                body.getBounding(),
                body.getLimit()
        );
        XcnResponseVO result = new XcnResponseVO(XcnRspCode.OK, data.getData(), data.getRecordsTotal(), data.getRecordsFiltered(), data.getSearchAfter());

        return ResponseEntity.ok(result);
    }

//    @Description("arkime PCAP FILES 목록 검색 API")
//    @ApiOperation(value = "더 많은 세션 목록 검색", notes = "arkime_sessions* 인덱스에 색인되어 있는 10000개 이상의 데이터를 검색합니다.", response = XcnResponseVO.class)
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "검색 성공"),
//            @ApiResponse(responseCode = "201", description = "성공적으로 요청되었으며 서버가 새 리소스를 작성"),
//            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
//            @ApiResponse(responseCode = "401", description = "인증 실패"),
//            @ApiResponse(responseCode = "403", description = "서버에 요청이 전달되었으나 권한 때문에 거절"),
//            @ApiResponse(responseCode = "404", description = "요청 URL 찾을 수 없음"),
//            @ApiResponse(responseCode = "500", description = "인터넷 서버 오류")})
//    @RequestMapping("/files")
//    public ResponseEntity<XcnResponseVO> getFiles(@RequestBody RequestVO body) {
//        ResultVO<SessionVO> data = sessionService.searchAfter(
//                body.getExpression(),
//                body.getIncludeFields(),
//                body.getExcludeFields(),
//                body.getSortInfo(),
//                body.getStartTime(),
//                body.getStopTime(),
//                body.getBounding(),
//                body.getOffset(),
//                body.getLimit()
//        );
//        XcnResponseVO result = new XcnResponseVO(XcnRspCode.OK, data.getData(), data.getRecordsTotal());
//
//        return ResponseEntity.ok(result);
//    }


}
