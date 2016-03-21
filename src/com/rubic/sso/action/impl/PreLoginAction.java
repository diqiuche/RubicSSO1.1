package com.rubic.sso.action.impl;

import com.rubic.sso.action.Action;
import com.rubic.sso.po.Ticket;
import com.rubic.sso.util.CookieUtils;
import com.rubic.sso.util.DESUtils;
import com.rubic.sso.util.exception.CookieNotFoundException;
import com.rubic.sso.util.exception.NullValueException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class PreLoginAction implements Action {

	
	/** 单点登录标记 */
	private Map<String, Ticket> tickets;
	
	/** cookie名称 */
	private String cookieName;

	/** 密钥 */
	private String secretKey;
	
	
	public PreLoginAction(Map<String, Ticket> tickets,String cookieName,String secretKey){
		this.tickets = tickets;
		this.cookieName = cookieName;
		this.secretKey = secretKey;
	}
	
	
	@Override
	public void doAction(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		try {
			Cookie ticket = CookieUtils.getCookie(request, cookieName);
			String encodedTicket = ticket.getValue();
			String decodedTicket = DESUtils.decrypt(encodedTicket, secretKey);
			if (tickets.containsKey(decodedTicket)) {
				String setCookieURL = request.getParameter("setCookieURL");
				String gotoURL = request.getParameter("gotoURL");
				if (setCookieURL != null){
					response.sendRedirect(setCookieURL + "?encodedticketKey="
							+ encodedTicket + "&expiry=" + ticket.getMaxAge()
							+ "&gotoURL=" + gotoURL);
				}
			} else {
				System.out.println("prelogin: tickets not contains this ticket");
				CookieUtils.deleteCookie(request, response, cookieName);
				request.getRequestDispatcher("html/login.html").forward(
						request, response);
			}
		} catch (CookieNotFoundException e) {
			System.out.println("Cookie in SSOAuth is not founded !");
			request.getRequestDispatcher("html/login.html").forward(request,
					response);
		} catch (NullValueException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
