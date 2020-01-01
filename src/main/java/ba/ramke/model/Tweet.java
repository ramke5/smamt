package ba.ramke.model;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;

public class Tweet {

	@Id
	public String id;
	public String user_id;
	public Long tweetId;
	public String message;
	public List<String> tweetKeywords;
	public Date dateOfCreation;
	public String url;
	public String source;
	public String type;
	public String twitter_userid;
	public String userName;
	public String userLocation;
	public List<String> categoryId;
	public List<String> criteriaId;

	public Tweet(String id, String user_id, Long tweetId, String message, List<String> tweetKeywords, Date dateOfCreation, String url, String source, String type, String twitter_userid, String userName, String userLocation, List<String> categoryId, List<String> criteriaId) {
		super();
		this.id = id;
		this.user_id = user_id;
		this.tweetId = tweetId;
		this.message = message;
		this.tweetKeywords = tweetKeywords;
		this.dateOfCreation = dateOfCreation;
		this.url = url;
		this.source = source;
		this.type = type;
		this.twitter_userid = twitter_userid;
		this.userName = userName;
		this.userLocation = userLocation;
		this.categoryId = categoryId;
		this.criteriaId = criteriaId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public Long getTweetId() {
		return tweetId;
	}

	public void setTweetId(Long tweetId) {
		this.tweetId = tweetId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<String> getTweetKeywords() {
		return tweetKeywords;
	}

	public void setTweetKeywords(List<String> tweetKeywords) {
		this.tweetKeywords = tweetKeywords;
	}

	public Date getDateOfCreation() {
		return dateOfCreation;
	}

	public void setDateOfCreation(Date dateOfCreation) {
		this.dateOfCreation = dateOfCreation;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUserId() {
		return twitter_userid;
	}

	public void setUserId(String twitter_userid) {
		this.twitter_userid = twitter_userid;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getUserLocation() {
		return userLocation;
	}

	public void setUserLocation(String userLocation) {
		this.userLocation = userLocation;
	}

	public List<String> getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(List<String> categoryId) {
		this.categoryId = categoryId;
	}

	public List<String> getCriteriaId() {
		return criteriaId;
	}

	public void setCriteriId(List<String> criteriaId) {
		this.criteriaId = criteriaId;
	}

}
