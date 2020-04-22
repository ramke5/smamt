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
	
	private static final String Twitter_API_key = "aZ6BgT892x8Xg3qPB8lk6y16H";
	private static final String Twitter_API_secret_key = "mMR2AoQww0rB8HdzcJmU97mDNABQvDVpJUjGGd0HPxEFdkh9aX";
	private static final String Twitter_API_access_token = "1144923975241392129-mgPNdmimWsnBVgUrHn7wFArXORuZBp";
	private static final String Twitter_API_access_token_secret = "6rdaqWaucP6jSSdOvQw3c0Jrp22DN6CuLOD70tsozDwSN";
	private static final Long Initial_Last_Saved_ID = 123L;

	
	
	public void addUserToCollection(DataSource ds) {
		mongoTemplate.insert(ds, COLLECTION_NAME);
	}
	
	public void deletePersonFromCollection(DataSource ds) {
		mongoTemplate.remove(ds, COLLECTION_NAME);
	}
	
	public void addTwitterAccount(String userId, String url, String pageName) {
		DataSourcePage newPage = new DataSourcePage(UUID.randomUUID().toString(), url, pageName, 1, Initial_Last_Saved_ID);
		mongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(userId)), new Update().push("twitterPages", newPage), COLLECTION_NAME);
		System.out.println("Everything is ok. Collection is updated");
	}
	
	public void deleteTwitterAccountById(String userId, String pageId) {
		Query query = new Query(Criteria.where("_id").is(userId).and("twitterPages").elemMatch(Criteria.where("_id").is(pageId)));
		mongoTemplate.updateFirst(query, new Update().set("twitterPages.$.status", 0), DataSource.class, COLLECTION_NAME);
	}

	public void restoreTwitterPageById(String userId, String pageId) {
		Query query = new Query(Criteria.where("_id").is(userId).and("twitterPages").elemMatch(Criteria.where("_id").is(pageId)));
		mongoTemplate.updateFirst(query, new Update().set("twitterPages.$.status", 1), DataSource.class, COLLECTION_NAME);
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
	
	public List<DataSource> getAllTwitterPagesWithValidStatusByUserId(String userId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(userId));
		List<DataSource> datasource = mongoTemplate.find(query, DataSource.class, COLLECTION_NAME);
		Iterator<DataSource> dataSourceIterator = datasource.iterator();
		while (dataSourceIterator.hasNext()) {
			DataSource ds = dataSourceIterator.next();
			System.out.println("in: getAllTwitterPagesWithValidStatusByUserId " + ds.toString());
			if (ds.getTwitterPages() == null)
				break;
			else {
				Iterator<DataSourcePage> dspIterator = ds.getTwitterPages().iterator();
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
	
	public List<DataSourcePage> getAllTwitterPagesWithValidStatus(String userId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(userId));
		List<DataSource> datasource = mongoTemplate.find(query, DataSource.class, COLLECTION_NAME);
		Iterator<DataSource> dataSourceIterator = datasource.iterator();
		while (dataSourceIterator.hasNext()) {
			DataSource ds = dataSourceIterator.next();
			System.out.println("in: getAllTwitterPagesWithValidStatusByUserId " + ds.toString());
			if (ds.getTwitterPages() == null)
				break;
			else {
				Iterator<DataSourcePage> dspIterator = ds.getTwitterPages().iterator();
				while (dspIterator.hasNext()) {
					DataSourcePage dsp = dspIterator.next();
					if (dsp.getStatus() == 0) {
						dspIterator.remove();
					}
				}
			}
		}
		List<DataSourcePage> listPages = datasource.get(0).getTwitterPages();
		return listPages;
	}

	public List<DataSource> getAllDeletedTwitterPagesByUserId(String userId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(userId));
		List<DataSource> datasource = mongoTemplate.find(query, DataSource.class, COLLECTION_NAME);
		Iterator<DataSource> dataSourceIterator = datasource.iterator();
		while (dataSourceIterator.hasNext()) {
			DataSource ds = dataSourceIterator.next();
			Iterator<DataSourcePage> dspIterator = ds.getTwitterPages().iterator();
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
