package com.rubic.sso.po;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * Created by Mian on 2016/3/25.
 */
public class MyAuthenticator extends Authenticator {

    private String userName;
    private String password;

    public MyAuthenticator(String userName, String password){
        this.userName = userName;
        this.password = password;
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(userName, password);
    }

}
