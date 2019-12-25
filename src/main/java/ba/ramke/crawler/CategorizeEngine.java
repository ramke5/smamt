package ba.ramke.crawler;

import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.mongodb.BasicDBList;

import ba.ramke.model.DataSource;
import ba.ramke.model.DataSourcePage;
import ba.ramke.model.Feed;
import twitter4j.Paging;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

@Repository
public class CategorizeEngine {

	@Autowired
	private MongoTemplate mongoTemplate;
	private final String COLLECTION_NAME = "datasource";
	private final String COLLECTION_NAME_ = "categorizedfeeds";

	private static final String Twitter_API_key = "aZ6BgT892x8Xg3qPB8lk6y16H";
	private static final String Twitter_API_secret_key = "mMR2AoQww0rB8HdzcJmU97mDNABQvDVpJUjGGd0HPxEFdkh9aX";
	private static final String Twitter_API_access_token = "1144923975241392129-mgPNdmimWsnBVgUrHn7wFArXORuZBp";
	private static final String Twitter_API_access_token_secret = "6rdaqWaucP6jSSdOvQw3c0Jrp22DN6CuLOD70tsozDwSN";

	public CategorizeEngine() {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true).setOAuthConsumerKey(Twitter_API_key).setOAuthConsumerSecret(Twitter_API_secret_key)
				.setOAuthAccessToken(Twitter_API_access_token)
				.setOAuthAccessTokenSecret(Twitter_API_access_token_secret);
	}

	public void setMongoTemplate(MongoTemplate mongoTemplate2) {
		mongoTemplate = mongoTemplate2;
	}

	static void sleep(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
	}

	public void categorize(DataSource user, Map<String, Map<String, String>> crawlCriteria) throws TwitterException {
		System.out.println("In method");
		Long lastCrawlFeedId = 0L;
		List<String> feedKeywords = new ArrayList<String>();
		List<Feed> feeds = new ArrayList<Feed>();
		List<String> criteriId = new ArrayList<String>();
		List<String> categoryId = new ArrayList<String>();
		int i = 0;
		Iterator<DataSourcePage> dspIterator = user.getFacebookPages().iterator();
		mainLoop: while (dspIterator.hasNext()) {
			DataSourcePage dsp = dspIterator.next();
			System.out.println(dsp.getName());

			ConfigurationBuilder cb = new ConfigurationBuilder();
			cb.setDebugEnabled(true).setOAuthConsumerKey(Twitter_API_key).setOAuthConsumerSecret(Twitter_API_secret_key)
					.setOAuthAccessToken(Twitter_API_access_token)
					.setOAuthAccessTokenSecret(Twitter_API_access_token_secret);
			TwitterFactory taf = new TwitterFactory(cb.build());
			Twitter twitter = taf.getInstance();

			ArrayList<Status> statuses = getStatusesFromTwitter(dsp, twitter);
			lastCrawlFeedId = statuses.get(0).getId();

			for (Status status : statuses) {
				System.out.println(i + " feeds crawled ############################");
				if (dsp.getLastSavedFeedId().equals(status.getId())) {
					System.out.println("We came to last crawled feed. Stop");
					if (feeds.size() != 0) {
						setLastCrawledFeed(user.getUserId(), dsp.getPageId(), lastCrawlFeedId);
						saveFeeds(feeds);
					}
					i = 1;
					continue mainLoop;
				} else if (status.getText() != null) {
					feedKeywords = getFeedKeywords(status.getText());
					if (!feedKeywords.isEmpty()) {
						for (Entry<String, Map<String, String>> ent : crawlCriteria.entrySet()) {
							for (Entry<String, String> ient : ent.getValue().entrySet()) {
								for (String f : feedKeywords) {
									if (f.equals(ient.getValue().toLowerCase())) {
										System.out.println("Feed " + ient.getValue().toLowerCase());
										if (categoryId.contains(ent.getKey())) {
											criteriId.add(ient.getKey().toString());
										} else {
											categoryId.add(ent.getKey().toString());
											criteriId.add(ient.getKey().toString());
										}
									}
								}
							}
						}

						if (!criteriId.isEmpty()) {
							feeds.add(new Feed(new UID().toString(), user.getUserId(), status.getId(), status.getText(),
									feedKeywords, status.getCreatedAt(),
									"twitter.com/" + dsp.getName() + "/status/" + (status.getId()), dsp.getName(),
									"status", "twitter.com/" + dsp.getName(), dsp.getName(), categoryId, criteriId));
							criteriId.toString();
							criteriId = new ArrayList<String>();
							categoryId = new ArrayList<String>();
							feedKeywords = new ArrayList<String>();
						} else {
							categoryId.add("uncategorized");
							criteriId.add("uncategorized");
							feeds.add(new Feed(new UID().toString(), user.getUserId(), status.getId(), status.getText(),
									feedKeywords, status.getCreatedAt(),
									"twitter.com/" + dsp.getName() + "/status/" + (status.getId()), dsp.getName(),
									"status", "twitter.com/" + dsp.getName(), dsp.getName(), categoryId, criteriId));
							criteriId = new ArrayList<String>();
							categoryId = new ArrayList<String>();
							feedKeywords = new ArrayList<String>();
						}
					}

					/// COMMENTS-REPLIES

					ArrayList<Status> replies = getReplies(twitter, dsp.getName(), status.getId());
					System.out.println("For this status/tweet we have: " + replies.size());

					for (Status reply : replies) {
						if (reply.getText() != null) {
							feedKeywords = getFeedKeywords(reply.getText());
							if (!feedKeywords.isEmpty()) {
								for (Entry<String, Map<String, String>> ent : crawlCriteria.entrySet()) {
									for (Entry<String, String> ient : ent.getValue().entrySet()) {
										for (String f : feedKeywords) {
											if (f.equals(ient.getValue().toLowerCase())) {
												System.out.println("Feed " + ient.getValue().toLowerCase());
												if (categoryId.contains(ent.getKey())) {
													criteriId.add(ient.getKey().toString());
												} else {
													categoryId.add(ent.getKey().toString());
													criteriId.add(ient.getKey().toString());
												}
											}
										}
									}
								}

								if (!criteriId.isEmpty()) {
									feeds.add(new Feed(new UID().toString(), user.getUserId(), reply.getId(),
											reply.getText(), feedKeywords, reply.getCreatedAt(),
											"twitter.com/" + dsp.getName() + "/status/" + (status.getId()),
											dsp.getName(), "comment", "twitter.com/" + dsp.getName(), dsp.getName(),
											categoryId, criteriId));
									criteriId.toString();
									criteriId = new ArrayList<String>();
									categoryId = new ArrayList<String>();
									feedKeywords = new ArrayList<String>();
								} else {
									categoryId.add("uncategorized");
									criteriId.add("uncategorized");
									feeds.add(new Feed(new UID().toString(), user.getUserId(), reply.getId(),
											reply.getText(), feedKeywords, reply.getCreatedAt(),
											"twitter.com/" + dsp.getName() + "/status/" + (status.getId()),
											dsp.getName(), "comment", "twitter.com/" + dsp.getName(), dsp.getName(),
											categoryId, criteriId));
									criteriId = new ArrayList<String>();
									categoryId = new ArrayList<String>();
									feedKeywords = new ArrayList<String>();
								}

							}
						}
					}

					///
				}

				i++;
				if (i == statuses.size()) {
					System.out.println(i + " FEEDS crawled. Stop");
					if (feeds.size() != 0) {
						setLastCrawledFeed(user.getUserId(), dsp.getPageId(), lastCrawlFeedId);
						saveFeeds(feeds);
					}
					feeds = new ArrayList<Feed>();
					i = 1;
					continue mainLoop;
				}

			}
		}
	}

	private ArrayList<Status> getStatusesFromTwitter(DataSourcePage dsp, Twitter twitter) {
		ArrayList<Status> statuses = new ArrayList<Status>();
		int pageno = 1;
		while (true) {
			try {
				System.out.println("getting tweets");
				Paging page = new Paging(pageno, 200);
				statuses.addAll(twitter.getUserTimeline(dsp.getName(), page));
				int size = statuses.size();
				System.out.println("total got : " + statuses.size());
				if (statuses.size() == size) {
					break;
				}
				pageno++;
				sleep(1000);
			} catch (TwitterException e) {
				System.out.println(e.getErrorMessage());
			}
		}
		return statuses;
	}

	public ArrayList<Status> getReplies(Twitter twitter, String screenName, long tweetID) {
		ArrayList<Status> replies = new ArrayList<Status>();

		try {
			twitter4j.Query query = new twitter4j.Query("to:" + screenName + " since_id:" + tweetID);
			QueryResult results;

			do {
				results = twitter.search(query);
				System.out.println("Results: " + results.getTweets().size());
				List<Status> tweets = results.getTweets();

				for (Status tweet : tweets)
					if (tweet.getInReplyToStatusId() == tweetID)
						replies.add(tweet);
			} while ((query = results.nextQuery()) != null);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return replies;
	}

	public List<String> getFeedKeywords(String message) {
		String trimedMessage = message.replaceAll("\\p{P}", " ").toLowerCase().trim().replaceAll("(\\s)+", "$1")
				.replaceAll("[\n\r]", "");
		String[] feeds = trimedMessage.split(" ");
		List<String> feedKeywords = new ArrayList<String>();
		for (String s : feeds) {
			if (s.length() > 3 && !feedKeywords.contains(s)) {
				feedKeywords.add(s);
			}
		}
		return feedKeywords;
	}

	public void saveFeeds(List<Feed> feeds) {
		System.out.println("CALLED FOR INSERT");
		BasicDBList d = new BasicDBList();
		d.addAll(feeds);
		mongoTemplate.insert(d, COLLECTION_NAME_);
	}

//	public void setLastCrawledFeed(String userId, String pageId, String feedId) {
//		Query query = new Query(Criteria.where("_id").is(userId).and("facebookPages").elemMatch(Criteria.where("_id").is(pageId)));
//		mongoTemplate.updateFirst(query, new Update().set("facebookPages.$.lastSavedFeedId", feedId), DataSource.class, COLLECTION_NAME);
//		System.out.println("OK");
//	}

	public void setLastCrawledFeed(String userId, String pageId, Long feedId) {
		Query query = new Query(
				Criteria.where("_id").is(userId).and("facebookPages").elemMatch(Criteria.where("_id").is(pageId)));
		mongoTemplate.updateFirst(query, new Update().set("facebookPages.$.lastSavedFeedId", feedId), DataSource.class,
				COLLECTION_NAME);
		System.out.println("OK");
	}

	public DataSource getLastCrawlFeedId(String userId, String pageId) {
		Query query = new Query(Criteria.where("_id").is(userId).and("facebookPages")
				.elemMatch(Criteria.where("lastSavedFeedId").is(pageId)));
		// query.fields().include("facebookPages.facebooklastSavedFeedId");
		DataSource pg = mongoTemplate.findOne(query, DataSource.class, COLLECTION_NAME);
		System.out.println("OK");
		return pg;
	}

}
