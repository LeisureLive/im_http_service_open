package com.qunar.qchat.controller;

import com.qunar.qchat.dao.IMucInfoDao;
import com.qunar.qchat.dao.model.MucIncrementInfo;
import com.qunar.qchat.dao.model.MucInfoModel;
import com.qunar.qchat.model.JsonResult;
import com.qunar.qchat.model.request.GetIncrementMucsRequest;
import com.qunar.qchat.model.request.GetMucVcardRequest;
import com.qunar.qchat.model.request.UpdateMucNickRequest;
import com.qunar.qchat.model.result.GetMucVcardResult;
import com.qunar.qchat.model.result.UpdateMucNickResult;
import com.qunar.qchat.utils.CookieUtils;
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

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@RequestMapping("/newapi/muc/")
@RestController
public class QMucInfoController {
    private static final Logger LOGGER = LoggerFactory.getLogger(QMucInfoController.class);

    @Autowired
    private IMucInfoDao iMucInfoDao;


    /**
     * 获取新增群列表.
     * @param httpRequest HttpServletRequest
     * @param paramRequest GetIncrementMucsRequest
     * @return  JsonResult<?>
     * */
    @RequestMapping(value = "/get_increment_mucs.qunar", method = RequestMethod.POST)
    public JsonResult<?> getIncrement(HttpServletRequest httpRequest,
                                      @RequestBody GetIncrementMucsRequest paramRequest) {
        try {
            if(Objects.isNull(paramRequest.getT())) {
                return JsonResultUtils.fail(1, "参数错误");
            }

            if(StringUtils.isBlank(paramRequest.getU())) {
                Map<String, Object> cookie = CookieUtils.getUserbyCookie(httpRequest);
                paramRequest.setU(cookie.get("u").toString());
            }
            if(StringUtils.isBlank(paramRequest.getD())) {
                Map<String, Object> cookie = CookieUtils.getUserbyCookie(httpRequest);
                paramRequest.setD(cookie.get("d").toString());
            }
            List<MucIncrementInfo> mucIncrementInfoList = iMucInfoDao.selectMucIncrementInfo(paramRequest.getU(), paramRequest.getD(), paramRequest.getT());

            List<Map<String, Object>> result = new ArrayList<>();
            mucIncrementInfoList.stream().forEach(item -> {
                Map<String, Object> map = new HashMap<>();
                map.put("M", item.getMuc_name());
                map.put("D", item.getDomain());
                map.put("T", item.getT());
                map.put("F", item.getRegisted_flag());
                result.add(map);
            });

            return JsonResultUtils.success(result);
        } catch (Exception e) {
            LOGGER.error("catch error : {}", ExceptionUtils.getStackTrace(e));
            return JsonResultUtils.fail(0, "服务器操作异常");
        }
    }

    /**
     * 设置群信息.
     * @param requests List<UpdateMucNickRequest>
     * @return JsonResult<?>
     * */
    @RequestMapping(value = "/set_muc_vcard.qunar", method = RequestMethod.POST)
    public JsonResult<?> updateMucNick(@RequestBody List<UpdateMucNickRequest> requests) {
        try{
            // 校验参数
            if (CollectionUtils.isEmpty(requests)) {
                return JsonResultUtils.fail(1, "参数错误");
            }

            for(UpdateMucNickRequest request : requests) {
                if(!request.isRequestValid()) {
                    return JsonResultUtils.fail(1, "参数错误");
                }
            }


            List<MucInfoModel> mucInfoModels = iMucInfoDao.selectMucInfoByIds(requests.stream()
                     .map(request -> request.getMuc_name()).collect(Collectors.toList()));
            if(CollectionUtils.isEmpty(mucInfoModels) || mucInfoModels.size() != requests.size()) {
                return JsonResultUtils.fail(1, "群不存在");
            }

            List<UpdateMucNickResult> resultList = new ArrayList<>();
            for(UpdateMucNickRequest request : requests) {
                MucInfoModel parameter = new MucInfoModel();
                parameter.setMucName(request.getMuc_name());
                parameter.setShowName(request.getNick());
                parameter.setMucTitle(request.getTitle());
                parameter.setMucDesc(request.getDesc());
                MucInfoModel newMucInfo = iMucInfoDao.updateMucInfo(parameter);

                UpdateMucNickResult result = new UpdateMucNickResult();
                if (!Objects.isNull(result)) {
                    result.setMuc_name(newMucInfo.getMucName());
                    result.setVersion(newMucInfo.getVersion());
                    result.setShow_name(newMucInfo.getShowName());
                    result.setMuc_title(newMucInfo.getMucTitle());
                    result.setMuc_desc(newMucInfo.getMucDesc());
                }
                resultList.add(result);
            }
            return JsonResultUtils.success(resultList);
        }catch (Exception ex) {
            LOGGER.error("catch error: {}", ExceptionUtils.getStackTrace(ex));
            return JsonResultUtils.fail(0, "服务器操作异常");
        }
    }

    /**
     * 获取群信息.
     * @param request List<GetMucVcardRequest>
     * @return  JsonResult<?>
     * */
    @RequestMapping(value = "/get_muc_vcard.qunar", method = RequestMethod.POST)
    public JsonResult<?> getMucVCard(@RequestBody List<GetMucVcardRequest> request) {
        try{
            //LOGGER.info(request.toString());

            //检查参数是否合法
            if (CollectionUtils.isEmpty(request)) {
                return JsonResultUtils.success(Collections.EMPTY_LIST);
            }
            for(GetMucVcardRequest item : request) {
                List<GetMucVcardRequest.MucInfo> mucInfos = item.getMucs();
                for(GetMucVcardRequest.MucInfo info : mucInfos) {
                    if (StringUtils.isBlank(info.getMuc_name())) {
                        return JsonResultUtils.fail(0, "参数错误");
                    }
                }
            }

            List<GetMucVcardResult> results =
            request.stream().map(item -> {
                List<GetMucVcardRequest.MucInfo> mucInfos = item.getMucs();
                GetMucVcardResult result = new GetMucVcardResult();
                result.setDomain(item.getDomain());
                if(CollectionUtils.isNotEmpty(mucInfos)){
                    List<String> mucIds = mucInfos.stream().
                            map(requestMucInfo -> requestMucInfo.getMuc_name()).collect(Collectors.toList());

                    List<MucInfoModel> mucInfoModels = iMucInfoDao.selectMucInfoByIds(mucIds);
                    List<GetMucVcardResult.MucInfo> mucInfoResultList =
                            mucInfoModels.stream().map(mucInfoModel -> {
                                GetMucVcardResult.MucInfo resultMucInfo = new GetMucVcardResult.MucInfo();
                                resultMucInfo.setMN(mucInfoModel.getMucName());
                                resultMucInfo.setSN(mucInfoModel.getShowName());
                                resultMucInfo.setMD(mucInfoModel.getMucDesc());
                                resultMucInfo.setMT(mucInfoModel.getMucTitle());
                                resultMucInfo.setMP(mucInfoModel.getMucPic());
                                resultMucInfo.setVS(mucInfoModel.getVersion());
                                return resultMucInfo;
                            }).collect(Collectors.toList());
                    result.setMucs(mucInfoResultList);
                }else {
                    result.setMucs(new ArrayList<>());
                }

                return result;
            }).collect(Collectors.toList());

            return JsonResultUtils.success(results);
        }catch (Exception ex) {
            LOGGER.error("catch error : {} ", ExceptionUtils.getStackTrace(ex));
            return JsonResultUtils.fail(0, "服务器操作异常");
        }

    }
}
