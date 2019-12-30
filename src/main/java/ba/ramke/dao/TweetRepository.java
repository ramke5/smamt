package ba.ramke.dao;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import ba.ramke.model.Tweet;

@Repository
public class TweetRepository {

	@Autowired
	private MongoTemplate mongoTemplate;
	public static final String COLLECTION_NAME = "categorizedtweets";

	public List<Tweet> searchTweetsByCategoryIdAndKeyword(String userId, String categoryId, String keyword, int skipTweets) {
		Query query = new Query().addCriteria(Criteria.where("user_id").is(userId).and("categoryId").is(categoryId).and("tweetKeywords").is(keyword)).skip(skipTweets).limit(200);
		query.fields().exclude("_id").exclude("user_id").exclude("tweetKeywords").exclude("type").exclude("categoryId").exclude("criteriaId");
		List<Tweet> tweets = mongoTemplate.find(query, Tweet.class, COLLECTION_NAME);
		if (tweets.isEmpty())
			return Collections.emptyList();
		else
			return tweets;
	}

	public List<Tweet> searchTweetsByCategoryId(String userId, String categoryId, int skipTweets) {
		Query query = new Query().addCriteria(Criteria.where("user_id").is(userId).and("categoryId").is(categoryId)).skip(skipTweets).limit(200);
		query.fields().exclude("_id").exclude("user_id").exclude("tweetKeywords").exclude("type").exclude("categoryId").exclude("criteriaId");
		List<Tweet> tweets = mongoTemplate.find(query, Tweet.class, COLLECTION_NAME);
		if (tweets.isEmpty())
			return Collections.emptyList();
		else
			return tweets;
	}

	public List<Tweet> searchTweetsByKeyword(String userId, String keyword, int skipTweets) {
		Query query = new Query().addCriteria(Criteria.where("user_id").is(userId).and("tweetKeywords").is(keyword)).skip(skipTweets).limit(200);
		//		query.with(new Sort(Sort.Direction.DESC, "dateOfCreation"));
		query.fields().exclude("_id").exclude("user_id").exclude("tweetKeywords").exclude("type").exclude("categoryId").exclude("criteriaId");
		List<Tweet> tweets = mongoTemplate.find(query, Tweet.class, COLLECTION_NAME);
		if (tweets.isEmpty())
			return Collections.emptyList();
		else
			return tweets;
	}
}
