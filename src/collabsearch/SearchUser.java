package collabsearch;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.google.appengine.api.users.User;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Indexed;

/**
 * User of the system. Google account owners only.
 * 
 * @author Kamil Olesiejuk
 * 
 */
@Cached
@Entity
@Indexed
public class SearchUser {

	@Id
	private String id;

	private String email;

	private String token = "";

	/**
	 * No-arg constructor for Objectify
	 */
	@SuppressWarnings("unused")
	private SearchUser() {
	}

	public SearchUser(String id, String email, String token) {
		this.setId(id);
		this.setEmail(email);
		this.setToken(token);
	}
	
	public SearchUser(User user, String token) {
		this.id = user.getUserId();
		this.email = user.getEmail();
		this.token = token;
	}

	public String getEmail() {
		return this.email;
	}

	public String getToken() {
		return token;
	}

	public String getId() {
		return id;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public void save() {
		Objectify service = getService();
		service.put(this);
	}

	public void addDocument(Document doc) {
		doc.setOwner(this);
		Objectify service = getService();
		service.put(doc);
	}

	public void addDocuments(List<Document> documents) {
		for (Document doc : documents) {
			doc.setOwner(this);
		}
		Objectify service = getService();
		service.put(documents);
	}

	private static Objectify getService() {
		return ObjectifyService.begin();
	}

	private void setId(String id) {
		this.id = id;
	}

	private void setEmail(String email) {
		this.email = email;
	}
}
