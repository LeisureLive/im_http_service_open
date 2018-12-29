package com.qunar.qchat.dao;

import com.qunar.qchat.dao.model.RobotPubSubModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @auth dongzd.zhang
 * @Date 2018/11/8 20:40
 */
public interface IRobotPubSubDao {

    /**
     * 插入订阅信息.
     * @param robotPubSubModel
     * @return Integer
     * */
    Integer insertPubSub(RobotPubSubModel robotPubSubModel);


    /**
     * 删除订阅信息.
     * @param robotPubSubModel
     * @return Integer
     * */
    Integer deletePubSub(RobotPubSubModel robotPubSubModel);


    /**
     * 查询用户的所有订阅.
     * @param userHost
     * @param userName
     * @return List<RobotPubSubModel>
     * */
    List<RobotPubSubModel> selectAllUserPubSub(@Param("userName") String userName,
                                               @Param("userHost") String userHost);


    /**
     * 根据主键查询数量.
     * @param userName
     * @param rbtName
     * @return Integer
     * */
    Integer selectPubSubCountByKey(@Param("userName") String userName,
                                   @Param("rbtName") String rbtName,
                                   @Param("userHost") String userHost);
}
