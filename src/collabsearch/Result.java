package collabsearch;

import java.util.ArrayList;

public class Result {

	private int status;
	private ArrayList<Page> pages;
	
	public Result() {
		this.setStatus(0);
	}
	
	public Result(ArrayList<Document> docs) {
		this.setStatus(1);
		this.setPages(docsToPages(docs));
	}
	
	private ArrayList<Page> docsToPages(ArrayList<Document> docs) {
		ArrayList<Page> pages = new ArrayList<Page>();
		for (Document d : docs ) {
			pages.add(new Page(d.getUrl()));
		}
		return pages;
	}

	private void setStatus(int status) {
		this.status = status;
	}

	public int getStatus() {
		return status;
	}

	private void setPages(ArrayList<Page> pages) {
		this.pages = pages;
	}

	public ArrayList<Page> getPages() {
		return pages;
	}
}
