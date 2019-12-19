package ba.ramke.dao;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.FacebookClient.AccessToken;
import com.restfb.Version;
import com.restfb.types.Page;

import ba.ramke.model.DataSource;
import ba.ramke.model.DataSourcePage;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

@Repository
public class DataSourceRepository {
	@Autowired
	private MongoTemplate mongoTemplate;
	public static final String COLLECTION_NAME = "datasource";
	public static final String APP_ID = "371307700457729";
	public static final String APP_SECRET = "e2100b59c8d1eea3f802d25f83410098";
	
	private static final String Twitter_API_key = "aZ6BgT892x8Xg3qPB8lk6y16H";
	private static final String Twitter_API_secret_key = "mMR2AoQww0rB8HdzcJmU97mDNABQvDVpJUjGGd0HPxEFdkh9aX";
	private static final String Twitter_API_access_token = "1144923975241392129-mgPNdmimWsnBVgUrHn7wFArXORuZBp";
	private static final String Twitter_API_access_token_secret = "6rdaqWaucP6jSSdOvQw3c0Jrp22DN6CuLOD70tsozDwSN";

	
	
	public void addUserToCollection(DataSource ds) {
		mongoTemplate.insert(ds, COLLECTION_NAME);
	}
	
	public void deletePersonFromCollection(DataSource ds) {
		mongoTemplate.remove(ds, COLLECTION_NAME);
	}
	
	public void addTwitterAccount(String userId, String url, String pageName) {
		DataSourcePage newPage = new DataSourcePage(UUID.randomUUID().toString(), url, pageName, 1, "recentlyAdded");
		mongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(userId)), new Update().push("facebookPages", newPage), COLLECTION_NAME);
		System.out.println("Everything is ok. Collection is updated");
	}
	
	public void deleteTwitterAccountById(String userId, String pageId) {
		Query query = new Query(Criteria.where("_id").is(userId).and("facebookPages").elemMatch(Criteria.where("_id").is(pageId)));
		mongoTemplate.updateFirst(query, new Update().set("facebookPages.$.status", 0), DataSource.class, COLLECTION_NAME);
	}

	public void restoreFacebookPageById(String userId, String pageId) {
		Query query = new Query(Criteria.where("_id").is(userId).and("facebookPages").elemMatch(Criteria.where("_id").is(pageId)));
		mongoTemplate.updateFirst(query, new Update().set("facebookPages.$.status", 1), DataSource.class, COLLECTION_NAME);
	}
	
	public boolean isTwitterAccountValid(String userId, String url) {
		try {
			System.out.println("in: isTwitterAccountValid");
			int index = url.lastIndexOf('/');
			String accountName = url.substring(index + 1, url.length());
			
			ConfigurationBuilder cb = new ConfigurationBuilder();
			cb.setDebugEnabled(true)
			  .setOAuthConsumerKey(Twitter_API_key)
			  .setOAuthConsumerSecret(Twitter_API_secret_key)
			  .setOAuthAccessToken(Twitter_API_access_token)
			  .setOAuthAccessTokenSecret(Twitter_API_access_token_secret);
			TwitterFactory taf = new TwitterFactory(cb.build());
			Twitter twitter = taf.getInstance();
			
			try {
				User us = twitter.showUser(accountName);
				System.out.println("Account: " + us.getName() + " is valid!");
				addTwitterAccount(userId, url, accountName);
			} catch (Exception e) {
				System.out.println("This is not an account. Please give valid link to Twitter account.");
				return false;
			}
		} catch (Exception e) {
			System.out.println("This is not an account. Please give valid link to Twitter account.");
			return false;
		}
		return true;
	}
	
	public List<DataSource> getAllFacebookPagesWithValidStatusByUserId(String userId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(userId));
		List<DataSource> datasource = mongoTemplate.find(query, DataSource.class, COLLECTION_NAME);
		Iterator<DataSource> dataSourceIterator = datasource.iterator();
		while (dataSourceIterator.hasNext()) {
			DataSource ds = dataSourceIterator.next();
			System.out.println("Hamdija ga sejva " + ds.toString());
			if (ds.getFacebookPages() == null)
				break;
			else {
				Iterator<DataSourcePage> dspIterator = ds.getFacebookPages().iterator();
				while (dspIterator.hasNext()) {
					DataSourcePage dsp = dspIterator.next();
					if (dsp.getStatus() == 0) {
						dspIterator.remove();
					}
				}
			}
		}
		return datasource;
	}

	public List<DataSource> getAllDeletedFacebookPagesByUserId(String userId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(userId));
		List<DataSource> datasource = mongoTemplate.find(query, DataSource.class, COLLECTION_NAME);
		Iterator<DataSource> dataSourceIterator = datasource.iterator();
		while (dataSourceIterator.hasNext()) {
			DataSource ds = dataSourceIterator.next();
			Iterator<DataSourcePage> dspIterator = ds.getFacebookPages().iterator();
			while (dspIterator.hasNext()) {
				DataSourcePage dsp = dspIterator.next();
				System.out.println(dsp.name);
				if (dsp.getStatus() == 1) {
					dspIterator.remove();
				}
			}
		}
		return datasource;
	}
}
