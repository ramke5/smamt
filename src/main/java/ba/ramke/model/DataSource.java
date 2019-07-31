package ba.ramke.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class DataSource {

	@Id
	public String userId;
	String facebookPages;

	public DataSource() {

	}

	public DataSource(String userId) {
		super();
		this.userId = userId;
	}

	public DataSource(String userId, String facebookPages) {
		super();
		this.userId = userId;
		this.facebookPages = facebookPages;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getFacebookPages() {
		return facebookPages;
	}

	public void setFacebookPages(String facebookPages) {
		this.facebookPages = facebookPages;
	}

	@Override
	public String toString() {
		return String.format("Id is # %s #", userId);
	}

}
