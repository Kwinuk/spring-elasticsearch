package com.arkime.elasticsearch.session.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SearchResult<T> {

    private List<T> data;
    private long recordsTotal;
    private long recordsFiltered;

}
