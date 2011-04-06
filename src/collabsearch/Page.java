package collabsearch;

public class Page {

	private String url;
	
	public Page(String url) {
		this.url = url;
	}
	
	public Page(Document d) {
		this.url = d.getUrl();
	}

	public String getUrl() {
		return url;
	}
	
	@Override
	public String toString() {
		return url.toString();
	}
}
