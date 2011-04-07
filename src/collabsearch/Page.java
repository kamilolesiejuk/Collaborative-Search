package collabsearch;

public class Page implements Comparable<Page> {

	private String _url;
	private int _rank;

	public Page(String url) {
		this._url = url;
	}

	public Page(String url, int rank) {
		this._url = url;
		this.setRank(rank);
	}

	public Page(Document d) {
		this._url = d.getUrl();
		this._rank = d.getRank();
	}

	public String getUrl() {
		return _url;
	}

	public int getRank() {
		return _rank;
	}

	private void setRank(int rank) {
		this._rank = rank;
	}

	@Override
	public String toString() {
		return _rank + ":" + _url.toString();
	}

	@Override
	public int compareTo(Page o) {
		return this._rank - o.getRank();
	}
}
