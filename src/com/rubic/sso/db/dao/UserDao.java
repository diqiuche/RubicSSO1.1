package com.rubic.sso.db.dao;

import com.rubic.sso.po.User;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * Created by LiuMian on 2016/2/28.
 */
public interface UserDao {

    User findUserByEmail(String email);

    int addUser(Map<String,String> user) throws Exception;

    void changeUserState(@Param("email") String email,@Param("state") int state);

}
