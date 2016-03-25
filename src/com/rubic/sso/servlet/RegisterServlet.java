package com.rubic.sso.servlet;

import com.rubic.sso.db.SqlSessionPool;
import com.rubic.sso.db.dao.UserDao;
import com.rubic.sso.po.MyAuthenticator;
import com.rubic.sso.po.User;
import org.apache.ibatis.session.SqlSession;

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
import javax.servlet.http.HttpSession;
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
        String email = request.getParameter("email");
        String registerName = request.getParameter("userName");
        String password = request.getParameter("password");

        registerUser(registerName,email,password);
        String url = generateAuthURL(request, email);
        try {
            sendEmail(email, url);
        } catch (MessagingException e) {
            System.out.println("something is wrong");
            e.printStackTrace();
        }
        System.out.println("forward to sendMailSuccess.jsp");
        request.getRequestDispatcher("/jsp/sendMailSuccess.jsp").forward(request, response);
    }


    private String generateAuthURL(HttpServletRequest request, String email) {
        String registerId = "" + Math.random() * Math.random();
        //待会用户点在邮箱中点击这个链接回到你的网站。
        String url = EMAIL_AUTH_ADDR + request.getContextPath() + "/authMail?registerId=" + registerId;
        /**
         * 可以考虑保存到redis或者memcache
         */
        HttpSession httpSession = request.getSession();
        httpSession.setAttribute(registerId, email);
        httpSession.setMaxInactiveInterval(600);
        System.out.println("auth email addr: "+url);
        return url;
    }

    private void sendEmail(String toEmailAddr, String authURL) throws MessagingException {

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
        msg.setContent("<a href=' " + authURL + "'>点击" + authURL + "完成注册</a>", "text/html;charset=utf-8");
        msg.setRecipient(MimeMessage.RecipientType.TO, to);
            /*
            Transport transport = session.getTransport("smtp");
            transport.connect("smtp.163.com", userName, password);
            transport.sendMessage(msg,msg.getAllRecipients());
            transport.close();
            */
        Transport.send(msg);
        System.out.println("send email succeed");
    }

    private boolean registerUser(String userName, String email, String password) {

        Map<String,String> user = new HashMap<>();
        user.put("user_name",userName);
        user.put("email",email);
        user.put("password",password);
//        User user = new User();
//        user.setUser_name(userName);
//        user.setEmail(email);
//        user.setPassword(password);
        SqlSession session = SqlSessionPool.getSqlSessionPool().newSqlSession();
        UserDao userDao = session.getMapper(UserDao.class);
        int result = 0;
        try {
            result = userDao.addUser(user);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("insert failed ---- s");
        }
        System.out.println("register: "+user+" result: "+result);
        session.commit();
        if (result == 1) {
            return true;
        } else {
            return false;
        }
    }


}
