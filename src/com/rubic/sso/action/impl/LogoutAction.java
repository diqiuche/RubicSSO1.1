package com.rubic.sso.action.impl;

import com.alibaba.fastjson.JSONObject;
import com.rubic.sso.action.Action;
import com.rubic.sso.po.Ticket;
import com.rubic.sso.util.CookieUtils;
import com.rubic.sso.util.DESUtils;
import com.rubic.sso.util.exception.NullValueException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class LogoutAction implements Action {

	
	/** 单点登录标记 */
	private Map<String, Ticket> tickets;
	
	/** cookie名称 */
	private String cookieName;

	/** 密钥 */
	private String secretKey;
	
	
	public LogoutAction(Map<String, Ticket> tickets,String cookieName,String secretKey){
		this.tickets = tickets;
		this.cookieName = cookieName;
		this.secretKey = secretKey;
	}
	
	@Override
	public void doAction(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		PrintWriter out = response.getWriter();
		
		
		String encodedTicket = request.getParameter("cookieName");
		JSONObject resultJSON = new JSONObject();
		if (encodedTicket == null||encodedTicket.equals("")) {
			resultJSON.put("error", true);
			resultJSON.put("errorInfo", "Ticket can not be empty!");
		} else {
			String decodedTicket = null;
			try {
				decodedTicket = DESUtils.decrypt(encodedTicket, secretKey);
			} catch (NullValueException e) {
				e.printStackTrace();
			}
			tickets.remove(decodedTicket);
			CookieUtils.deleteCookie(request, response, cookieName);
			resultJSON.put("error", false);
		}
		out.print(resultJSON.toJSONString());
	

	}

}
