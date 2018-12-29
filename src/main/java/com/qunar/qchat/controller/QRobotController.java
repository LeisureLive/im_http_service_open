package com.qunar.qchat.controller;

import com.alibaba.fastjson.JSON;
import com.qunar.qchat.constants.QChatConstant;
import com.qunar.qchat.dao.IRobotInfoDao;
import com.qunar.qchat.dao.IRobotPubSubDao;
import com.qunar.qchat.dao.model.RobotInfoModel;
import com.qunar.qchat.dao.model.RobotPubSubModel;
import com.qunar.qchat.model.JsonResult;
import com.qunar.qchat.model.request.*;
import com.qunar.qchat.model.result.GetRecommendRobotResult;
import com.qunar.qchat.model.result.GetRobotInfoResult;
import com.qunar.qchat.model.result.SearchRobotResult;
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
import java.util.stream.Collectors;

/**
 * @auth dongzd.zhang
 * @Date 2018/11/8 13:47
 */
@RestController
@RequestMapping("/newapi/robot/")
public class QRobotController {

    private static final Logger LOGGER = LoggerFactory.getLogger(QRobotController.class);

    @Autowired
    private IRobotInfoDao robotInfoDao;
    @Autowired
    private IRobotPubSubDao robotPubSubDao;

    /**
     * 获取订阅公众号信息.
     * @param requests List<GetRobotRequest>
     * @return  JsonResult<?>
     * */
    @RequestMapping(value = "/get_robot_info.qunar", method = RequestMethod.POST)
    public JsonResult<?> getRobotInfo(@RequestBody List<GetRobotRequest> requests) {
        try {

            if (Objects.isNull(requests) ||
                    CollectionUtils.isEmpty(requests)) {
                return JsonResultUtils.fail(1, QChatConstant.PARAMETER_ERROR);
            }


            List<GetRobotInfoResult> infoModels = new ArrayList<>();
            for(GetRobotRequest request: requests) {

                if(StringUtils.isBlank(request.getRobot_name()) ||
                        StringUtils.isBlank(request.getVersion())) {
                    return JsonResultUtils.fail(1, QChatConstant.PARAMETER_ERROR);
                }

                RobotInfoModel model = robotInfoDao.selectRobotInfoByEnnameAndVersion(request.getRobot_name(),
                        Integer.valueOf(request.getVersion()));
                GetRobotInfoResult result = GetRobotInfoResult.builder()
                                                .rbt_name(model.getEnName())
                                                .rbt_body(model.getRbtBody())
                                                .rbt_ver(String.valueOf(model.getRbtVersion()))
                                                .build();
                infoModels.add(result);

            }

            return JsonResultUtils.success(infoModels);
        }catch (Exception ex) {
            LOGGER.error("catch error : {}", ExceptionUtils.getStackTrace(ex));
            return JsonResultUtils.fail(0, QChatConstant.SERVER_ERROR);
        }
    }


    /**
     * 获取推荐公众号列表.
     * @param request GetRecommendRobotRequest
     * @return JsonResult<?>
     * */
    @RequestMapping(value = "/get_recommend_robot_info.qunar", method = RequestMethod.POST)
    public JsonResult<?> getCommendRobotInfo(@RequestBody GetRecommendRobotRequest request) {
        try{
            if(Objects.isNull(request) ||
                   request.getType() == -1) {
               return JsonResultUtils.fail(1, QChatConstant.PARAMETER_ERROR);
            }

            List<RobotInfoModel> robotInfoModels = robotInfoDao.selectRobotInfoByType(request.getType());
            if(CollectionUtils.isEmpty(robotInfoModels)) {
                return JsonResultUtils.success(Collections.EMPTY_LIST);
            }

            List<GetRecommendRobotResult> result = robotInfoModels.stream().map(model -> {
               GetRecommendRobotResult robot = new GetRecommendRobotResult();
                robot.setRbt_name(model.getEnName());
                robot.setRbt_body(model.getRbtBody());
                robot.setRbt_ver(String.valueOf(model.getRbtVersion()));
                return robot;
            }).collect(Collectors.toList());

            return JsonResultUtils.success(result);
        }catch (Exception ex) {
            LOGGER.error("catch error : {}", ExceptionUtils.getStackTrace(ex));
            return JsonResultUtils.fail(0, QChatConstant.SERVER_ERROR);
        }

    }


    /**
     * 注册公众号.
     * @param request RegistRobotRequest
     * @return JsonResult<?>
     * */
    @RequestMapping(value = "/regist_robot.qunar", method = RequestMethod.POST)
    public JsonResult<?> registRobot(@RequestBody RegistRobotRequest request) {
        try {
            if (Objects.isNull(request) || !request.isRequestValid()) {
                return JsonResultUtils.fail(1, QChatConstant.PARAMETER_ERROR);
            }

            Integer checkCount = robotInfoDao.selectRobotCountByEnName(request.getRobotEnName());
            if(checkCount > 0) {
                return JsonResultUtils.fail(1, "该机器人已存在");
            }

            RobotInfoModel robotInfoModel = new RobotInfoModel();

            robotInfoModel.setEnName(request.getRobotEnName());
            robotInfoModel.setCnName(StringUtils.defaultString(request.getRobotCnName() ));
            robotInfoModel.setRequestUrl(StringUtils.defaultString(request.getRequestUrl()));
            robotInfoModel.setRbtDesc(StringUtils.defaultString(request.getRobotDesc()));
            robotInfoModel.setRbtVersion(1);
            robotInfoModel.setPassword(StringUtils.defaultString(request.getPassword()));

            Map<String, String> body = new HashMap<>();
            body.put("robotEnName",request.getRobotEnName());
            body.put("robotCnName", StringUtils.defaultString(request.getRobotCnName()));
            body.put("robotDesc", StringUtils.defaultString(request.getRobotDesc()));

            String jsonBody = JSON.toJSONString(body);
            robotInfoModel.setRbtBody(jsonBody);

            robotInfoDao.insertRobotInfo(robotInfoModel);
            return JsonResultUtils.success();
        }catch (Exception ex) {
            LOGGER.error("catch error : {}", ExceptionUtils.getStackTrace(ex));
            return JsonResultUtils.fail(0, QChatConstant.SERVER_ERROR);
        }
    }

