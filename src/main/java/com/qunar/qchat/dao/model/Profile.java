package com.qunar.qchat.dao.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Profile {
    private String username;
    private String host;
    private int version;
    private String mood;
    private String url;
}
