package com.xcurenet.arkime.vo.session;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@Document(indexName = "arkime_sessions*")   // 인덱스
@JsonIgnoreProperties(ignoreUnknown=true)   // 포함되지 않은 속성 무시
@JsonInclude(JsonInclude.Include.NON_NULL)  // null 인 속성 무시
public class SessionVO {

    @Id
    private String _id;
    private String _index;
    private Long firstPacket;
    private Long lastPacket;
    private Integer length;
    private Integer ipProtocol;
    private TcpFlags tcpflags;
    private Long initRTT;
    private String srcPayload8;
    private String dstPayload8;
    private Long timestamp;
    private Source source;
    private Destination destination;
    private String srcRIR;
    private String dstRIR;
    private Network network;
    private Client client;
    private Server server;
    private Integer totDataBytes;
    private Integer segmentCnt;
    private String node;
    private List<Long> packetPos;
    private List<Integer> fileId;
    private Integer dstDscpCnt;
    private List<Integer> dstDscp;
    private Integer dstOuiCnt;
    private List<String> dstOui;
    private Http http;
    private String protocolCnt;
    private List<String> protocol;
    private Integer srcDscpCnt;
    private List<Integer> srcDscp;
    private Integer srcOuiCnt;
    private List<String> srcOui;
    private Integer tagsCnt;
    private List<String> tags;

    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class TcpFlags {
        private Integer syn;
        private Integer synAck;
        private Integer ack;
        private Integer psh;
        private Integer fin;
        private Integer rst;
        private Integer urg;
        private Integer srcZero;
        private Integer dstZero;
    }

    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Source {
        private String ip;
        private Integer port;
        private Integer bytes;
        private Integer packets;
        private Integer macCnt;
        private List<String> mac;
    }

    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Destination {
        private String ip;
        private Integer port;
        private Integer bytes;
        private Integer packets;
        private Integer macCnt;
        private List<String> mac;
    }

    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Network {
        private Integer packets;
        private Integer bytes;
        private String communityId;
    }

    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Client {
        private Integer bytes;
    }

    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Server {
        private Integer bytes;
    }

    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Http {
        private Integer bodyMagicCnt;
        private List<String> bodyMagic;
        private Integer clientVersionCnt;
        private List<String> clientVersion;
        private Integer cookieKeyCnt;
        private List<String> cookieKey;
        private Integer hostCnt;
        private List<String> host;
        private Integer keyCnt;
        private List<String> key;
        private Integer md5Cnt;
        private List<String> md5;
        private Integer methodCnt;
        private List<String> method;
        private Integer pathCnt;
        private List<String> path;
        private Integer requestRefererCnt;
        private List<String> requestReferer;
        private Integer requestHeaderCnt;
        private List<String> requestHeader;
        private Integer responseContentTypeCnt;
        private List<String> responseContentType;
        private List<String> responseServer;
        private Integer responseHeaderCnt;
        private List<String> responseHeader;
        private Integer serverVersionCnt;
        private List<String> serverVersion;
        private Integer statuscodeCnt;
        private List<Integer> statuscode;
        private Integer uriCnt;
        private List<String> uri;
        private Integer useragentCnt;
        private List<String> useragent;
    }

}