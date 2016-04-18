package com.rubic.sso.action.impl;

import com.alibaba.fastjson.JSONObject;
import com.rubic.sso.action.Action;
import com.rubic.sso.po.Ticket;
import com.rubic.sso.util.CookieUtils;
import com.rubic.sso.util.DESUtils;
import com.rubic.sso.util.exception.NullValueException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Map;

public class AuthTicketAction implements Action {

	/** 单点登录标记 */
	private Map<String, Ticket> tickets;

	/** cookie名称 */
	private String cookieName;

	/** 密钥 */
	private String secretKey;


	public AuthTicketAction(Map<String, Ticket> tickets,String cookieName,String secretKey){
		this.tickets = tickets;
		this.cookieName = cookieName;
		this.secretKey = secretKey;
	}

	@Override
	public void doAction(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		// TODO Auto-generated method stub
		printParameters(request);
		PrintWriter out = response.getWriter();
		String encodedTicket = request.getParameter("cookieName");
		System.out.println("authticketaction cookiename: "+encodedTicket);
		JSONObject resultJSON = new JSONObject();

		String decodedTicket = null;
		try {
			decodedTicket = DESUtils.decrypt(encodedTicket, secretKey);
		} catch (NullValueException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			resultJSON.put("error", true);
			CookieUtils.deleteCookie(request, response, cookieName);
			resultJSON.put("errorInfo", "Ticket is null or '' !");
			out.print(resultJSON.toJSONString());
		}
		if (tickets.containsKey(decodedTicket)) {
			resultJSON.put("error", false);
			resultJSON.put("email", tickets.get(decodedTicket).getEmail());
			resultJSON.put("userId", tickets.get(decodedTicket).getUserId());
		} else {
			resultJSON.put("error", true);
			CookieUtils.deleteCookie(request, response, cookieName);
			resultJSON.put("errorInfo", "Ticket is not found!");
		}
		out.print(resultJSON.toJSONString());
	}
	
	
	private void printParameters(HttpServletRequest request){
		Enumeration<String> parameters = request.getParameterNames();
		while(parameters.hasMoreElements()){
			String paramterName = parameters.nextElement();
			System.out.println("paramterName: "+paramterName+" - value: "+request.getParameter(paramterName));
		}
	}
}
