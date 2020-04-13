package ba.ramke.crawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
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
		
		//
		ArrayList<ArrayList<String>> listDatuma = new ArrayList<ArrayList<String>>();
		ArrayList<String> singleList = new ArrayList<String>();
		
//		singleList = new ArrayList<String>();		
//		singleList.add("25-31-2018");
//		singleList.add("15-01-2019");
//		listDatuma.add(singleList);
//		
//		
//		singleList = new ArrayList<String>();		
//		singleList.add("15-01-2019");
//		singleList.add("30-01-2019");
//		listDatuma.add(singleList);
//		
//		singleList = new ArrayList<String>();
//		singleList.add("30-01-2019");			
//		singleList.add("15-02-2019");			
//		listDatuma.add(singleList);
//		
//		singleList = new ArrayList<String>();
//		singleList.add("15-02-2019");			
//		singleList.add("28-02-2019");			
//		listDatuma.add(singleList);			
//
//		singleList = new ArrayList<String>();
//		singleList.add("28-02-2019");			
//		singleList.add("15-03-2019");			
//		listDatuma.add(singleList);
//		
//		singleList = new ArrayList<String>();
//		singleList.add("15-03-2019");			
//		singleList.add("30-03-2019");			
//		listDatuma.add(singleList);
//		
//		singleList = new ArrayList<String>();
//		singleList.add("30-03-2019");			
//		singleList.add("15-04-2019");			
//		listDatuma.add(singleList);	
//		
//		singleList = new ArrayList<String>();
//		singleList.add("15-04-2019");			
//		singleList.add("30-04-2019");			
//		listDatuma.add(singleList);	
//		
//		singleList = new ArrayList<String>();
//		singleList.add("30-04-2019");			
//		singleList.add("15-05-2019");			
//		listDatuma.add(singleList);
//		
//		singleList = new ArrayList<String>();
//		singleList.add("15-05-2019");			
//		singleList.add("30-05-2019");			
//		listDatuma.add(singleList);	
//		
//		singleList = new ArrayList<String>();
//		singleList.add("30-05-2019");			
//		singleList.add("15-06-2019");			
//		listDatuma.add(singleList);	
		
		singleList = new ArrayList<String>();
		singleList.add("15-06-2019");			
		singleList.add("30-06-2019");			
		listDatuma.add(singleList);		
		
		singleList = new ArrayList<String>();
		singleList.add("30-06-2019");			
		singleList.add("15-07-2019");			
		listDatuma.add(singleList);	
		
		singleList = new ArrayList<String>();
		singleList.add("15-07-2019");			
		singleList.add("30-07-2019");			
		listDatuma.add(singleList);	
		
		singleList = new ArrayList<String>();
		singleList.add("30-07-2019");			
		singleList.add("15-08-2019");			
		listDatuma.add(singleList);	
		
		singleList = new ArrayList<String>();
		singleList.add("15-08-2019");			
		singleList.add("30-08-2019");			
		listDatuma.add(singleList);	
		
		singleList = new ArrayList<String>();
		singleList.add("30-08-2019");			
		singleList.add("15-09-2019");			
		listDatuma.add(singleList);	
		
		singleList = new ArrayList<String>();
		singleList.add("15-09-2019");			
		singleList.add("30-09-2019");			
		listDatuma.add(singleList);	
		
		singleList = new ArrayList<String>();
		singleList.add("30-09-2019");			
		singleList.add("15-10-2019");			
		listDatuma.add(singleList);	
		
		singleList = new ArrayList<String>();
		singleList.add("15-10-2019");			
		singleList.add("30-10-2019");			
		listDatuma.add(singleList);
		
		singleList = new ArrayList<String>();
		singleList.add("30-10-2019");			
		singleList.add("15-11-2019");			
		listDatuma.add(singleList);	
		
		singleList = new ArrayList<String>();
		singleList.add("15-11-2019");			
		singleList.add("30-11-2019");			
		listDatuma.add(singleList);	
		
		singleList = new ArrayList<String>();
		singleList.add("30-11-2019");			
		singleList.add("15-12-2019");			
		listDatuma.add(singleList);	
		
		singleList = new ArrayList<String>();
		singleList.add("15-12-2019");			
		singleList.add("30-12-2019");			
		listDatuma.add(singleList);	
		
		singleList = new ArrayList<String>();
		singleList.add("30-12-2019");			
		singleList.add("15-01-2020");			
		listDatuma.add(singleList);	
		
		singleList = new ArrayList<String>();
		singleList.add("15-01-2020");			
		singleList.add("30-01-2020");			
		listDatuma.add(singleList);	
		
		singleList = new ArrayList<String>();
		singleList.add("30-01-2020");			
		singleList.add("15-02-2020");			
		listDatuma.add(singleList);	
		
		singleList = new ArrayList<String>();
		singleList.add("15-02-2020");			
		singleList.add("28-02-2020");			
		listDatuma.add(singleList);	
		
		singleList = new ArrayList<String>();
		singleList.add("28-02-2020");			
		singleList.add("15-03-2020");			
		listDatuma.add(singleList);	
		
		singleList = new ArrayList<String>();
		singleList.add("15-03-2020");			
		singleList.add("30-03-2020");			
		listDatuma.add(singleList);	
		
		singleList = new ArrayList<String>();
		singleList.add("30-03-2020");			
		singleList.add("15-04-2020");			
		listDatuma.add(singleList);			
		
		
		for (int i=0; i<listDatuma.size(); i++) {
			String dateOdGT= listDatuma.get(i).get(0);
			String datedoLT = listDatuma.get(i).get(1);
			List<Tweet> allPosts = twitterDataSource.getAllTwitterPosts(dateOdGT, datedoLT);
			System.out.println("Total nuber of tweets: " + allPosts.size() + " for period " + dateOdGT + " to " + datedoLT);
			System.out.println(categories.size());
			
			try {
				ce.categorize(ds, categories);
				ce.recategorize(ds, categories, allPosts);
	////			ce.parse(user);
	////			ce.lastTweetId(user);
	////			ce.spasiJedanTweet(user);
	////			ce.updateLastSavedTweet(user);
	//			
			} catch (TwitterException | IOException e) {
	//			 TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
