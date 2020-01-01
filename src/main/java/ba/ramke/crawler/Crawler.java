package ba.ramke.crawler;

import java.io.IOException;
import java.util.Map;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import ba.ramke.model.DataSource;
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
		System.out.println(categories.size());
		try {
			ce.categorize(ds, categories);
		} catch (TwitterException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
