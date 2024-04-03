package com.xcurenet.arkime.vo.file;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import nonapi.io.github.classgraph.json.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Getter
@Builder
@AllArgsConstructor
@Document(indexName = "arkime_files_v*")   // 인덱스
@JsonIgnoreProperties(ignoreUnknown=true)   // 포함되지 않은 속성 무시
@JsonInclude(JsonInclude.Include.NON_NULL)  // null 인 속성 무시
public class FileVO {

    @Id
    private String _id;
    private String _index;
    private int num;
    private String name;
    private long first;
    private String node;
    private int locked;
    private String packetPosEncoding;
    private String uncompressedBits;
    private int packetsSize;
    private int filesize;
    private int packets;

}