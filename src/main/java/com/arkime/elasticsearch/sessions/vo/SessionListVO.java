package com.arkime.elasticsearch.sessions.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SessionListVO<T> {

    private List<T> data;
    private long recordsTotal;
    private long recordsFiltered;

}
