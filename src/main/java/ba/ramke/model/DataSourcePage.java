package ba.ramke.model;

import org.springframework.data.annotation.Id;

public class DataSourcePage {
	@Id
	public String pageId;
	public String url;
	public String name;
	public int status;
	public Long lastSavedTweetId;

	public DataSourcePage(String pageId, String url, String name, int status, Long lastSavedTweetId) {
		super();
		this.pageId = pageId;
		this.url = url;
		this.name = name;
		this.status = status;
		this.lastSavedTweetId = lastSavedTweetId;
	}

	public String getPageId() {
		return pageId;
	}

	public void setPageId(String pageId) {
		this.pageId = pageId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Long getLastSavedTweetId() {
		return lastSavedTweetId;
	}

	public void setLastSavedTweetId(Long lastSavedTweetId) {
		this.lastSavedTweetId = lastSavedTweetId;
	}

	@Override
	public String toString() {
		return String.format("Name is # %s #", name);
	}
}
