package collabsearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;

import collabsearch.Session.Domain;
import collabsearch.Session.Domain.Page;

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
		resp.setContentType("text/plain");
		String reqType = req.getParameter("type");
		// Retrieve and return results for given query
		if (reqType.equals("query")) {

			String q = req.getParameter("q");
			System.out.println(q);

			makeResponse(resp, q);

			// System.out.println(resp);

			// resp.getWriter()
			// .println(
			// "{\"status\":\"1\","
			// +
			// "\"pages\":[\"http://www.google.com\",\"http://www.tryHaskell.com\",\"http://www.bing.com\"]}");
		} else if (reqType.equals("session")) {
			ObjectMapper mapper = new ObjectMapper();
			Session session = new Session();
			try {
				session = mapper.readValue(req.getParameter("data"),
						Session.class);
			} catch (IOException e) {
				resp.getWriter().println("Error reading session data");
				// log stack trace

				e.printStackTrace();
				System.out.println(req.getParameter("data"));
			}

			String query = session.getQuery();
			resp.getWriter().println(
					"Data for '" + query + "' session recieved!");
			String userid = req.getParameter("userid");
			storeSessionData(userid, session);

		} else {
			resp.getWriter().println("Error sending session data");
		}
	}

	private void makeResponse(HttpServletResponse resp, String q)
			throws IOException {

		ObjectMapper mapper = new ObjectMapper();
		Objectify ofy = ObjectifyService.begin();
		Iterable<Key<Document>> docKeys = ofy.query(Document.class)
				.filter("query =", q).fetchKeys();
		
		System.out.println(docKeys.iterator().hasNext());
		
		Map<Key<Document>, Document> docMap = ofy.get(docKeys);
		Result res = new Result(new ArrayList<Document>(docMap.values()));
		try {
			resp.getWriter().println(mapper.writeValueAsString(res));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void storeSessionData(String uid, Session s) {

		SearchUser user = new SearchUser(uid);
		user.save();

		@SuppressWarnings("unused")
		String userId = s.getUserId();
		String query = s.getQuery();
		int sessionTime = s.getTime();

		ArrayList<Domain> domains = s.getDomains();

		for (Domain d : domains) {

			ArrayList<Page> pages = d.getPages();

			for (Page p : pages) {
				if (p.getTime() < 2000 && !p.isRated() && !p.isPayment()) {
					continue; // coarse-grain filter
				}
				Document doc = new Document(query, p, user, sessionTime);

				user.addDocument(doc);
			}
		}
	}
}
