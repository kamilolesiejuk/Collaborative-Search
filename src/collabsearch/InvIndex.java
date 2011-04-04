package collabsearch;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.appengine.api.users.User;

/**
 * Inverted Index of documents by query and tokenised queries.
 * 
 * @author Kamil Olesiejuk
 * 
 */
public class InvIndex extends DocIndex {

	private User owner;
	private HashMap<String, ArrayList<Document>> map;

	public HashMap<String, ArrayList<Document>> getMap() {
		return map;
	}

	public InvIndex(User owner) {
		this.map = new HashMap<String, ArrayList<Document>>();
		this.owner = owner;
	}

	private InvIndex(User owner, HashMap<String, ArrayList<Document>> map) {
		this.map = map;
		this.owner = owner;
	}

	public User getOwner() {
		return owner;
	}

	public boolean containsKey(String q) {
		return map.containsKey(q);
	}

	@Override
	public boolean containsDoc(Document doc) {
		for (ArrayList<Document> arr : map.values()) {
			if (arr.contains(doc))
				return true;
		}
		return false;
	}

	@Override
	public ArrayList<Document> query(String query) {
		ArrayList<Document> results = new ArrayList<Document>();
		results.addAll(map.get(query));
		String[] tokens;
		if ((tokens = query.split(" ")) != null) {
			for (int i = 0; i < tokens.length; i++) {
				results.addAll(map.get(tokens[i]));
			}
		}
		return results;
	}

	@Override
	public void put(String query, Document doc) {
		if (!this.containsDoc(doc)) {
			if (map.containsKey(query)) {
				map.get(query).add(doc);
			} else {
				ArrayList<Document> arr = new ArrayList<Document>();
				arr.add(doc);
				map.put(query, arr);
			}
		} else {
			Document oldDoc = getDoc(doc);
			if (oldDoc != null) {
				oldDoc.setRank((oldDoc.getRank() + doc.getRank()) / 2);
				if (map.containsKey(query) && !map.get(query).contains(doc)) {
					map.get(query).add(oldDoc);
				} else if (!map.containsKey(query)) {
					ArrayList<Document> arr = new ArrayList<Document>();
					arr.add(oldDoc);
					map.put(query, arr);
				}
			} else {
				System.out.print("ERROR: Could not find a Document");
			}
		}
	}

	private Document getDoc(Document doc) {
		for (ArrayList<Document> arr : map.values()) {
			for (Document d : arr) {
				if (doc.equals(d)) {
					return d;
				}
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see collabsearch.DocIndex#merge(java.util.ArrayList)
	 */
	// This method is a very basic version and might introduce rank bias
	// during a merge.
	@Override
	public DocIndex merge(ArrayList<DocIndex> indices) {
		DocIndex newIndex = this.clone();
		for (DocIndex index : indices) {
			InvIndex ind = (InvIndex) index;
			for (String key : ind.getMap().keySet()) {
				for (Document doc : ind.getMap().get(key)) {
					newIndex.put(key, doc);
				}
			}
		}
		return newIndex;
	}

	@Override
	public InvIndex clone() {
		return new InvIndex(this.owner, this.map);
	}
}
