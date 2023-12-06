package com.arkime.elasticsearch.common.response;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class StatusResponse {

    private final boolean success;
    private final StatusCode code;
    private final Object data;
    private final long recordsTotal;
    private String message;

    public StatusResponse(final StatusCode rspCode) {
        this.code = rspCode;
        this.data = new JSONObject();
        this.success = this.code == StatusCode.OK;
        this.recordsTotal = 0;
    }

    public StatusResponse(final StatusCode rspCode, final String data) {
        this.code = rspCode;
        this.data = data;
        this.success = this.code == StatusCode.OK;
        this.recordsTotal = 0;
    }

    public StatusResponse(final StatusCode rspCode, final boolean data) {
        this.code = rspCode;
        this.data = data;
        this.success = this.code == StatusCode.OK;
        this.recordsTotal = 0;
    }

    public <T> StatusResponse(final StatusCode rspCode, final T data) {
        this(rspCode, data, 0);
    }

    public <T> StatusResponse(final StatusCode rspCode, final T data, final long recordsTotal) {
        this.code = rspCode;
        this.data = data;
        this.success = this.code == StatusCode.OK;
        this.recordsTotal = recordsTotal;
    }

    public <T> StatusResponse(final StatusCode rspCode, final JSONArray data) {
        this.code = rspCode;
        this.data = data;
        this.success = this.code == StatusCode.OK;
        this.recordsTotal = 0;
    }

    public <T> StatusResponse(final StatusCode rspCode, final List<T> data) {
        this(rspCode, data, 0);
    }

    public <T> StatusResponse(final StatusCode rspCode, final List<T> data, final long recordsTotal) {
        this.code = rspCode;
        this.data = data;
        this.success = this.code == StatusCode.OK;
        this.recordsTotal = recordsTotal;
    }

    public StatusResponse setMessage(String message){
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
        if (this.code == StatusCode.OK_CUSTOM) return this.message;
        else return StatusCode.getMessage(this.code);
    }

    public long getrecordsTotal() {
        return this.recordsTotal;
    }

    public StatusResponse status(int code, HttpServletResponse response) {
        response.setStatus(code);
        return this;
    }


}
