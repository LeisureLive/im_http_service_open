package com.qunar.qchat.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.qunar.qchat.constants.Config;
import com.qunar.qchat.utils.HttpClientUtils;
import com.qunar.qchat.utils.JacksonUtils;
import com.qunar.qchat.utils.JsonResultUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * QtalkUpdateStructure
 *
 * @author binz.zhang
 * @date 2018/11/26
 */
@Controller
@RequestMapping("/newapi/update/")
public class QtalkUpdateStructure {

    @ResponseBody
    @RequestMapping(value = "/getUpdateUsers.qunar", method = RequestMethod.POST)
    public String updateStructure(HttpServletRequest request, @RequestBody String param) {
        Cookie[] cookies = request.getCookies();
        if(null==cookies||cookies.length==0){
            return JacksonUtils.obj2String(JsonResultUtils.fail(1, "cookie无效"));
        }
        String ckey = null;
        for (Cookie c : cookies) {
            if (c.getName().equals("q_ckey")) {
                ckey = c.getValue();
            }
        }
        if(Strings.isNullOrEmpty(ckey)){
            return JacksonUtils.obj2String(JsonResultUtils.fail(1, "cookie无效"));
        }
        JSONObject receivedParam = JSON.parseObject(param);
        Integer clientUserInfoVersion = (Integer) receivedParam.get("version");
        if (clientUserInfoVersion == null || clientUserInfoVersion < 0) {
            return JacksonUtils.obj2String(JsonResultUtils.fail(1, "无效version"));
        }
        String result = HttpClientUtils.postJson(Config.getProperty("qtalk_update_url"), param,ckey);
        return result;
    }
}
