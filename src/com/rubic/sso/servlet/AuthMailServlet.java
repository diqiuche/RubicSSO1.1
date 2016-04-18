package com.rubic.sso.servlet;

import com.rubic.sso.db.SqlSessionPool;
import com.rubic.sso.db.dao.UserDao;
import com.rubic.sso.util.JedisUtils;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;
import redis.clients.jedis.Jedis;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mian on 2016/3/25.
 */
public class AuthMailServlet extends HttpServlet{

    private Log logger = LogFactory.getLog(AuthMailServlet.class);

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String registerID = request.getParameter("registerId");
        System.out.println("registerId: "+registerID);
        if(registerID == null){
            request.getRequestDispatcher("/index.jsp").forward(request,response);
            return ;
        }

        //使用redis进行存储
        String email = getValueFromJedis(registerID);

        System.out.println("email: "+email);
        if(email == null || email.equals("")){
            System.out.println("错误！邮箱不存在");
            request.getRequestDispatcher("/index.jsp").forward(request,response);
            return ;
        }
        activeUserState(email);
        request.setAttribute("registerName", email);
        request.getRequestDispatcher("/jsp/authMailSuccess.jsp").forward(request,response);
    }


    /**
     * 改变用户账户激活状态，如果已经激活应当抛出异常
     * @param email
     */
    private void activeUserState(String email){
        SqlSession session = SqlSessionPool.getSqlSessionPool().newSqlSession();
        UserDao userDao = session.getMapper(UserDao.class);
        Map<String,Object> parameters = new HashMap<>();
        parameters.put("email",email);
        parameters.put("state",1);
        logger.debug("email: "+email+"state: "+1);
        userDao.changeUserState(parameters);
        session.commit();
        logger.debug("finished update");
    }

    /**
     * 从Jedis里面获取value
     * @param key key
     * @return
     */
    private String getValueFromJedis(String key){
        Jedis jedis = JedisUtils.getJedis();
        String value = jedis.get(key);
        JedisUtils.returnResource(jedis);
        return value;
    }

}
