package com.rubic.sso.db.dao;

import com.rubic.sso.po.User;

/**
 * Created by LiuMian on 2016/2/28.
 */
public interface UserDao {

    User findUserByEmail(String email);

}
