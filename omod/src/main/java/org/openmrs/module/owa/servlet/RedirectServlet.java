package org.openmrs.module.owa.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmrs.api.context.Context;

public class RedirectServlet extends HttpServlet {
	
	/**
	 * Process HEAD request. This returns the same headers as GET request, but without content.
	 * 
	 * @param request
	 * @param response
	 * @throws javax.servlet.ServletException
	 * @throws java.io.IOException
	 * @see HttpServlet#doHead(HttpServletRequest, HttpServletResponse).
	 */
	@Override
	protected void doHead(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Process request without content.
		processRequest(request, response, false);
	}
	
	/**
	 * Process GET request.
	 * 
	 * @param request
	 * @param response
	 * @throws javax.servlet.ServletException
	 * @throws java.io.IOException
	 * @see HttpServlet#doGet(HttpServletRequest, HttpServletResponse).
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Process request with content.
		processRequest(request, response, true);
	}
	
	/**
	 * Process the actual request.
	 * 
	 * @param request The request to be processed.
	 * @param response The response to be created.
	 * @param content Whether the request body should be written (GET) or not (HEAD).
	 * @throws IOException If something fails at I/O level.
	 */
	private void processRequest(HttpServletRequest request, HttpServletResponse response, boolean content)
	        throws IOException {
		String url = request.getRequestURL().toString().replace("/ms/owa/redirectServlet", "/owa");
		//@TODO redirecting to original url after login in openmrs.
		String loginUrl = Context.getAdministrationService().getGlobalProperty("login.url", "login.htm");
		response.sendRedirect(request.getContextPath() + "/" + loginUrl + "?redirect=" + url);
	}
}
