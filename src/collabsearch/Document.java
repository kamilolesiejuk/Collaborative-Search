package collabsearch;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Indexed;

import collabsearch.Session.Domain.Page;

/**
 * Represents a document from an IR point of view in the system. A
 * <code>Document</code> represents a single page on the internet, and it cannot
 * change the page it represents (it is immutable). The rank of the page
 * represented may vary and can be updated.
 * 
 * @author Kamil Olesiejuk
 * 
 */
@Cached
@Entity
@Indexed
public class Document {

	@Id
	private Long id;

	/**
	 * The rank assigned to this document.
	 */
	private int rank;

	private String query;

	/**
	 * URL of the page this <code>Document</code> represents. This value is
	 * immutable.
	 */
	private String url;

	private int time;
	private int outgoing;
	private boolean payment;

	private int visits;
	private String title;
	private boolean rated;
	private int sessionTime;

	private Key<SearchUser> owner;

	/**
	 * No-arg constructor for the purposes of Objectify
	 */
	@SuppressWarnings("unused")
	private Document() {
	}

	/**
	 * Single-value constructor that defaults the rank to lowest possible (10).
	 * 
	 * @param url
	 *            the URL of the page this <code>Document</code> will represent
	 */
	public Document(String url) {
		this.url = url;
		this.rank = 10;
	}

	/**
	 * Preferred constructor. Creates a full <code>Document</code> along with
	 * its assigned rank.
	 * 
	 * @param url
	 *            the URL od the page this <code>Document</code> will represent
	 * @param rank
	 *            the rank assigned to this document
	 */
	public Document(String url, int rank) {
		this.url = url;
		this.rank = rank;
	}

	public Document(String query, Page p, SearchUser owner, int sessionTime) {
		this.setQuery(query);
		this.setUrl(p.getUrl());
		this.setTime(p.getTime());
		this.setOutgoing(p.getOutgoing());
		this.setPayment(p.isPayment());
		this.setRated(p.isRated());
		this.setSessionTime(sessionTime);
		this.setTitle(p.getTitle());
		this.setVisits(p.getVisits());
		
		this.setRank(calculateRank());
		
		this.setOwner(owner);
	}

	private int calculateRank() {
		int rank = 10;

		if (this.payment) {
			rank -= 2;
		}
		if (this.rated) {
			rank -= 2;
		}

		if (this.time > 1000 * 10) {
			if (this.time < 1000 * 60) {
				rank -= 1;
			} else {
				rank -= 2;
			}
		}

		if (this.outgoing > 1) {
			if (this.outgoing > 3) {
				rank -= 2;
			} else {
				rank -= 1;
			}
		}

		Double t = (double) (this.time / this.sessionTime);
		if (t.compareTo(new Double(0.4d)) > 0) {
			if (t.compareTo(new Double(0.6d)) > 0) {
				rank -= 2;
			} else {
				rank -= 1;
			}
		}

		return rank < 1 ? 1 : rank;
	}

	private void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Getter for <code>rank</code>
	 * 
	 * @return the documents <code>rank<code>
	 */
	public int getRank() {
		return rank;
	}

	/**
	 * Setter for <code>rank</code>
	 * 
	 * @param rank
	 *            new value for the <code>rank</code> of this
	 *            <code>Document</code>
	 */
	public void setRank(int rank) {
		this.rank = rank;
	}

	/**
	 * Getter for the represented page URL.
	 * 
	 * @return URL of the page represented by this <code>Document</code>
	 */
	public String getUrl() {
		return url;
	}

	public Key<SearchUser> getOwner() {
		return owner;
	}

	public void setOwner(SearchUser owner) {
		this.owner = new Key<SearchUser>(SearchUser.class, owner.getName());
		;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public boolean isPayment() {
		return payment;
	}

	public void setPayment(boolean payment) {
		this.payment = payment;
	}

	public int getOutgoing() {
		return outgoing;
	}

	public void setOutgoing(int outgoing) {
		this.outgoing = outgoing;
	}

	public int getVisits() {
		return visits;
	}

	public void setVisits(int visits) {
		this.visits = visits;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isRated() {
		return rated;
	}

	public void setRated(boolean rated) {
		this.rated = rated;
	}

	public int getSessionTime() {
		return sessionTime;
	}

	public void setSessionTime(int sessionTime) {
		this.sessionTime = sessionTime;
	}

	public void setOwner(Key<SearchUser> owner) {
		this.owner = owner;
	}

	@Override
	public boolean equals(Object _doc) {
		Document doc = (Document) _doc;
		if (this.getUrl().equals(doc.getUrl()))
			return true;
		else
			return false;
	}

	@Override
	public String toString() {
		return "url: " + getUrl() + "rank: " + getRank();
	}
}
