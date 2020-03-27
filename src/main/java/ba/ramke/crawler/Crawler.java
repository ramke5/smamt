package ba.ramke.crawler;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import ba.ramke.model.DataSource;
import ba.ramke.model.Tweet;
import twitter4j.TwitterException;

@Repository
public class Crawler implements Runnable{
	
	private String user;
	private TwitterDataSource twitterDataSource;
	private CategorizeEngine ce;
	
	public Crawler() {
	}

	public Crawler(MongoTemplate mongoTemplate, String user) {
		this.user = user;
		twitterDataSource = new TwitterDataSource();
		twitterDataSource.setMongoTemplate(mongoTemplate);
		ce = new CategorizeEngine();
		ce.setMongoTemplate(mongoTemplate);
	}
	
	@Override
	public void run() {
		System.out.println("Thread of " + user + " has been started");
		DataSource ds = twitterDataSource.getValidTwitterPagesByUserId(user);
		Map<String, Map<String, String>> categories = twitterDataSource.getCrawlCategoriesByUserId(user);
		List<Tweet> allPosts = twitterDataSource.getAllTwitterPosts();
		System.out.println("Total nuber of tweets: " + allPosts.size());
		System.out.println(categories.size());
		try {
//			ce.categorize(ds, categories);
			//when we want to recategorize
			ce.recategorize(ds, categories, allPosts);
		} catch (TwitterException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
