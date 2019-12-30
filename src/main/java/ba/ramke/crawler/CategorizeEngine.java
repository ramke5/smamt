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
import ba.ramke.model.Tweet;
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
	private final String COLLECTION_NAME_ = "categorizedtweets";

	private static final String Twitter_API_key = "aZ6BgT892x8Xg3qPB8lk6y16H";
	private static final String Twitter_API_secret_key = "mMR2AoQww0rB8HdzcJmU97mDNABQvDVpJUjGGd0HPxEFdkh9aX";
	private static final String Twitter_API_access_token = "1144923975241392129-mgPNdmimWsnBVgUrHn7wFArXORuZBp";
	private static final String Twitter_API_access_token_secret = "6rdaqWaucP6jSSdOvQw3c0Jrp22DN6CuLOD70tsozDwSN";
	private static final Long Initial_Last_Saved_ID = 123L;

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
		Long lastCrawlTweetId = 0L;
		List<String> tweetKeywords = new ArrayList<String>();
		List<Tweet> tweets = new ArrayList<Tweet>();
		List<String> criteriId = new ArrayList<String>();
		List<String> categoryId = new ArrayList<String>();
		int i = 0;
		Iterator<DataSourcePage> dspIterator = user.getTwitterPages().iterator();
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

			for (int counter=statuses.size(); counter != 0; counter--) {
				Status status = statuses.get(counter-1);
				lastCrawlTweetId = status.getId();
				System.out.println(i + " tweets crawled ############################");
				if (dsp.getLastSavedTweetId().equals(status.getId())) {
					System.out.println("We came to last crawled tweet. Stop");
					if (tweets.size() != 0) {
						setLastCrawledTweet(user.getUserId(), dsp.getPageId(), lastCrawlTweetId);
						saveTweets(tweets);
					}
					i = 1;
					continue mainLoop;
				} else if (status.getText() != null) {
					tweetKeywords = getTweetKeywords(status.getText());
					if (!tweetKeywords.isEmpty()) {
						for (Entry<String, Map<String, String>> ent : crawlCriteria.entrySet()) {
							for (Entry<String, String> ient : ent.getValue().entrySet()) {
								for (String f : tweetKeywords) {
									if (f.equals(ient.getValue().toLowerCase())) {
										System.out.println("Tweet " + ient.getValue().toLowerCase());
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
							tweets.add(new Tweet(new UID().toString(), user.getUserId(), status.getId(),
									status.getText(), tweetKeywords, status.getCreatedAt(),
									"twitter.com/" + dsp.getName() + "/status/" + (status.getId()), dsp.getName(),
									"status", "twitter.com/" + dsp.getName(), dsp.getName(), categoryId, criteriId));
							criteriId.toString();
							criteriId = new ArrayList<String>();
							categoryId = new ArrayList<String>();
							tweetKeywords = new ArrayList<String>();
						} else {
							categoryId.add("uncategorized");
							criteriId.add("uncategorized");
							tweets.add(new Tweet(new UID().toString(), user.getUserId(), status.getId(),
									status.getText(), tweetKeywords, status.getCreatedAt(),
									"twitter.com/" + dsp.getName() + "/status/" + (status.getId()), dsp.getName(),
									"status", "twitter.com/" + dsp.getName(), dsp.getName(), categoryId, criteriId));
							criteriId = new ArrayList<String>();
							categoryId = new ArrayList<String>();
							tweetKeywords = new ArrayList<String>();
						}
					}

					/// COMMENTS-REPLIES

					ArrayList<Status> replies = getReplies(twitter, dsp.getName(), status.getId());
					System.out.println("For this status/tweet we have: " + replies.size());

					for (Status reply : replies) {
						if (reply.getText() != null) {
							tweetKeywords = getTweetKeywords(reply.getText());
							if (!tweetKeywords.isEmpty()) {
								for (Entry<String, Map<String, String>> ent : crawlCriteria.entrySet()) {
									for (Entry<String, String> ient : ent.getValue().entrySet()) {
										for (String f : tweetKeywords) {
											if (f.equals(ient.getValue().toLowerCase())) {
												System.out.println("Tweet " + ient.getValue().toLowerCase());
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
									tweets.add(new Tweet(new UID().toString(), user.getUserId(), reply.getId(),
											reply.getText(), tweetKeywords, reply.getCreatedAt(),
											"twitter.com/" + dsp.getName() + "/status/" + (status.getId()),
											dsp.getName(), "comment", "twitter.com/" + dsp.getName(), dsp.getName(),
											categoryId, criteriId));
									criteriId.toString();
									criteriId = new ArrayList<String>();
									categoryId = new ArrayList<String>();
									tweetKeywords = new ArrayList<String>();
								} else {
									categoryId.add("uncategorized");
									criteriId.add("uncategorized");
									tweets.add(new Tweet(new UID().toString(), user.getUserId(), reply.getId(),
											reply.getText(), tweetKeywords, reply.getCreatedAt(),
											"twitter.com/" + dsp.getName() + "/status/" + (status.getId()),
											dsp.getName(), "comment", "twitter.com/" + dsp.getName(), dsp.getName(),
											categoryId, criteriId));
									criteriId = new ArrayList<String>();
									categoryId = new ArrayList<String>();
									tweetKeywords = new ArrayList<String>();
								}

							}
						}
					}

					///
				}

				i++;
				if (i == statuses.size()) {
					System.out.println(i + " TWEETS crawled. Stop");
					if (tweets.size() != 0) {
						setLastCrawledTweet(user.getUserId(), dsp.getPageId(), lastCrawlTweetId);
						saveTweets(tweets);
					}
					tweets = new ArrayList<Tweet>();
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
				Paging page = new Paging(pageno, 100);
				if(dsp.getLastSavedTweetId()==Initial_Last_Saved_ID) {
					statuses.addAll(twitter.getUserTimeline(dsp.getName(), page));
				}
				else {
					// get from
					Paging page2 = new Paging(pageno, dsp.getLastSavedTweetId());
					statuses.addAll(twitter.getUserTimeline(dsp.getName(), page2));
				}
				
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

	public List<String> getTweetKeywords(String message) {
		String trimedMessage = message.replaceAll("\\p{P}", " ").toLowerCase().trim().replaceAll("(\\s)+", "$1")
				.replaceAll("[\n\r]", "");
		String[] tweets = trimedMessage.split(" ");
		List<String> tweetKeywords = new ArrayList<String>();
		for (String s : tweets) {
			if (s.length() > 3 && !tweetKeywords.contains(s)) {
				tweetKeywords.add(s);
			}
		}
		return tweetKeywords;
	}

	public void saveTweets(List<Tweet> tweets) {
		System.out.println("CALLED FOR INSERT");
		BasicDBList d = new BasicDBList();
		d.addAll(tweets);
		mongoTemplate.insert(d, COLLECTION_NAME_);
	}

//	public void setLastCrawledTweet(String userId, String pageId, String tweetId) {
//		Query query = new Query(Criteria.where("_id").is(userId).and("twitterPages").elemMatch(Criteria.where("_id").is(pageId)));
//		mongoTemplate.updateFirst(query, new Update().set("twitterPages.$.lastSavedTweetId", tweetId), DataSource.class, COLLECTION_NAME);
//		System.out.println("OK");
//	}

	public void setLastCrawledTweet(String userId, String pageId, Long tweetId) {
		Query query = new Query(
				Criteria.where("_id").is(userId).and("twitterPages").elemMatch(Criteria.where("_id").is(pageId)));
		mongoTemplate.updateFirst(query, new Update().set("twitterPages.$.lastSavedTweetId", tweetId), DataSource.class,
				COLLECTION_NAME);
		System.out.println("OK");
	}

	public DataSource getLastCrawlTweetId(String userId, String pageId) {
		Query query = new Query(Criteria.where("_id").is(userId).and("twitterPages")
				.elemMatch(Criteria.where("lastSavedTweetId").is(pageId)));
		// query.fields().include("twitterPages.twitterlastSavedTweetId");
		DataSource pg = mongoTemplate.findOne(query, DataSource.class, COLLECTION_NAME);
		System.out.println("OK");
		return pg;
	}

}
