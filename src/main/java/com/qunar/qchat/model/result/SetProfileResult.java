package com.qunar.qchat.model.result;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * @auth dongzd.zhang
 * @Date 2018/11/17 14:43
 */
@Data
@Builder
@ToString
public class SetProfileResult {

    private String user;
    private String domain;
    private String version;
    private String mood;
    private String url;
}
