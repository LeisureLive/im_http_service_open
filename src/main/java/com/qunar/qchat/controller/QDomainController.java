package com.qunar.qchat.controller;

import com.alibaba.fastjson.JSON;
import com.qunar.qchat.constants.Config;
import com.qunar.qchat.constants.QChatConstant;
import com.qunar.qchat.dao.IVCardInfoDao;
import com.qunar.qchat.dao.model.VCardInfoModel;
import com.qunar.qchat.model.JsonResult;
import com.qunar.qchat.model.request.GetUserStatusRequest;
import com.qunar.qchat.model.request.GetVCardInfoRequest;
import com.qunar.qchat.model.result.GetVCardInfoResult;
import com.qunar.qchat.model.result.SearchVCardResult;
import com.qunar.qchat.utils.CommonRedisUtil;
import com.qunar.qchat.utils.HttpClientUtils;
import com.qunar.qchat.utils.JsonResultUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @auth dongzd.zhang
 * @Date 2018/10/19 16:05
 */

@RequestMapping("/newapi/domain")
@RestController
public class QDomainController {

    private static final Logger LOGGER = LoggerFactory.getLogger(QDomainController.class);

    @Autowired
    private CommonRedisUtil commonRedisUtil;

    @Autowired
    private IVCardInfoDao vCardInfoDao;

    @RequestMapping(value = "/get_user_status.qunar", method = RequestMethod.POST)
    public JsonResult<?> getUserStatus(@RequestBody GetUserStatusRequest request) {
        //QMonitor.recordOne("get_user_status");
        try {

            if(Objects.isNull(request) ||
                    Objects.isNull(request.getUsers()) ||
                        CollectionUtils.isEmpty(request.getUsers())) {
                return JsonResultUtils.fail(1, "参数错误");
            }

            List<Map<String, Object>> resultData = new ArrayList<>();
            List<Map<String, String>> userStatus = new ArrayList<>();
            Map<String, Object> rowData = new HashMap<>();
            for(String key : request.getUsers()) {

                if(StringUtils.isBlank(key) ||
                        key.indexOf("@") == -1) {
                    return JsonResultUtils.fail(1, "参数错误");
                }

                String status = commonRedisUtil.getUserStatus(key);
                Map<String, String> currentUserStatus = new HashMap<>();
                currentUserStatus.put("u", key);
                currentUserStatus.put("o", status);
                userStatus.add(currentUserStatus);
            }
            rowData.put("ul", userStatus);
            resultData.add(rowData);

            /*if(Objects.isNull(request) || StringUtils.isBlank(request.getDomain())) {
                return JsonResultUtils.fail(1, "参数错误");
            }

            //QTalk验证，QChat不验证
            if (QChatConstant.ENVIRONMENT_QTALK.equals(Config.CURRENT_ENV)) {
                HostInfoModel hostInfo = hostInfoDao.selectHostInfoByHostName(request.getDomain());
                if (Objects.isNull(hostInfo)) {
                    return JsonResultUtils.fail(1, "domain [" + request.getDomain() + "] 不存在");
                }
            }

            List<Map<String, Object>> resultData = new ArrayList<>();
            Map<String, Object> rowData = new HashMap<>();
            rowData.put("domain", request.getDomain());

            List<String> users = request.getUsers();
            List<Map<String, String>> userStatus = new ArrayList<>();
            if (!CollectionUtils.isEmpty(users)) {
                    users.stream().forEach(user -> {
                        String status = CommonRedisUtil.getUserStatus(user, request.getDomain());
                        Map<String, String> currentUserStatus = new HashMap<>();
                        currentUserStatus.put("u", user);
                        currentUserStatus.put("o", status);
                        userStatus.add(currentUserStatus);
                    });
            }

            rowData.put("ul", userStatus);
            resultData.add(rowData);*/

            return JsonResultUtils.success(rowData);
        }catch (Exception ex) {
            LOGGER.error("catch error", ex);
            return JsonResultUtils.fail(0, "服务器异常：" + ExceptionUtils.getStackTrace(ex));
        }
    }



