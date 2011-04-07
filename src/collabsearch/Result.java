package collabsearch;

import java.util.ArrayList;
import java.util.Collections;

public class Result {

	private int _status;
	private ArrayList<Page> _pages;
	
	public Result() {
		this.setStatus(0);
	}
	
	public Result(ArrayList<Document> docs) {
		this();
		if (!docs.isEmpty()) {
			this.setStatus(1);
			this.setPages(docsToPages(docs));
		}
	}
	
	public int getStatus() {
		return _status;
	}

	public ArrayList<Page> getPages() {
		return _pages;
	}
	
	public void sortPages() {
		Collections.sort(_pages);
	}

	private ArrayList<Page> docsToPages(ArrayList<Document> docs) {
		ArrayList<Page> pages = new ArrayList<Page>();
		for (Document d : docs ) {
			pages.add(new Page(d.getUrl()));
		}
		Collections.sort(pages);
		return pages;
	}

	private void setStatus(int status) {
		this._status = status;
	}

	private void setPages(ArrayList<Page> pages) {
		this._pages = pages;
	}
}
