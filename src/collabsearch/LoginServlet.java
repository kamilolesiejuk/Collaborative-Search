package collabsearch;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import com.google.gdata.client.*;
import com.google.gdata.client.contacts.*;
import com.google.gdata.client.http.AuthSubUtil;
import com.google.gdata.data.*;
import com.google.gdata.data.contacts.*;
import com.google.gdata.data.extensions.*;
import com.google.gdata.util.*;

import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;

@SuppressWarnings("serial")
public class LoginServlet extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		UserService userService = UserServiceFactory.getUserService();

		String thisURL = request.getRequestURL().toString();
		
//		String next = "http://localhost:8888/login";
		String scope = "https://www.google.com/m8/feeds/";
		boolean secure = false;
		boolean session = true;
		String authSubLogin = AuthSubUtil.getRequestUrl(thisURL, scope,
				secure, session);
		
		if (request.getUserPrincipal() != null) {
			
//			String token = AuthSubUtil.getTokenFromReply(urlFromAuthSub);
			String token = request.getParameter("token");
			try {
				String sessionToken = AuthSubUtil.exchangeForSessionToken(token, null);
				
				User user = userService.getCurrentUser();
				SearchUser suser = new SearchUser(user.getUserId(), user.getEmail(), sessionToken);
				suser.save();
				
			} catch (AuthenticationException e) {
				// TODO Auto-generated catch block
				// Contacts fetch failed
				// SearchUser not created
				e.printStackTrace();
			} catch (GeneralSecurityException e) {
				// TODO Auto-generated catch block
				// Contacts fetch failed
				// SearchUser not created
				e.printStackTrace();
			}
			
			response.getWriter().println(
					"<p>Hello, " + request.getUserPrincipal().getName()
							+ "! </br></br> "
							+ " You can <a href=\""
							+ userService.createLogoutURL(thisURL)
							+ "\">sign out</a>.</p>");
		} else {
			response.getWriter().println(
					"<p>Please <a href=\""
							+ userService.createLoginURL(authSubLogin)
							+ "\">sign in</a>.</p>");
		}
	}
}
