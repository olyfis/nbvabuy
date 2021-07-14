package com.olympus.nbvabuy;



import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.logging.Handler;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.olympus.authenticate.ActiveDirectoryAuthentication;

import com.olympus.authenticate.ValidationException;
import com.olympus.nbva.assets.AssetData;

import com.olympus.olyutil.log.OlyLog;
 
	@WebServlet("/nbvaauth")
	public class NbvaAuth extends HttpServlet {
		private final Logger logger = Logger.getLogger(NbvaAuth.class.getName()); // define logger

		/********************************************************************************************************************************************************/
			public static boolean doAdAuth(HttpServletRequest request, String domain, String uname, String pw) {
				try {

					// authenticate user
					ActiveDirectoryAuthentication ada = new ActiveDirectoryAuthentication(domain);
					ada.authenticate(uname, pw);
					request.getSession().setAttribute("authToken", "true");
					return (true);

				} catch (ValidationException e) {
					request.getSession().setAttribute("authToken", "false");
					// error: authentication failed
					System.err.println(e.getMessage());
					return (false);
				}
			}
				 	 
		/****************************************************************************************************************************************************/
			// Service method
			protected void doServiceCode(HttpServletRequest request, HttpServletResponse response, String method)
					throws ServletException, IOException {
				PrintWriter out = response.getWriter();
				String directoryName = "D:/javalogs/logfiles/nbvabuy";
				String logFileName = "nbvabuy.log";
				Handler fileHandler = OlyLog.setAppendLog(directoryName, logFileName, logger);
				String domain = "OAI.OLYMPUSGLOBAL.COM";
				String username = "";
				String password = "";
				String dispatchJSP = "/nbvaauth.jsp";
				String dispatchJSPError = "/auth_error.jsp";

				username = request.getParameter("username");
				password = request.getParameter("password");

				DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS");
				LocalDateTime now = null;
				now = LocalDateTime.now();

				response.setContentType("text/html");

				// out.println("This is the Test of the Authentication Servlet");

				// out.println("*** username:" + username + "--<br>");
				// out.println("*** password:" + password + "--<br>");
				logger.info(dtf.format(now) + ": " + "--- Authenticating user: " + username);

				if (doAdAuth(request, domain, username, password)) {
					logger.info(dtf.format(now) + ": " + "---  User Authenication success: " + username);

					System.out.println("Authentication successful!");
					out.println("Authentication successful!");
					request.getRequestDispatcher(dispatchJSP).forward(request, response);
				} else {

					out.println("Authentication FAILED!");
					logger.info(dtf.format(now) + ": " + "---  User Authenication failed: " + username);
					request.getRequestDispatcher(dispatchJSPError).forward(request, response);

				}
				request.getSession().setAttribute("username", username);
				fileHandler.close();
			}
			 
		 /********************************************************************************************************************************************************/
			// Service method
			@Override
			protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				doServiceCode(request, response, "POST");
			}
		/********************************************************************************************************************************************************/
			// Service method
			@Override
			protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				doServiceCode(request, response, "GET");
			}
		/********************************************************************************************************************************************************/
				 
				 
		} // end class

