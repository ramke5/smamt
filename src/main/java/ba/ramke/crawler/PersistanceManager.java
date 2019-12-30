package ba.ramke.crawler;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.mongodb.BasicDBList;

import ba.ramke.model.DataSource;
import ba.ramke.model.Tweet;

@Repository
public class PersistanceManager {

	@Autowired
	private MongoTemplate mongoTemplate;

	private final String COLLECTION_NAME_ = "categorizedtweets";
	private final String COLLECTION_NAME = "datasource";

	public PersistanceManager() {
	}

	public void setMongoTemplate(MongoTemplate mongoTemplateSet) {
		mongoTemplate = mongoTemplateSet;
	}

	public void saveTweets(List<Tweet> tweets) {
		System.out.println("CALLED FOR INSERT");
		BasicDBList d = new BasicDBList();
		d.addAll(tweets);
		mongoTemplate.insert(d, COLLECTION_NAME_);
	}

	public void setLastCrawledTweet(String userId, String pageId, String tweetId) {
		Query query = new Query(Criteria.where("_id").is(userId).and("twitterPages").elemMatch(Criteria.where("_id").is(pageId)));
		mongoTemplate.updateFirst(query, new Update().set("twitterPages.$.lastSavedTweetId", tweetId), DataSource.class, COLLECTION_NAME);
		System.out.println("OK");
	}

	public List<String> getTweetKeywords(String message) {
		String trimedMessage = message.replaceAll("\\p{P}", " ").toLowerCase().trim().replaceAll("(\\s)+", "$1");
		String[] tweets = trimedMessage.split(" ");
		List<String> tweetKeywords = new ArrayList<String>();
		for (String s : tweets) {
			if (s.length() > 3 && !tweetKeywords.contains(s)) {
				tweetKeywords.add(s);
			}
		}
		return tweetKeywords;
	}

	public DataSource getLastCrawlTweetId(String userId, String pageId) {
		Query query = new Query(Criteria.where("_id").is(userId).and("twitterPages").elemMatch(Criteria.where("lastSavedTweetId").is(pageId)));
		DataSource pg = mongoTemplate.findOne(query, DataSource.class, COLLECTION_NAME);
		System.out.println("OK");
		return pg;
	}
}
