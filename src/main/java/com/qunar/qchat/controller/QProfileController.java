package com.qunar.qchat.controller;

import com.qunar.qchat.constants.Config;
import com.qunar.qchat.constants.QChatConstant;
import com.qunar.qchat.dao.IProfileDao;
import com.qunar.qchat.dao.model.Profile;
import com.qunar.qchat.model.JsonResult;
import com.qunar.qchat.model.request.GetProfileRequest;
import com.qunar.qchat.model.result.SetProfileResult;
import com.qunar.qchat.utils.JsonResultUtils;
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

;

@RequestMapping("/newapi/profile/")
@RestController
public class QProfileController {
    private static final Logger LOGGER = LoggerFactory.getLogger(QProfileController.class);

    @Autowired
    private IProfileDao iProfileDao;

    /**
     * 获取用户信息.
     * @param request GetProfileRequest
     * @return JsonResult<?>
     * */
    @RequestMapping(value = "/get_profile.qunar", method = RequestMethod.POST)
    public JsonResult<?> getProfile(@RequestBody GetProfileRequest request) {
        try {
            if(StringUtils.isBlank(request.getUser()) ||
                    StringUtils.isBlank(request.getDomain())) {
                return JsonResultUtils.fail(1, QChatConstant.PARAMETER_ERROR);
            }

            List<Profile> profileList = iProfileDao.selectProfileInfo(request.getUser(), request.getDomain(), request.getVersion());

            List<Map<String, Object>> resultList = new ArrayList<>();

            for(Profile profile : profileList) {
                Map<String, Object> map = new HashMap<>();
                // username, host, mood, profile_version as version
                map.put("username", profile.getUsername());
                map.put("host", profile.getHost());
                map.put("mood", profile.getMood());
                map.put("version", profile.getVersion());
                resultList.add(map);
            }

            return JsonResultUtils.success(resultList);
        } catch (Exception e) {
            LOGGER.error("catch error : {}", ExceptionUtils.getStackTrace(e));
            return JsonResultUtils.fail(0, QChatConstant.SERVER_ERROR);
        }
    }

    /**
     * 设置用户信息.
     * @param request GetProfileRequest
     * @return JsonResult<?>
     * */
    @RequestMapping(value = "/set_profile.qunar", method = RequestMethod.POST)
    public JsonResult<?> SetProfile(@RequestBody GetProfileRequest request) {
        try {

            if(StringUtils.isBlank(request.getUser())
                    || StringUtils.isBlank(request.getDomain())) {
                return JsonResultUtils.fail(1, QChatConstant.PARAMETER_ERROR);
            }

            //不支持qchat调用
            if(QChatConstant.ENVIRONMENT_QCHAT.equals(Config.CURRENT_ENV)) {
                return JsonResultUtils.fail(1, "不支持的操作");
            }

            Profile profileList = iProfileDao.selectProfileInfoByUserAndHost(request.getUser(), request.getDomain());

            if(Objects.isNull(profileList)) {
                return JsonResultUtils.fail(1, "user:" + request.getUser() +
                        ", host: " + request.getDomain() +"数据不存在");
            }

            Profile newProfile = iProfileDao.updateProfileInfo(request.getUser(), request.getDomain(),
                                    request.getUrl(), request.getMood());

            SetProfileResult result = SetProfileResult.builder()
                                            .user(newProfile.getUsername())
                                            .domain(newProfile.getHost())
                                            .url(newProfile.getUrl())
                                            .mood(newProfile.getMood())
                                            .version(String.valueOf(newProfile.getVersion())).build();

            return JsonResultUtils.success(result);

        } catch (Exception e) {
            LOGGER.error("catch error : {}", ExceptionUtils.getStackTrace(e));
            return JsonResultUtils.fail(0, QChatConstant.SERVER_ERROR);
        }
    }
}
