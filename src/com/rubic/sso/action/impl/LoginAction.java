package com.rubic.sso.action.impl;

import com.alibaba.fastjson.JSONObject;
import com.rubic.sso.action.Action;
import com.rubic.sso.db.SqlSessionPool;
import com.rubic.sso.db.dao.UserDao;
import com.rubic.sso.po.Ticket;
import com.rubic.sso.po.User;
import com.rubic.sso.util.CookieUtils;
import com.rubic.sso.util.DESUtils;
import com.rubic.sso.util.exception.IllegalInfoException;
import org.apache.ibatis.session.SqlSession;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;
import java.util.UUID;

public class LoginAction implements Action {

	/** 单点登录标记 */
	private Map<String, Ticket> tickets;
	
	/** cookie名称 */
	private String cookieName;

	/** 是否安全协议 */
	private boolean secure;

	/** 密钥 */
	private String secretKey;

	/** ticket有效时间 */
	private int ticketTimeout;
	
	
	public LoginAction(Map<String, Ticket> tickets,String cookieName,String secretKey,boolean secure,int ticketTimeout){
		this.tickets = tickets;
		this.cookieName = cookieName;
		this.secretKey = secretKey;
		this.secure = secure;
		this.ticketTimeout = ticketTimeout;
	}
	
	@Override
	public void doAction(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String email = request.getParameter("email");
		String password = request.getParameter("password");
//		printRequestParameter(request);
		User user = null;
		try {
			user = checkUserInfo(email, password);
		} catch (IllegalInfoException e) {
//			request.getRequestDispatcher(
//					"html/login.html?err_msg=username or password is wrong!")
//					.forward(request, response);
			JSONObject result = new JSONObject();
			result.put("state", 1);
			result.put("errMsg", "email or password is wrong!");
			result.put("protocol_id", "A-7-2-1-response");
			response.getWriter().write(result.toJSONString());
			
			System.out.println("error : email: "+email+" - "+"password: "+password);
			
			return;
		}

		String ticketKey = UUID.randomUUID().toString().replace("-", "");
		String encodedticketKey = DESUtils.encrypt(ticketKey, secretKey);

		Timestamp createTime = new Timestamp(System.currentTimeMillis());
		Calendar cal = Calendar.getInstance();
		cal.setTime(createTime);
		cal.add(Calendar.MINUTE, ticketTimeout);
		Timestamp recoverTime = new Timestamp(cal.getTimeInMillis());
		Ticket ticket = new Ticket(email, user.getUser_id(), createTime,
				recoverTime);

		tickets.put(ticketKey, ticket);

		String[] checks = request.getParameterValues("autoLoginInWeek");
		int expiry = -1;
		if (checks != null && "1".equals(checks[0])) {
			expiry = 7 * 24 * 3600;
			System.out.println("一周之内免登录~~~");
		}

		CookieUtils.generateCookeie(cookieName, encodedticketKey, response,
				expiry, request.getContextPath()+"/", secure);

		String setCookieURL = request.getParameter("setCookieURL");
		String gotoURL = request.getParameter("gotoURL");

//		printRequestParameter(request);
		writeJS2Browser(response, setCookieURL, gotoURL, encodedticketKey,
				expiry);

	}
	
	
	/**
	 * 检查用户登录信息是否正确
	 * 
	 * @param email
	 * @param password
	 * @return
	 * @throws IllegalInfoException
	 */
	private User checkUserInfo(String email, String password)
			throws IllegalInfoException {
		SqlSession session = SqlSessionPool.getSqlSessionPool().newSqlSession();
		UserDao userDao = session.getMapper(UserDao.class);
		User user = userDao.findUserByEmail(email);
		if (user == null || !user.getPassword().equals(password)) {
			throw new IllegalInfoException();
		}
		System.out.println("SSOAuth: " + user.toString());
		return user;
	}

	/**
	 * 写一段js代码到浏览器，目的：页面的自动跳转指定页面并且带着特定的参数。
	 * 
	 * @param response
	 * @param setCookieURL
	 * @param gotoURL
	 * @param encodedticketKey
	 * @param expiry
	 * @throws IOException
	 */
	private void writeJS2Browser(HttpServletResponse response,
			String setCookieURL, String gotoURL, String encodedticketKey,
			int expiry) throws IOException {
		PrintWriter out = response.getWriter();

		JSONObject result = new JSONObject();
		result.put("setCookieURL", setCookieURL);
		result.put("gotoURL", gotoURL);
		result.put("encodedticketKey", encodedticketKey);
		result.put("expiry", expiry);
		result.put("state", 0);
		result.put("protocol_id", "A-7-2-1-response");
		System.out.println("result: " + result.toJSONString());
		out.write(result.toJSONString());

	}
	

}
