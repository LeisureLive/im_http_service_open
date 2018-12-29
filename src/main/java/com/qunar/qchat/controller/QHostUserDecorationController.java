package com.qunar.qchat.controller;

import com.qunar.qchat.dao.IHostUserDecorationDao;
import com.qunar.qchat.dao.model.HostUserDecorationModel;
import com.qunar.qchat.model.JsonResult;
import com.qunar.qchat.model.request.GetHostUserDecorationRequest;
import com.qunar.qchat.model.result.GetHostUserDecorationResponse;
import com.qunar.qchat.utils.JsonResultUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 用户勋章
 * @auth dongzd.zhang
 * @Date 2018/12/3 20:37
 */
@RestController
@RequestMapping("/newapi/user")
public class QHostUserDecorationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(QHostUserDecorationController.class);

    @Autowired
    private IHostUserDecorationDao hostUserDecorationDao;


    @RequestMapping(value = "/get_user_decoration.qunar", method = RequestMethod.POST)
    public JsonResult<?> getUserDecorations(@RequestBody GetHostUserDecorationRequest request) {
        try {
            LOGGER.info("request parameter: {}", request.toString());
            if(!request.isRequestValid()) {
                return JsonResultUtils.fail(1, "参数错误");
            }
            List<HostUserDecorationModel> hostUserDecorationModelList =
                    hostUserDecorationDao.selectUserDecorations(request.getUserId(), request.getHost());

            List<GetHostUserDecorationResponse> responseList = new ArrayList<>();
            if(!CollectionUtils.isEmpty(hostUserDecorationModelList)) {
                //GetHostUserDecorationResponse response = new GetHostUserDecorationResponse();
                hostUserDecorationModelList.stream().forEach(model -> {
                    GetHostUserDecorationResponse response = new GetHostUserDecorationResponse();
                    BeanUtils.copyProperties(model, response);
                    response.setUpt(String.valueOf(model.getUpdateTime().getTime()));
                    response.setDesc(model.getUrlDesc());
                    responseList.add(response);
                });
                return JsonResultUtils.success(responseList);
            }
            return JsonResultUtils.success(Collections.EMPTY_LIST);
        }catch (Exception ex) {
            LOGGER.error("catch error {}", ExceptionUtils.getStackTrace(ex));
            return JsonResultUtils.fail(1, "服务器异常");
        }
    }
}
