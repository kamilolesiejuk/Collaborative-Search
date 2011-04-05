package collabsearch;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.googlecode.objectify.ObjectifyService;

public class ContextInitializer implements ServletContextListener {

	public void contextDestroyed(ServletContextEvent arg) {
	}

	public void contextInitialized(ServletContextEvent arg) {
		ObjectifyService.register(Document.class);
		ObjectifyService.register(SearchUser.class);
	}
}