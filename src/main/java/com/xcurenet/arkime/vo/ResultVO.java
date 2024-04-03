package com.xcurenet.arkime.vo;

import com.xcurenet.arkime.vo.session.SessionVO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ResultVO<T> {

    private List<T> data;
    private long recordsTotal;
    private long recordsFiltered;
    private Object searchAfter;

    public ResultVO(List<T> results, long recordsTotal, long recordsFiltered) {
        this.data  = results;
        this.recordsTotal = recordsTotal;
        this.recordsFiltered = recordsFiltered;
    }
}
