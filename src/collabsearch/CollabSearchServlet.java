package collabsearch;

import java.io.IOException;
import javax.servlet.http.*;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.*;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

import collabsearch.Session.Domain;
import collabsearch.Session.Domain.Page;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

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
			makeResponse(resp, q);
			
			
			
//			resp.getWriter()
//					.println(
//							"{\"status\":\"1\","
//									+ "\"pages\":[\"http://www.google.com\",\"http://www.tryHaskell.com\",\"http://www.bing.com\"]}");
		} else if (reqType.equals("session")) {
			ObjectMapper mapper = new ObjectMapper();
			Session session = new Session();
			try {
				session = mapper.readValue(
						req.getParameter("data"), Session.class);
			} catch (IOException e) {
				resp.getWriter().println("Error reading session data");
				// log stack trace
				
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
	
	private void makeResponse(HttpServletResponse resp, String q) throws IOException {
		
		MappingJsonFactory fact = new MappingJsonFactory();
		JsonGenerator gen = fact.createJsonGenerator(resp.getWriter());
		
		Objectify ofy = ObjectifyService.begin();
		
		Iterable<Key<Document>> docKeys = ofy.query(Document.class).filter("query =", q).fetchKeys();
		
		Map<Key<Document>, Document> docMap = ofy.get(docKeys);
		
		ArrayList<Document> docs = new ArrayList<Document>(docMap.values());
		
		if (docs.isEmpty()) {
			try {
				resp.getWriter().println("{\"status\":\"0\",");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				resp.getWriter().println("{\"status\":\"1\","
						+ "\"pages\":");
				gen.writeObject(docs);
				gen.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void storeSessionData(String uid, Session s) {
		
		SearchUser user = new SearchUser(uid);
		user.save();
		
		String userId = s.getUserId();
		String query = s.getQuery();
		int sessionTime = s.getTime();
		
		ArrayList<Domain> domains = s.getDomains();
		
		for (Domain d : domains ) {
			
			ArrayList<Page> pages = d.getPages();
			
			for (Page p : pages ) {
				
				if(p.getTime() < 2000 ) { continue; }
				
				Document doc = new Document(p.getUrl());
				
				doc.setOutgoing(p.getOutgoing());
				doc.setPayment(p.isPayment());
				doc.setRated(p.isRated());
				doc.setSessionTime(sessionTime);
				doc.setTitle(p.getTitle());
				doc.setVisits(p.getVisits());
				doc.setQuery(query);
				
				doc.setOwner(user);
				
				user.addDocument(doc);
				
			}
			
		}
		
		
	}
}
