package com.arkime.common;

import lombok.Getter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class ArkimeFields {

    // 문서 ID
    public static final String FIELD_ID = "_id";
    public static final String FIELD_DOC = "_doc";

    // 아이피
    public static final String FIELD_SRC_IP = "source.ip";
    public static final String FIELD_DST_IP = "destination.ip";
    public static final String FIELD_ALL_IP = "ipall";

    // 포트
    public static final String FIELD_SRC_PORT = "source.port";
    public static final String FIELD_DST_PORT = "destination.port";
    public static final String FIELD_ALL_PORT = "portall";

    // 바이트 합계
    public static final String FIELD_SRC_BYTES = "source.bytes";
    public static final String FIELD_DST_BYTES = "destination.bytes";
    public static final String FIELD_SERVER_BYTES = "server.bytes";
    public static final String FIELD_CLIENT_BYTES = "client.bytes";
    public static final String FIELD_NET_BYTES = "network.bytes";
    public static final String FIELD_TOTAL_BYTES = "totDataBytes";

    // 패킷 합계
    public static final String FIELD_SRC_PACKETS = "source.packets";
    public static final String FIELD_DST_PACKETS = "destination.packets";
    public static final String FIELD_PACKET_POS = "packetPos";
    public static final String FIELD_TOTAL_PACKETS = "totPackets";
    public static final String FIELD_NET_PACKETS = "network.packets";

    // 타임 스탬프
    public static final String FIELD_START_TIME = "firstPacket";
    public static final String FIELD_STOP_TIME = "lastPacket";
    public static final String FIELD_TIMESTAMP = "@timestamp";

    // 프로토콜
    public static final String FIELD_PROTOCOL = "protocol";
    public static final String FIELD_PROTOCOL_CNT = "protocolCnt";
    public static final String FIELD_IP_PROTOCOL = "ipProtocol";

    // ETC
    public static final String FIELD_LENGTH = "length";
    public static final String FIELD_USER = "user";
    public static final String FIELD_FILE_NAME = "fileand";
    public static final String FIELD_COMMUNITY_ID = "communityId";
    public static final String FIELD_ALL_HOST = "hostall";
    public static final String FIELD_NODE = "node";
    public static final String FIELD_FILE_ID = "fileId";
    public static final String FIELD_SRC_DSCP_CNT = "srcDscpCnt";
    public static final String FIELD_SRC_DSCP = "srcDscp";
    public static final String FIELD_SRC_OUI_CNT = "srcOuiCnt";
    public static final String FIELD_SRC_OUI = "srcOui";
    public static final String FIELD_SRC_TAGS_CNT = "tagsCnt";
    public static final String FIELD_SRC_TAGS = "tags";

    // HTTP
    public static final String FIELD_HTTP_BODY_MAGIC = "http.bodyMagic";
    public static final String FIELD_HTTP_HOST = "http.host";
    public static final String FIELD_HTTP_URI = "http.uri";
    public static final String FIELD_HTTP_USER = "http.user";
    public static final String FIELD_HTTP_METHOD = "http.method";
    public static final String FIELD_HTTP_USER_AGENT = "http.useragent";
    public static final String FIELD_HTTP_VERSION = "httpversion";
    public static final String FIELD_HTTP_PATH = "http.path";
    public static final String FIELD_HTTP_STATUS_CODE = "http.statuscode";
    public static final String FIELD_HTTP_CLIENT_VERSION = "http.clientVersion";
    public static final String FIELD_HTTP_RESPONSE_SERVER = "http.response-server";
    public static final String FIELD_HTTP_RESPONSE_CONTENT_TYPE = "http.response-content-type";
    public static final String FIELD_HTTP_RESPONSE_LOCATION = "http.response-location";
    public static final String FIELD_HTTP_RESPONSE_HEADER = "http.responseHeader";
    public static final String FIELD_HTTP_REQUEST_ORIGIN = "http.request-origin";
    public static final String FIELD_HTTP_REQUEST_BODY = "http.requestBody";
    public static final String FIELD_HTTP_REQUEST_HEADER = "http.requestHeader";

    public static String[] defaultFields = new String[]{
            FIELD_ID,
            FIELD_FILE_NAME,
            FIELD_IP_PROTOCOL,
            FIELD_START_TIME,
            FIELD_STOP_TIME,
            FIELD_SRC_IP,
            FIELD_SRC_PORT,
            FIELD_DST_IP,
            FIELD_DST_PORT,
            FIELD_NET_BYTES,
            FIELD_TOTAL_BYTES,
            FIELD_NET_BYTES,
            FIELD_NODE
    };

    public static String[] validateFields = generateAllFields();

    private static String[] generateAllFields() {
        List<String> allFieldsList = new ArrayList<>();

        Field[] fields = ArkimeFields.class.getDeclaredFields();
        for (Field field : fields) {
            if (field.getType() == String.class) {
                try {
                    String fieldValue = (String) field.get(null);
                    allFieldsList.add(fieldValue);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return allFieldsList.toArray(new String[0]);
    }

    public static String[] getAllFields() {
        return Arrays.stream(ArkimeFields.class.getDeclaredFields())
                .filter(field -> java.lang.reflect.Modifier.isPublic(field.getModifiers())
                        && java.lang.reflect.Modifier.isStatic(field.getModifiers())
                        && java.lang.reflect.Modifier.isFinal(field.getModifiers())
                        && field.getType() == String.class)
                .map(field -> {
                    try {
                        return (String) field.get(null);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toArray(String[]::new);
    }
}