    /**
     * 根据关键字搜索公众号信息.
     * @param request SearchRobotRequest
     * @return JsonResult<?>
     * */
    @RequestMapping(value = "/search_robot.qunar", method = RequestMethod.POST)
    public JsonResult<?> searchRobot(@RequestBody SearchRobotRequest request) {
        try {
            if(Objects.isNull(request) ||
                    StringUtils.isBlank(request.getKeyword())) {
                return JsonResultUtils.fail(1, QChatConstant.PARAMETER_ERROR);
            }

            List<RobotInfoModel> models = robotInfoDao.selectRobotInfoByKeyword(request.getKeyword());

            if(CollectionUtils.isEmpty(models)) {
                return JsonResultUtils.success(Collections.EMPTY_LIST);
            }

            List<SearchRobotResult> result = models.stream().map(model -> {
                SearchRobotResult robot = new SearchRobotResult();
                robot.setRbt_name(model.getEnName());
                robot.setRbt_body(model.getRbtBody());
                robot.setRbt_ver(String.valueOf(model.getRbtVersion()));
                return robot;
            }).collect(Collectors.toList());

            return JsonResultUtils.success(result);
        }catch (Exception ex) {
            LOGGER.error("catch error : {}", ExceptionUtils.getStackTrace(ex));
            return JsonResultUtils.fail(0, QChatConstant.SERVER_ERROR);
        }
    }


    /**
     * 新增、查询、删除用户订阅信息.
     * @param request RobotSubRequest
     * @return  JsonResult<?>
     * */
    @RequestMapping(value = "/user_robot_pubsub.qunar", method = RequestMethod.POST)
    public JsonResult<?> robotSubscription(@RequestBody RobotSubRequest request) {
        try {
            if (Objects.isNull(request) ||
                    StringUtils.isBlank(request.getMethod())) {
                return JsonResultUtils.fail(1, QChatConstant.PARAMETER_ERROR);
            }

            if ("add".equals(request.getMethod())) {
                return addRobotSubscription(request);
            } else if ("del".equals(request.getMethod())) {
                return delRobotSubscription(request);
            } else if ("get".equals(request.getMethod())){
                return getUserSubscriptions(request);
            }

            return JsonResultUtils.fail(1, QChatConstant.PARAMETER_ERROR);
        }catch (Exception ex) {
            LOGGER.error("catch error : {}", ExceptionUtils.getStackTrace(ex));
            return JsonResultUtils.fail(0, QChatConstant.SERVER_ERROR);
        }
    }


    private JsonResult<?> addRobotSubscription(RobotSubRequest request) {
        if(StringUtils.isBlank(request.getUser()) ||
                        StringUtils.isBlank(request.getRbt())) {
            return JsonResultUtils.fail(1, "参数错误");
        }

        Integer robotCount = robotInfoDao.selectRobotCountByEnName(request.getRbt());
        if(robotCount == 0) {
            return JsonResultUtils.fail(1,"待订阅的机器人不存在");
        }

        Integer subscriptionCount = robotPubSubDao.selectPubSubCountByKey(request.getUser(),
                request.getRbt(), request.getUhost());
        if(subscriptionCount > 0) {
            return JsonResultUtils.fail(1,"该订阅已存在");
        }

        RobotPubSubModel model = new RobotPubSubModel();
        model.setUserName(request.getUser());
        model.setRbtName(request.getRbt());
        model.setUserHost(request.getUhost());
        model.setRbtHost(request.getRhost());

        Integer result = robotPubSubDao.insertPubSub(model);
        if(result == 1) {
            return JsonResultUtils.success();
        }else {
            return JsonResultUtils.fail(1,"添加订阅失败");
        }
    }


    private JsonResult<?> delRobotSubscription(RobotSubRequest request) {


        if(StringUtils.isBlank(request.getRbt()) ||
                    StringUtils.isBlank(request.getUser())) {
            return JsonResultUtils.fail(1, "参数错误");
        }

        RobotPubSubModel model = new RobotPubSubModel();
        model.setUserName(request.getUser());
        model.setRbtName(request.getRbt());
        model.setUserHost(request.getUhost());

        Integer result = robotPubSubDao.deletePubSub(model);
        if(result == 1) {
            return JsonResultUtils.success();
        }else {
            return JsonResultUtils.fail(1,"待删除数据不存在");
        }
    }


    private JsonResult<?> getUserSubscriptions(RobotSubRequest request) {

        if(StringUtils.isBlank(request.getUser())) {
            return JsonResultUtils.fail(1, "参数错误");
        }

        List<RobotPubSubModel> models = robotPubSubDao.selectAllUserPubSub(request.getUser(), request.getUhost());
        if(CollectionUtils.isEmpty(models)) {
            models = Collections.EMPTY_LIST;
        }
        return JsonResultUtils.success(models.stream().map(model ->
                        model.getRbtName()).collect(Collectors.toList()));
    }

}
