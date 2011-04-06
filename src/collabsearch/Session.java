package collabsearch;

import java.util.ArrayList;

public class Session {

	private String _userId;
	private String _query;
	private int _time;
	private ArrayList<Domain> _domains;
	
	public String getUserId() {
		return _userId;
	}

	public void setUserId(String userId) {
		this._userId = userId;
	}

	public String getQuery() {
		return _query;
	}

	public void setQuery(String query) {
		this._query = query;
	}

	public int getTime() {
		return _time;
	}

	public void setTime(int time) {
		this._time = time;
	}

	public ArrayList<Domain> getDomains() {
		return _domains;
	}

	public void setDomains(ArrayList<Domain> domains) {
		this._domains = domains;
	}

	public static class Domain {
		
		private String _name;
		private boolean _payment;
		private int _visits;
		private ArrayList<Page> _pages;
		
		public String getName() {
			return _name;
		}

		public void setName(String name) {
			this._name = name;
		}

		public boolean isPayment() {
			return _payment;
		}

		public void setPayment(boolean payment) {
			this._payment = payment;
		}

		public int getVisits() {
			return _visits;
		}

		public void setVisits(int visits) {
			this._visits = visits;
		}

		public ArrayList<Page> getPages() {
			return _pages;
		}

		public void setPages(ArrayList<Page> pages) {
			this._pages = pages;
		}

		public static class Page {
			
			private String _url;
			private int _time;
			private boolean _payment;
			private int _outgoing;
			private int _visits;
			private String _title;
			private boolean _rated;
			
			public String getUrl() {
				return _url;
			}
			public void setUrl(String url) {
				this._url = url;
			}
			public int getTime() {
				return _time;
			}
			public void setTime(int time) {
				this._time = time;
			}
			public boolean isPayment() {
				return _payment;
			}
			public void setPayment(boolean payment) {
				this._payment = payment;
			}
			public int getOutgoing() {
				return _outgoing;
			}
			public void setOutgoing(int outgoing) {
				this._outgoing = outgoing;
			}
			public int getVisits() {
				return _visits;
			}
			public void setVisits(int visits) {
				this._visits = visits;
			}
			public String getTitle() {
				return _title;
			}
			public void setTitle(String title) {
				this._title = title;
			}
			public boolean isRated() {
				return _rated;
			}
			public void setRated(boolean rated) {
				this._rated = rated;
			}
			
		}
	}
}
