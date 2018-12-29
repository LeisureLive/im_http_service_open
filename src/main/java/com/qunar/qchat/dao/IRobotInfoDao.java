package com.qunar.qchat.dao;

import com.qunar.qchat.dao.model.RobotInfoModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @auth dongzd.zhang
 * @Date 2018/11/8 12:21
 */
public interface IRobotInfoDao {

    /**
     * 根据名称查询机器人信息.
     * @param robotName
     * @return RobotInfoModel
     * */
    RobotInfoModel selectRobotInfoByEnName(@Param("robotName") String robotName);

    /**
     * 根据名称查询机器人信息.
     * @param names
     * @return List<RobotInfoModel>
     * */
    List<RobotInfoModel> selectRobotInfoByEnNames(@Param("names") List<String> names);

    /**
     * 根据recommend类型查询机器人信息.
     * @param type
     * @return List<RobotInfoModel>
     * */
    List<RobotInfoModel> selectRobotInfoByType(@Param("type") Integer type);

    /**
     * 插入新的robot信息.
     * @param newRobot
     * @return Integer
     * */
    Integer insertRobotInfo(RobotInfoModel newRobot);


    /**
     * 按关键字查询机器人信息.
     * @param keyword
     * @return List<RobotInfoModel>
     * */
    List<RobotInfoModel> selectRobotInfoByKeyword(@Param("keyword") String keyword);

    /**
     * 根据en_name查询数量.
     * @param enName
     * @return Integer
     * */
    Integer selectRobotCountByEnName(@Param("enName") String enName);

    /**
     * 根据enname和version查询.
     * @param enName
     * @param version
     * @return RobotInfoModel
     * */
    RobotInfoModel selectRobotInfoByEnnameAndVersion(@Param("enName") String enName,
                                                     @Param("version") Integer version);
}
