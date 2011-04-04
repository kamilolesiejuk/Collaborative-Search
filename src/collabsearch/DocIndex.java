package collabsearch;

import java.util.ArrayList;

/**
 * @author Kamil Olesiejuk
 *
 */
public abstract class DocIndex {


	/**
	 * Index lookup against a given query.
	 * 
	 * @param query search query to the index
	 * @return a list of relevant documents
	 */
	public abstract ArrayList<Document> query(String query);

	/**
	 * Index insert.
	 * 
	 * @param doc document to be inserted in the index
	 */
	public abstract void put(String query, Document doc);
	
	public abstract DocIndex merge(ArrayList<DocIndex> indices);
	
	public abstract boolean containsKey(String q);
	
	public abstract boolean containsDoc(Document doc);
	
}