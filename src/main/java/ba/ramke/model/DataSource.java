package ba.ramke.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class DataSource {

	@Id
	public String userId;
	public List<DataSourcePage> twitterPages;

	public DataSource() {

	}

	public DataSource(String userId) {
		super();
		this.userId = userId;
	}

	public DataSource(String userId, List<DataSourcePage> twitterPages) {
		super();
		this.userId = userId;
		this.twitterPages = twitterPages;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public List<DataSourcePage> getTwitterPages() {
		return twitterPages;
	}

	public void setTwitterPages(List<DataSourcePage> twitterPages) {
		this.twitterPages = twitterPages;
	}

	@Override
	public String toString() {
		return String.format("Id is # %s #", userId);
	}

}
