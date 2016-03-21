package com.rubic.sso.action.impl;

import com.rubic.sso.action.Action;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ErrorAction implements Action {

	
	public ErrorAction(){
	}
	
	
	@Override
	public void doAction(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		System.err.println("指令错误");
//		response.sendRedirect("/RubicSSO/html/login.html");

//		request.getRequestDispatcher("html/login.html").forward(request,
//				response);

		response.getWriter().write("指令错误，点击返回<a href='http://www.rcis.cqupt.edu.cn'>");

		
	}

}
