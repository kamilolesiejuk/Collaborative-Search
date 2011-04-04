package collabsearch;

import java.io.IOException;
import javax.servlet.http.*;

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
		resp.getWriter().println("Hello, world");
	}
	
	
}
