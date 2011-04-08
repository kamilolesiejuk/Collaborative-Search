package collabsearch;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gdata.client.http.AuthSubUtil;
import com.google.gdata.util.AuthenticationException;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

@SuppressWarnings("serial")
public class LoginServlet extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		Objectify ofy = ObjectifyService.begin();
		UserService userService = UserServiceFactory.getUserService();

		String thisURL = request.getRequestURL().toString();

		// String next = "http://2.collaborativesearch.appspot.com/login";
		String scope = "https://www.google.com/m8/feeds/";
		boolean secure = false;
		boolean session = true;
		String authSubLogin = AuthSubUtil.getRequestUrl(thisURL, scope, secure,
				session);

		if (userService.isUserLoggedIn()) {

			User user = userService.getCurrentUser();

			response.getWriter().print(
					"<p>Hello, " + request.getUserPrincipal().getName()
							+ "! You have just signed in to "
							+ "Collaborative Search App</br></br>");

//			String urlFromAuthSub = request.getRequestURL().toString();
//			String token = AuthSubUtil.getTokenFromReply(urlFromAuthSub);
			 String token = request.getParameter("token");
			try {
				String sessionToken = AuthSubUtil.exchangeForSessionToken(
						token, null);

				SearchUser suser = new SearchUser(user.getUserId(),
						user.getEmail(), sessionToken);
				suser.save();
				System.out.println("Created SearchUser?");
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

			if (ofy.find(new Key<SearchUser>(SearchUser.class, user.getUserId())) == null) {

				response.getWriter().print(
						"Please authorize access to You contacts "
								+ "by clicking <a href=\"" + authSubLogin
								+ "\">here</a> so You can start sharing "
								+ "searches.</p> </br>");
			}

			response.getWriter().println(
					" You can <a href=\""
							+ userService.createLogoutURL(thisURL)
							+ "\">sign out</a>.</p>");

		} else {
			response.getWriter().println(
					"<p>Please <a href=\""
							+ userService.createLoginURL(thisURL)
							+ "\">sign in</a>.</p>");
		}
	}
}
