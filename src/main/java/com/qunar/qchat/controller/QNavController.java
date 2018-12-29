package com.qunar.qchat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.ByteStreams;
import com.qunar.qchat.model.JsonResult;
import com.qunar.qchat.utils.JsonResultUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@RequestMapping("/newapi/nck/")
@RestController
public class QNavController {
    private static final Logger LOGGER = LoggerFactory.getLogger(QNavController.class);

    @RequestMapping(value = "/qtalk_nav.qunar", method = RequestMethod.GET)
    public Map qtalkNav() {
        try {
            String Configfile = "nav.json";
            ClassPathResource classPathResource = new ClassPathResource(Configfile);
            InputStream read = classPathResource.getInputStream();
            String config = new String(ByteStreams.toByteArray(read));
            ObjectMapper mapper = new ObjectMapper();
            Map map = mapper.readValue(config, Map.class);
            return map;
        } catch (Exception e) {
            LOGGER.error("catch error ", e);
            Map map = new HashMap();
            return map;
        }
    }


}
