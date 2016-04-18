package com.rubic.sso.servlet;

import com.rubic.sso.action.Action;
import com.rubic.sso.action.impl.*;
import com.rubic.sso.po.Ticket;
import com.rubic.sso.server.RecoverTicket;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by LiuMian on 2016/2/28.
 * 
 * Servlet implementation class SSOAuth
 */
public class SSOAuth extends HttpServlet {

	private static final long serialVersionUID = 1L;

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

	/** 回收ticket线程�? */
	private ScheduledExecutorService schedulePool;
	
	/** 请求操作处理接口 **/
	private Action action;

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {

		tickets = new ConcurrentHashMap<String, Ticket>();

		cookieName = config.getInitParameter("cookieName");
		secure = Boolean.parseBoolean(config.getInitParameter("secure"));
		secretKey = config.getInitParameter("secretKey");
		ticketTimeout = Integer.parseInt(config
				.getInitParameter("ticketTimeout"));

		schedulePool = Executors.newScheduledThreadPool(1);
		schedulePool.scheduleAtFixedRate(new RecoverTicket(tickets),
				ticketTimeout * 60, 1, TimeUnit.MINUTES);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String requestAction = request.getParameter("action");
		if ("preLogin".equals(requestAction)) {
			action = new PreLoginAction(tickets, cookieName, secretKey);
		} else if ("login".equals(requestAction)) {
			action = new LoginAction(tickets, cookieName, secretKey, secure, ticketTimeout);
		} else if ("logout".equals(requestAction)) {
			action = new LogoutAction(tickets, cookieName, secretKey);
		} else if ("authTicket".equals(requestAction)) {
			action = new AuthTicketAction(tickets, cookieName, secretKey);
		} else {
			action = new ErrorAction();
		}
		action.doAction(request, response);
	}

	@Override
	public void destroy() {
		if (schedulePool != null)
			schedulePool.shutdown();
	}
}
