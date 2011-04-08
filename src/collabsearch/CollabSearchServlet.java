package collabsearch;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.omg.CORBA.Current;

import collabsearch.Session.Domain;
import collabsearch.Session.Domain.Page;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gdata.client.Query;
import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.ContactFeed;
import com.google.gdata.data.contacts.ContactGroupEntry;
import com.google.gdata.data.contacts.ContactGroupFeed;
import com.google.gdata.data.extensions.Email;
import com.google.gdata.util.ServiceException;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

/**
 * 
 * 
 * @author Kamil Olesiejuk
 * 
 */
@SuppressWarnings("serial")
public class CollabSearchServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		Objectify ofy = ObjectifyService.begin();
		UserService userService = UserServiceFactory.getUserService();

		if (!userService.isUserLoggedIn()) {
			resp.getWriter().println(
					"<p>Please <a href=\""
							+ "http://2.collaborativesearch.appspot.com/login"
							+ "\">sign in</a>.</p>");
		} else {
			User u = userService.getCurrentUser();
			String uid = u.getUserId();
			// TODO: check if user logged in, check if exists ?
			SearchUser user = ofy.find(new Key<SearchUser>(SearchUser.class,
					uid));
			if (user == null) {
				// is not in our DB
				// do something sensible
				// redirect to /login
			} else {
				resp.setContentType("text/plain");
				String reqType = req.getParameter("type");
				if (reqType.equals("query")) {
					handleQuery(req, resp, user);
				} else if (reqType.equals("session")) {
					handleSessionData(req, resp, user);
				} else {
					resp.getWriter().println("Error sending session data");
				}
			}
		}
	}

	private void handleSessionData(HttpServletRequest req,
			HttpServletResponse resp, SearchUser user) throws IOException,
			UnsupportedEncodingException {
		ObjectMapper mapper = new ObjectMapper();
		Session session = new Session();
		try {
			session = mapper.readValue(
					URLDecoder.decode(req.getParameter("data"), "UTF-8"),
					Session.class);
		} catch (IOException e) {
			resp.getWriter().println("Error reading session data");
			e.printStackTrace();
			System.out.println(URLDecoder.decode(req.getParameter("data"),
					"UTF-8"));
		}
		String query = session.getQuery();
		resp.getWriter().println("Data for '" + query + "' session recieved!");
		storeSessionData(user, session);
	}

	private void handleQuery(HttpServletRequest req, HttpServletResponse resp,
			SearchUser user) throws UnsupportedEncodingException, IOException {
		String q = URLDecoder.decode(req.getParameter("q"), "UTF-8");
		makeResponse(resp, user, q);
	}

	private void makeResponse(HttpServletResponse resp, SearchUser user,
			String q) throws IOException {
		Objectify ofy = ObjectifyService.begin();
		ArrayList<Document> documents = new ArrayList<Document>();
		ArrayList<String> addresses = getContacts(user, getGroups(user));
		// for (String addr : addresses) {
		// SearchUser u = ofy.query(SearchUser.class).filter("email", addr)
		// .get();
		// if (u != null) {
		// Iterable<Key<Document>> docKeys = ofy
		// .query(Document.class)
		// .filter("owner",
		// new Key<SearchUser>(SearchUser.class, u.getId()))
		// .fetchKeys();
		//
		// Map<Key<Document>, Document> docs = ofy.get(docKeys);
		// documents.addAll(docs.values());
		// }
		// }
		//
		// Iterable<Key<Document>> docKeys = ofy
		// .query(Document.class)
		// .filter("owner",
		// new Key<SearchUser>(SearchUser.class, user.getId()))
		// .fetchKeys();
		// System.out.println(docKeys.iterator().hasNext());
		// Map<Key<Document>, Document> docs = ofy.get(docKeys);
		// documents.addAll(docs.values());

		Iterator<Document> tmp = ofy.query(Document.class).filter("query =", q)
				.iterator();
		Document tempD;
		while (tmp.hasNext()) {
			tempD = tmp.next();
			System.out.println(tempD.getUrl() + " "
					+ tempD.getOwner().toString());
			for (String addr : addresses) {
				SearchUser u = ofy.query(SearchUser.class)
						.filter("email", addr).get();
				if (u != null) {
					if (tempD.getOwner().equals(
							new Key<SearchUser>(SearchUser.class, u.getEmail()))) {
						documents.add(tempD);
					}
				}
			}
			if (tempD.getOwner().equals(
					new Key<SearchUser>(SearchUser.class, user.getEmail()))) {
				documents.add(tempD);
			}
		}

		Result res = new Result(documents);
		try {
			ObjectMapper mapper = new ObjectMapper();
			resp.getWriter().println(mapper.writeValueAsString(res));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void storeSessionData(SearchUser user, Session s) {
		Objectify ofy = ObjectifyService.begin();
		String query = s.getQuery();
		int sessionTime = s.getTime();
		ArrayList<Domain> domains = s.getDomains();
		for (Domain d : domains) {
			ArrayList<Page> pages = d.getPages();
			for (Page p : pages) {
				Document doc = new Document(query, p, user, sessionTime);
				Iterable<Key<Document>> docKeys = ofy.query(Document.class)
						.filter("url =", doc.getUrl()).fetchKeys();
				if (!docKeys.iterator().hasNext()) {
					user.addDocument(doc);
				} else {
					Key<Document> docKey = docKeys.iterator().next();
					Document tempD = ofy.get(docKey);
					if (doc.equals(tempD)) {
						ofy.delete(docKey);
					} // replace the previous entry if query and URL equal
					user.addDocument(doc);
				}
			}
		}
	}

	private ArrayList<String> getGroups(SearchUser user) throws IOException {
		String sessionToken = user.getToken();
		ContactsService myService = new ContactsService("collabsearch");
		myService.setAuthSubToken(sessionToken, null);
		ArrayList<String> gids = new ArrayList<String>();
		try {
			URL feedUrl = new URL(
					"https://www.google.com/m8/feeds/groups/default/full");
			ContactGroupFeed resultFeed = myService.getFeed(feedUrl,
					ContactGroupFeed.class);
			for (int i = 0; i < resultFeed.getEntries().size(); i++) {
				ContactGroupEntry groupEntry = resultFeed.getEntries().get(i);
				if (groupEntry.getTitle().getPlainText().equals("My Contacts")) {
					gids.add(groupEntry.getId());
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		return gids;
	}

	private ArrayList<String> getContacts(SearchUser user,
			ArrayList<String> gids) throws IOException {
		ArrayList<String> addresses = new ArrayList<String>();
		String sessionToken = user.getToken();
		ContactsService myService = new ContactsService("collabsearch");
		myService.setAuthSubToken(sessionToken, null);
		// Create query and submit a request
		URL feedUrl = new URL(
				"https://www.google.com/m8/feeds/contacts/default/full");
		Query myQuery = new Query(feedUrl);

		for (String groupId : gids) {
			myQuery.setStringCustomParameter("group", groupId);
			ContactFeed resultFeed;
			try {
				resultFeed = myService.query(myQuery, ContactFeed.class);
				for (int i = 0; i < resultFeed.getEntries().size(); i++) {
					ContactEntry entry = resultFeed.getEntries().get(i);
					for (Email email : entry.getEmailAddresses()) {
						if (email.getAddress().endsWith("@gmail.com")
								|| email.getAddress().endsWith(
										"@googlemail.com")) {
							addresses.add(email.getAddress());
						}
					}

				}
			} catch (ServiceException e) {
				e.printStackTrace();
			}
		}
		return addresses;
	}
}
