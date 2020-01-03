package ba.ramke.crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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

import com.github.irobson.jgenderize.GenderizeIoAPI;
import com.github.irobson.jgenderize.client.Genderize;
import com.github.irobson.jgenderize.model.NameGender;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mongodb.BasicDBList;

import ba.ramke.dao.TweetRepository;
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
	@Autowired
	TweetRepository tweetDao;
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

	public void categorize(DataSource user, Map<String, Map<String, String>> crawlCriteria) throws TwitterException, IOException {
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

			for (int counter = statuses.size(); counter != 0; counter--) {
				Status status = statuses.get(counter - 1);
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
									boolean similarWords = false;
									// 1st check if words are equal
									if (f.equals(ient.getValue().toLowerCase())) {
										similarWords = true;
										System.out.println("Tweet: '" + f + "' is same as '"
												+ ient.getValue().toLowerCase() + "'.");
									}
									// 2nd check if first word is substring of next one
									else if (f.toLowerCase().contains(ient.getValue().toLowerCase())) {
										similarWords = true;
										System.out.println("Tweet: '" + ient.getValue().toLowerCase()
												+ "' is a substring of '" + f + "'.");
									}
									// Fist 3 letters same + Simon White of Catalysoft algorithm
									else if (f.toLowerCase().substring(0, 3)
											.equals(ient.getValue().toLowerCase().substring(0, 3))) {
										double similarity = compareStrings(f.toLowerCase(),
												ient.getValue().toLowerCase());
										if (similarity >= 0.7) {
											similarWords = true;
											System.out.println("Tweet: '" + f + "' is similar to '"
													+ ient.getValue().toLowerCase() + "'.");
										}
									} else {
										similarWords = false;
//										System.out.println("Tweet: '" + f + "' is NOT similar to '"
//												+ ient.getValue().toLowerCase() + "'.");
									}

//									// Another option
//									Integer noOfCharsDifferent = LevenshteinDistance.getDefaultInstance().apply(f, ient.getValue().toLowerCase());
//									Integer noOfCharsInWord = f.length();
//									if(noOfCharsInWord<=5 && noOfCharsDifferent<=1) {
//										similarWords=true;
//									}
//									else if(noOfCharsInWord<=7 && noOfCharsDifferent<=2) {
//										similarWords=true;
//									}
//									else if(noOfCharsInWord>7 && noOfCharsDifferent<=3) {
//										similarWords=true;
//									}

									if (similarWords == true) {
//										System.out.println("Tweet '" + f + "' is similar/same to '" + ient.getValue().toLowerCase()+"'.");
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
						
						String location = twitter.showUser(status.getUser().getId()).getLocation();
						String name = twitter.showUser(status.getUser().getId()).getName();
						String gender = checkGender(user.userId, name);

						if (!criteriId.isEmpty()) {
							tweets.add(new Tweet(new UID().toString(), user.getUserId(), status.getId(),
									status.getText(), tweetKeywords, status.getCreatedAt(),
									"twitter.com/" + dsp.getName() + "/status/" + (status.getId()), dsp.getName(),
									"status", "twitter.com/" + dsp.getName(), dsp.getName(), location, name, gender, categoryId,
									criteriId));
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
									"status", "twitter.com/" + dsp.getName(), dsp.getName(), location, name, gender, categoryId,
									criteriId));
							criteriId = new ArrayList<String>();
							categoryId = new ArrayList<String>();
							tweetKeywords = new ArrayList<String>();
						}
					}

					/// COMMENTS-REPLIES

					ArrayList<Status> replies = getReplies(twitter, dsp.getName(), status.getId());
					System.out.println("For this status/tweet we have: " + replies.size() + " comments.");

					for (Status reply : replies) {
						if (reply.getText() != null) {
							tweetKeywords = getTweetKeywords(reply.getText());
							if (!tweetKeywords.isEmpty()) {
								for (Entry<String, Map<String, String>> ent : crawlCriteria.entrySet()) {
									for (Entry<String, String> ient : ent.getValue().entrySet()) {
										for (String f : tweetKeywords) {
											boolean similarWords = false;
											// 1st check if words are equal
											if (f.equals(ient.getValue().toLowerCase())) {
												similarWords = true;
												System.out.println("Comment: '" + f + "' is same as '"
														+ ient.getValue().toLowerCase() + "'.");
											}
											// 2nd check if first word is substring of next one
											else if (f.toLowerCase().contains(ient.getValue().toLowerCase())) {
												similarWords = true;
												System.out.println("Comment: '" + ient.getValue().toLowerCase()
														+ "' is a substring of '" + f + "'.");
											}
											// Fist 3 letters same + Simon White of Catalysoft algorithm
											else if (f.toLowerCase().substring(0, 2) == ient.getValue().toLowerCase()
													.substring(0, 2)) {
												double similarity = compareStrings(f.toLowerCase(),
														ient.getValue().toLowerCase());
												if (similarity >= 70) {
													similarWords = true;
													System.out.println("Comment: '" + f + "' is similar to '"
															+ ient.getValue().toLowerCase() + "'.");
												}
											} else {
												similarWords = false;
//												System.out.println("Comment: '" + f + "' is NOT similar to '"
//														+ ient.getValue().toLowerCase() + "'.");
											}

//											// Another option
//											Integer noOfCharsDifferent = LevenshteinDistance.getDefaultInstance().apply(f, ient.getValue().toLowerCase());
//											Integer noOfCharsInWord = f.length();
//											if(noOfCharsInWord<=5 && noOfCharsDifferent<=1) {
//												similarWords=true;
//											}
//											else if(noOfCharsInWord<=7 && noOfCharsDifferent<=2) {
//												similarWords=true;
//											}
//											else if(noOfCharsInWord>7 && noOfCharsDifferent<=3) {
//												similarWords=true;
//											}
											if (similarWords == true) {
//												System.out.println("Comment '" + f + "' is similar/same to '" + ient.getValue().toLowerCase()+"'.");
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
								
								String location = twitter.showUser(reply.getUser().getId()).getLocation();
								String name = twitter.showUser(reply.getUser().getId()).getName();
								String gender = checkGender(user.getUserId(), name);

								if (!criteriId.isEmpty()) {
									tweets.add(new Tweet(new UID().toString(), user.getUserId(), reply.getId(),
											reply.getText(), tweetKeywords, reply.getCreatedAt(),
											"twitter.com/" + dsp.getName() + "/status/" + (status.getId()),
											dsp.getName(), "comment", "twitter.com/" + dsp.getName(), dsp.getName(),
											location, name, gender, categoryId, criteriId));
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
											location, name, gender, categoryId, criteriId));
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
				Paging page = new Paging(pageno, 20);
				if (dsp.getLastSavedTweetId() == Initial_Last_Saved_ID) {
					statuses.addAll(twitter.getUserTimeline(dsp.getName(), page));
				} else {
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

	/** @return an array of adjacent letter pairs contained in the input string */
	private static String[] letterPairs(String str) {
		int numPairs = str.length() - 1;
		String[] pairs = new String[numPairs];
		for (int i = 0; i < numPairs; i++) {
			pairs[i] = str.substring(i, i + 2);
		}
		return pairs;
	}

	/** @return an ArrayList of 2-character Strings. */
	private static ArrayList wordLetterPairs(String str) {
		ArrayList allPairs = new ArrayList();
		// Tokenize the string and put the tokens/words into an array
		String[] words = str.split("\\s");
		// For each word
		for (int w = 0; w < words.length; w++) {
			// Find the pairs of characters
			String[] pairsInWord = letterPairs(words[w]);
			for (int p = 0; p < pairsInWord.length; p++) {
				allPairs.add(pairsInWord[p]);
			}
		}
		return allPairs;
	}

	/** @return lexical similarity value in the range [0,1] */
	public static double compareStrings(String str1, String str2) {
		ArrayList pairs1 = wordLetterPairs(str1.toUpperCase());
		ArrayList pairs2 = wordLetterPairs(str2.toUpperCase());
		int intersection = 0;
		int union = pairs1.size() + pairs2.size();
		for (int i = 0; i < pairs1.size(); i++) {
			Object pair1 = pairs1.get(i);
			for (int j = 0; j < pairs2.size(); j++) {
				Object pair2 = pairs2.get(j);
				if (pair1.equals(pair2)) {
					intersection++;
					pairs2.remove(j);
					break;
				}
			}
		}
		return (2.0 * intersection) / union;
	}
	
	public String checkGender(String userId, String nameToCheck) throws IOException { 
		
		//need to check if name exists in DB
		String gender = "";
		String beforeFirstSpace = nameToCheck.split("\\ ")[0];
		Query query = new Query().addCriteria(Criteria.where("user_id").is(userId).and("name").is(beforeFirstSpace)).limit(1);
		List<Tweet> tweets = mongoTemplate.find(query, Tweet.class, COLLECTION_NAME_);
		//if it doesn't exist proceed, if does, return value from DB
		if (!tweets.isEmpty()) {
			gender = tweets.get(0).getUserGender();	
		}
		else {
			
			Genderize api = GenderizeIoAPI.create();
			gender = api.getGender(beforeFirstSpace).getGender();
			
			if (gender==null) {
				
			    String myKey = "MZFXaPVcYHbtZMbsXC";
			    URL url = new URL("https://gender-api.com/get?key=" + myKey + "&name=" + beforeFirstSpace);
			    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		
			    if (conn.getResponseCode() != 200) {
			      throw new RuntimeException("Error: " + conn.getResponseCode());
			    }
			    InputStreamReader input = new InputStreamReader(conn.getInputStream());
			    BufferedReader reader = new BufferedReader(input);
		
			    Gson gson = new Gson();
			    JsonObject json = gson.fromJson(reader, JsonObject.class);
			    gender = json.get("gender").getAsString();
			    conn.disconnect();
			}
		}
	    return gender;
	}
}
