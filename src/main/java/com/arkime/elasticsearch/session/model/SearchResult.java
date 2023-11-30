package com.arkime.elasticsearch.session.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchResult<T> {

    private List<T> data;
    private long recordsTotal;
    private long recordsFiltered;

}