    /**
     * 获取用户信息.
     * @param requests List<GetVCardInfoRequest>
     * @return  JsonResult<?>
     * */
    @RequestMapping(value = "/get_vcard_info.qunar", method = RequestMethod.POST)
    public JsonResult<?> getVCardInfo(@RequestBody List<GetVCardInfoRequest> requests) {

        //LOGGER.info(requests.toString());
        try {
            if (CollectionUtils.isEmpty(requests)) {
                return JsonResultUtils.fail(1, QChatConstant.PARAMETER_ERROR);
            }

            List<Map<String, Object>> finalResult = new ArrayList<>();
            for (GetVCardInfoRequest request : requests) {
                Map<String, Object> map = new HashMap<>();
                String domain = request.getDomain();

                if(StringUtils.isBlank(domain)
                        || QChatConstant.ENVIRONMENT_QCHAT.equals(Config.CURRENT_ENV)) {
                    return JsonResultUtils.fail(1, "不支持当前操作");
                }

                map.put("domain", domain);

                List<GetVCardInfoResult> users = new ArrayList<>();
                List<GetVCardInfoRequest.UserInfo> userInfos = request.getUsers();
                for (GetVCardInfoRequest.UserInfo userInfo : userInfos) {

                    if (StringUtils.isBlank(userInfo.getUser())) {
                        return JsonResultUtils.fail(1, QChatConstant.PARAMETER_ERROR);
                    }

                    Integer count = vCardInfoDao.getCountByUsernameAndHost(userInfo.getUser(), domain);
                    if (count > 0) {
                        VCardInfoModel result = vCardInfoDao.selectByUsernameAndHost(userInfo.getUser(), domain, userInfo.getVersion());
                        GetVCardInfoResult resultBean = new GetVCardInfoResult();
                        resultBean.setType("qunar_emp");
                        resultBean.setLoginName(userInfo.getUser());
                        resultBean.setEmail("");
                        resultBean.setGender(String.valueOf(result.getGender()));
                        resultBean.setNickname(result.getNickname());
                        resultBean.setV(String.valueOf(result.getVersion()));
                        resultBean.setImageurl(Objects.isNull(result.getUrl()) ?
                                getImageUrl(String.valueOf(result.getGender()))
                                : result.getUrl());
                        resultBean.setUid("0");
                        resultBean.setUsername(userInfo.getUser());
                        resultBean.setDomain(domain);
                        resultBean.setCommenturl(QChatConstant.VCARD_COMMON_URL);

                        users.add(resultBean);
                    } else {
                        users.add(new GetVCardInfoResult());
                    }
                }
                map.put("users", users);

                finalResult.add(map);
            }

            return JsonResultUtils.success(finalResult);
        }catch (Exception ex) {
            LOGGER.error("catch error : {}", ExceptionUtils.getStackTrace(ex));
            return JsonResultUtils.fail(0, QChatConstant.SERVER_ERROR);
        }
    }


    @RequestMapping(value = "/search_vcard.qunar", method = RequestMethod.GET)
    public JsonResult<?> searchVCard(String p, String v, String keyword) {

        try {

            if (StringUtils.isBlank(p) ||
                    StringUtils.isBlank(v) ||
                    StringUtils.isBlank(keyword)) {
                return JsonResultUtils.fail(1, "参数错误");
            }

            String getVCardInfoUrl = Config.GET_VCARD_INFO_URL + "?p=" + p + "&v=" + v + "&username=" + keyword;
            String httpResult = HttpClientUtils.get(getVCardInfoUrl);

                Map<?, ?> jsonResult = JSON.parseObject(httpResult, Map.class);
                Object data = jsonResult.get("data");
                if (Objects.isNull(data)) {
                    return JsonResultUtils.success();
                }

                if(StringUtils.isBlank(data.toString())) {
                    return JsonResultUtils.success();
                }

                Map<?, ?> dataResult = JSON.parseObject(data.toString(), Map.class);

                SearchVCardResult result = new SearchVCardResult();
                result.setDomain("ejabhost1");
                result.setNickname(dataResult.get("nickname"));
                result.setUsername(dataResult.get("username"));

                return JsonResultUtils.success(result);
            } catch (Exception ex) {
                LOGGER.error("catch error", ex);
                return JsonResultUtils.fail(0, "服务器异常：" + ExceptionUtils.getStackTrace(ex));
            }



    }

    private String getImageUrl(String gender) {
        if ("2".equals(gender)) {
            return QChatConstant.VCARD_IMAGE_URL_FEMAIL;
        }
        return QChatConstant.VCARD_IMAGE_URL_MAIL;
    }

}
