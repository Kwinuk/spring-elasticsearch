package com.xcurenet.common.vo;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class XcnResponseVO {
    private final boolean success;
    private final XcnRspCode code;
    private final Object data;
    private final long recordsTotal;
    private long recordFiltered;
    private String message;
    private Object searchAfter;

    public XcnResponseVO(final XcnRspCode rspCode) {
        this.code = rspCode;
        this.data = new JSONObject();
        this.success = this.code == XcnRspCode.OK;
        this.recordsTotal = 0;
    }

    public XcnResponseVO(final XcnRspCode rspCode, final String data) {
        this.code = rspCode;
        this.data = data;
        this.success = this.code == XcnRspCode.OK;
        this.recordsTotal = 0;
    }

    public XcnResponseVO(final XcnRspCode rspCode, final boolean data) {
        this.code = rspCode;
        this.data = data;
        this.success = this.code == XcnRspCode.OK;
        this.recordsTotal = 0;
    }

    public <T> XcnResponseVO(final XcnRspCode rspCode, final T data) {
        this(rspCode, data, 0);
    }

    public <T> XcnResponseVO(final XcnRspCode rspCode, final T data, final long recordsTotal) {
        this.code = rspCode;
        this.data = data;
        this.success = this.code == XcnRspCode.OK;
        this.recordsTotal = recordsTotal;
    }

    public <T> XcnResponseVO(final XcnRspCode rspCode, final JSONArray data) {
        this.code = rspCode;
        this.data = data;
        this.success = this.code == XcnRspCode.OK;
        this.recordsTotal = 0;
    }

    public <T> XcnResponseVO(final XcnRspCode rspCode, final List<T> data, final long recordsTotal) {
        this.code = rspCode;
        this.data = data;
        this.success = this.code == XcnRspCode.OK;
        this.recordsTotal = recordsTotal;
    }

    public <T> XcnResponseVO(final XcnRspCode rspCode, final List<T> data, final long recordsTotal, long recordFiltered) {
        this.code = rspCode;
        this.data = data;
        this.success = this.code == XcnRspCode.OK;
        this.recordsTotal = recordsTotal;
        this.recordFiltered = recordFiltered;
    }

    public <T> XcnResponseVO(final XcnRspCode rspCode, final List<T> data, final long recordsTotal, long recordFiltered, Object searchAfter) {
        this.code = rspCode;
        this.data = data;
        this.success = this.code == XcnRspCode.OK;
        this.recordsTotal = recordsTotal;
        this.recordFiltered = recordFiltered;
        this.searchAfter = searchAfter;
    }

    public XcnResponseVO setMessage(String message){
        this.message = message;
        return this;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public String getCode() {
        return code.getCode();
    }

    public Object getData() {
        return data;
    }

    public String getMessage() {
        if (this.code == XcnRspCode.OK_CUSTOM) return this.message;
        else return XcnRspCode.getMessage(this.code);
    }

    public long getRecordsTotal() {
        return this.recordsTotal;
    }

    public long getRecordFiltered() {
        return this.recordFiltered;
    }

    public Object getSearchAfter() {
        return this.searchAfter;
    }

    public XcnResponseVO status(int code, HttpServletResponse response) {
        response.setStatus(code);
        return this;
    }


}
