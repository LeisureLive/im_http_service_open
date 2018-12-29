package com.qunar.qchat.dao;

import com.qunar.qchat.dao.model.HostUserModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IHostUserDao {

    List<HostUserModel> selectIncrementByVersion(@Param("table") String table,
                                                 @Param("version") Integer version,
                                                 @Param("hostId") Integer hostId);

    Integer selectMaxVersion(@Param("table") String table);

}
