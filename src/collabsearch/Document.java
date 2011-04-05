package collabsearch;

import javax.persistence.Id;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Unindexed;

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
public class Document {

	@Id
	private Long id;

	/**
	 * URL of the page this <code>Document</code> represents. This value is
	 * immutable.
	 */
	@Unindexed
	private String url;

	/**
	 * The rank assigned to this document.
	 */
	private int rank;

	private Key<SearchUser> owner;

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

	public void setOwner(SearchUser owner) {
		this.owner = new Key<SearchUser>(SearchUser.class, owner.getName());;
	}
}
