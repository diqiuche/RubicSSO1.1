package com.rubic.sso.servlet;

import com.rubic.sso.db.SqlSessionPool;
import com.rubic.sso.db.dao.UserDao;
import com.rubic.sso.po.MyAuthenticator;
import com.rubic.sso.util.JedisUtils;
import com.rubic.sso.util.exception.RegisterException;
import org.apache.ibatis.session.SqlSession;
import redis.clients.jedis.Jedis;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Mian on 2016/3/25.
 */
public class RegisterServlet extends HttpServlet {

    //系统的邮箱地址
    private String SYSTEM_EMAIL_ADDR;
    //系统邮箱密码
    private String SYSTEM_EMAIL_PASSWORD;
    //邮箱验证地址
    private String EMAIL_AUTH_ADDR;
    //smtp服务地址
    private String MAIL_SMTP_HOST;

    private String MAIL_SMTP_AUTH;


    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.SYSTEM_EMAIL_ADDR = config.getInitParameter("systemEmailAddr");
        this.SYSTEM_EMAIL_PASSWORD = config.getInitParameter("systemEmailPassword");
        this.EMAIL_AUTH_ADDR = config.getInitParameter("emailAuthAddr");
        this.MAIL_SMTP_AUTH = config.getInitParameter("mailSmtpAuth");
        this.MAIL_SMTP_HOST = config.getInitParameter("mailSmtpHost");
        System.out.println(SYSTEM_EMAIL_ADDR+" - "+SYSTEM_EMAIL_PASSWORD+" - "+EMAIL_AUTH_ADDR+" - "+MAIL_SMTP_AUTH+" - "+MAIL_SMTP_HOST);

    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        String email = request.getParameter("email");
        String registerName = request.getParameter("userName");
        String password = request.getParameter("password");

        try {
            registerUser(registerName,email,password);
            sendEmail(email);
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (RegisterException e) {
            e.printStackTrace();
        }
        request.getRequestDispatcher("/jsp/sendMailSuccess.jsp").forward(request, response);
    }


    /**
     * 生成验证地址
     * @param email
     * @return
     */
    private String generateAuthURL(String email) {
        String registerId = "" + Math.random() * Math.random();
        //待会用户点在邮箱中点击这个链接回到你的网站。
        String url = EMAIL_AUTH_ADDR + "/authMail?registerId=" + registerId;

        //使用redis存储
        storeKeyValueToJedis(registerId,email);

        return url;
    }

    /**
     * 发送email
     * @param toEmailAddr 目标邮箱地址
     * @throws MessagingException
     */
    private void sendEmail(String toEmailAddr) throws MessagingException {
        String authURL = generateAuthURL(toEmailAddr);
        Properties props = new Properties();
        props.setProperty("mail.smtp.host", MAIL_SMTP_HOST);
        props.setProperty("mail.smtp.auth", MAIL_SMTP_AUTH);

        Authenticator authenticator = new MyAuthenticator(SYSTEM_EMAIL_ADDR, SYSTEM_EMAIL_PASSWORD);

        javax.mail.Session session = javax.mail.Session.getDefaultInstance(props, authenticator);
        session.setDebug(true);

        Address from = new InternetAddress(SYSTEM_EMAIL_ADDR);
        Address to = new InternetAddress(toEmailAddr);

        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(from);
        msg.setSubject("注册CubeAPIStore");
        msg.setSentDate(new Date());
        msg.setContent("<a href=' " + authURL + "'>点击" + authURL + "完成注册</a></br>" +
                "如果是非本人操作请忽略本邮件", "text/html;charset=utf-8");
        msg.setRecipient(MimeMessage.RecipientType.TO, to);

        Transport.send(msg);
    }

    /**
     * 注册用户，未验证邮箱
     * @param userName
     * @param email
     * @param password
     * @return
     * @throws RegisterException
     */
    private boolean registerUser(String userName, String email, String password) throws RegisterException {
        SqlSession session = SqlSessionPool.getSqlSessionPool().newSqlSession();
        UserDao userDao = session.getMapper(UserDao.class);
        int isExist = 0;
        isExist = userDao.checkEmail(email);
        if(isExist != 0){
            throw new RegisterException("用户已存在");
        }

        Map<String,String> user = new HashMap<>();
        user.put("user_name",userName);
        user.put("email",email);
        user.put("password",password);
        int result = 0;
        try {
            result = userDao.addUser(user);
            session.commit();
        } catch (Exception e) {
//            e.printStackTrace();
            throw new RegisterException("注册用户失败");
        }
        if (result == 1) {
            return true;
        } else {
            return false;
        }
    }

    private void storeKeyValueToJedis(String key,String value){
        //使用redis存储
        Jedis jedis = JedisUtils.getJedis();
        jedis.set(key,value);
        JedisUtils.returnResource(jedis);
    }

}
