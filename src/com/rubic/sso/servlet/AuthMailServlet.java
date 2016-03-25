package com.rubic.sso.servlet;

import com.rubic.sso.db.SqlSessionPool;
import com.rubic.sso.db.dao.UserDao;
import org.apache.ibatis.session.SqlSession;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Mian on 2016/3/25.
 */
public class AuthMailServlet extends HttpServlet{

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String registerID = request.getParameter("registerId");
        System.out.println("registerId: "+registerID);
        if(registerID == null){
            request.getRequestDispatcher("/index.jsp").forward(request,response);
            return ;
        }

        String email = (String)request.getSession().getAttribute(registerID);

        if(email == null || email.equals("")){
            request.getRequestDispatcher("/index.jsp").forward(request,response);
            return ;
        }
        activeUserState(email);
        System.out.println("email: "+email);
        request.setAttribute("registerName", email);
        request.getRequestDispatcher("/jsp/authMailSuccess.jsp").forward(request,response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    private void activeUserState(String email){
        SqlSession session = SqlSessionPool.getSqlSessionPool().newSqlSession();
        UserDao userDao = session.getMapper(UserDao.class);
        userDao.changeUserState(email,1);
    }

}
