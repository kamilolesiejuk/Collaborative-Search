package collabsearch;

import java.io.IOException;
import javax.servlet.http.*;
import org.codehaus.jackson.map.*;
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
		//Retrieve and return results for given query
		if (reqType.equals("query")){
			String q = req.getParameter("q");
			resp.getWriter().println("{\"status\":\"1\"," +
					"\"pages\":[\"http://www.google.com\",\"http://www.tryHaskell.com\",\"http://www.bing.com\"]}");
		} else if(reqType.equals("session")){
			ObjectMapper mapper = new ObjectMapper();
			try {				
				Map<String,Object> session = mapper.readValue(req.getParameter("data"),Map.class);
				String query = (String)session.get("query");
				resp.getWriter().println("Data for '"+query+"' session recieved!");
			} catch(Exception e) {
				resp.getWriter().println("Error reading session data");
			}
			
		} else {
			resp.getWriter().println("Error sending session data");
		}
	}	
}
