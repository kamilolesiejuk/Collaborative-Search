package collabsearch;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Indexed;
import com.googlecode.objectify.annotation.Unindexed;

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
	private String name;

	/**
	 * No-arg constructor for Objectify
	 */
	@SuppressWarnings("unused")
	private SearchUser() {
	}

	public SearchUser(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void save() {
		Objectify service = getService();
		service.put(this);
	}

	public void addDocument(Document retweet) {
		retweet.setOwner(this);
		Objectify service = getService();
		service.put(retweet);
	}

	public void addDocuments(List<Document> documents) {
		for (Document doc : documents) {
			doc.setOwner(this);
		}
		Objectify service = getService();
		service.put(documents);
	}

	public static SearchUser findByName(String name) {
		Objectify service = getService();
		return service.get(SearchUser.class, name);
	}

	private static Objectify getService() {
		return ObjectifyService.begin();
	}
}
