package com.arkime.elasticsearch.common;

public enum StatusCode {
    OK("200"), //
    OK_CUSTOM("201"), //
    NOT_FOUND_PAGE("404"), //
    NETWORK_CONNECT_FAIL("599"), //
    SYSTEM_ERROR("500"), //
    UNREGISTERED_KEY("401"), //
    INCORRECT_QUERY_REQUEST("011"), //
    REQUIRED_PARAMETER_ISNOT_PRESENT("012"), //
    REQUEST_COUNT_OVER_LIMIT("010"), //
    REQUEST_QUERY_ERROR("013"), //
    DELETED_KEY("800"), //
    NONE_APPROVED_KEY("801"), //
    DATABASE_CONNECT_FAIL("802"), //
    UNDEFINED_ERROR("999");

    private final String code;

    StatusCode(final String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    public static String getMessage(final StatusCode code) {
        switch (code) {
            case OK:								return "";
            case NOT_FOUND_PAGE:					return "Not found url";
            case NETWORK_CONNECT_FAIL: 				return "Network connection timeout error";
            case SYSTEM_ERROR: 						return "Internal server error";
            case UNREGISTERED_KEY: 					return "Unregistered key";
            case INCORRECT_QUERY_REQUEST: 			return "Incorrect query request";
            case REQUIRED_PARAMETER_ISNOT_PRESENT: return "Required parameter is not present";
            case REQUEST_COUNT_OVER_LIMIT: 			return "Your query request count is over the limit";
            case DELETED_KEY: 						return "Your key is deleted";
            case DATABASE_CONNECT_FAIL: 			return "Database connection timeout error";
            case NONE_APPROVED_KEY:					return "Your key is not approved";
            case REQUEST_QUERY_ERROR:				return "incorrect request";
            case UNDEFINED_ERROR: 					return "Undefined error occured";
            default: 								return "Undefined messages";
        }
    }

}
