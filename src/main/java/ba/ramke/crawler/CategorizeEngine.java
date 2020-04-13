package ba.ramke.crawler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.server.UID;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TimeZone;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.mongodb.BasicDBList;

import ba.ramke.dao.TweetRepository;
import ba.ramke.model.DataSource;
import ba.ramke.model.DataSourcePage;
import ba.ramke.model.Tweet;
import twitter4j.Paging;
import twitter4j.QueryResult;
import twitter4j.ResponseList;
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
				.setOAuthAccessTokenSecret(Twitter_API_access_token_secret).setHttpConnectionTimeout(100000);
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
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true).setOAuthConsumerKey(Twitter_API_key).setOAuthConsumerSecret(Twitter_API_secret_key)
				.setOAuthAccessToken(Twitter_API_access_token)
				.setOAuthAccessTokenSecret(Twitter_API_access_token_secret);
		TwitterFactory taf = new TwitterFactory(cb.build());
		Twitter twitter = taf.getInstance();
		int i = 0;
		Iterator<DataSourcePage> dspIterator = user.getTwitterPages().iterator();
		mainLoop: while (dspIterator.hasNext()) {
			DataSourcePage dsp = dspIterator.next();
			System.out.println(dsp.getName());
			ArrayList<Status> statuses = getStatusesFromTwitter(dsp, twitter);			
			for (int counter = statuses.size(); counter != 0; counter--) {
				Status status = statuses.get(counter - 1);
				lastCrawlTweetId = status.getId();
				System.out.println(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss:SSS").format(java.time.ZonedDateTime.now()) + " " + i + " tweets crawled ############################");
				if (dsp.getLastSavedTweetId().equals(status.getId())) {
					System.out.println("We came to last crawled tweet. Stop");
					if (tweets.size() != 0) {
						setLastCrawledTweet(user.getUserId(), dsp.getPageId(), lastCrawlTweetId);
						saveTweets(tweets);
					}
					i = 0;
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
									// For words that have more than 3 letters
									else if (f.length()>3 && f.toLowerCase().contains(ient.getValue().toLowerCase())) {
										similarWords = true;
										System.out.println("Tweet: '" + ient.getValue().toLowerCase()
												+ "' is a substring of '" + f + "'.");
									}
									// Fist 3 letters same + Simon White of Catalysoft algorithm
									// For words that have more than 3 letters
									else if (f.length()>3 && f.toLowerCase().substring(0, 3)
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
						
						String name = twitter.showUser(status.getUser().getId()).getName();
						String gender = checkGender(user.userId, name);
						String location = checkLocation(twitter, status, user.userId, name);

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

//					/// COMMENTS-REPLIES
//
//					ArrayList<Status> replies = getReplies(twitter, dsp.getName(), status.getId());
//					System.out.println("For this status/tweet we have: " + replies.size() + " comments.");
//
//					for (Status reply : replies) {
//						if (reply.getText() != null) {
//							tweetKeywords = getTweetKeywords(reply.getText());
//							if (!tweetKeywords.isEmpty()) {
//								for (Entry<String, Map<String, String>> ent : crawlCriteria.entrySet()) {
//									for (Entry<String, String> ient : ent.getValue().entrySet()) {
//										for (String f : tweetKeywords) {
//											boolean similarWords = false;
//											// 1st check if words are equal
//											if (f.equals(ient.getValue().toLowerCase())) {
//												similarWords = true;
//												System.out.println("Comment: '" + f + "' is same as '"
//														+ ient.getValue().toLowerCase() + "'.");
//											}
//											// 2nd check if first word is substring of next one
//											else if (f.toLowerCase().contains(ient.getValue().toLowerCase())) {
//												similarWords = true;
//												System.out.println("Comment: '" + ient.getValue().toLowerCase()
//														+ "' is a substring of '" + f + "'.");
//											}
//											// Fist 3 letters same + Simon White of Catalysoft algorithm
//											else if (f.toLowerCase().substring(0, 2) == ient.getValue().toLowerCase()
//													.substring(0, 2)) {
//												double similarity = compareStrings(f.toLowerCase(),
//														ient.getValue().toLowerCase());
//												if (similarity >= 70) {
//													similarWords = true;
//													System.out.println("Comment: '" + f + "' is similar to '"
//															+ ient.getValue().toLowerCase() + "'.");
//												}
//											} else {
//												similarWords = false;
////												System.out.println("Comment: '" + f + "' is NOT similar to '"
////														+ ient.getValue().toLowerCase() + "'.");
//											}
//
////											// Another option
////											Integer noOfCharsDifferent = LevenshteinDistance.getDefaultInstance().apply(f, ient.getValue().toLowerCase());
////											Integer noOfCharsInWord = f.length();
////											if(noOfCharsInWord<=5 && noOfCharsDifferent<=1) {
////												similarWords=true;
////											}
////											else if(noOfCharsInWord<=7 && noOfCharsDifferent<=2) {
////												similarWords=true;
////											}
////											else if(noOfCharsInWord>7 && noOfCharsDifferent<=3) {
////												similarWords=true;
////											}
//											if (similarWords == true) {
////												System.out.println("Comment '" + f + "' is similar/same to '" + ient.getValue().toLowerCase()+"'.");
//												if (categoryId.contains(ent.getKey())) {
//													criteriId.add(ient.getKey().toString());
//												} else {
//													categoryId.add(ent.getKey().toString());
//													criteriId.add(ient.getKey().toString());
//												}
//											}
//										}
//									}
//								}
//								
//								String location = twitter.showUser(reply.getUser().getId()).getLocation();
//								String name = twitter.showUser(reply.getUser().getId()).getName();
////								String gender = checkGender(user.getUserId(), name);
//								String gender = "test";
//								
//								if (!criteriId.isEmpty()) {
//									tweets.add(new Tweet(new UID().toString(), user.getUserId(), reply.getId(),
//											reply.getText(), tweetKeywords, reply.getCreatedAt(),
//											"twitter.com/" + dsp.getName() + "/status/" + (status.getId()),
//											dsp.getName(), "comment", "twitter.com/" + dsp.getName(), dsp.getName(),
//											location, name, gender, categoryId, criteriId));
//									criteriId.toString();
//									criteriId = new ArrayList<String>();
//									categoryId = new ArrayList<String>();
//									tweetKeywords = new ArrayList<String>();
//								} else {
//									categoryId.add("uncategorized");
//									criteriId.add("uncategorized");
//									tweets.add(new Tweet(new UID().toString(), user.getUserId(), reply.getId(),
//											reply.getText(), tweetKeywords, reply.getCreatedAt(),
//											"twitter.com/" + dsp.getName() + "/status/" + (status.getId()),
//											dsp.getName(), "comment", "twitter.com/" + dsp.getName(), dsp.getName(),
//											location, name, gender, categoryId, criteriId));
//									criteriId = new ArrayList<String>();
//									categoryId = new ArrayList<String>();
//									tweetKeywords = new ArrayList<String>();
//								}
//
//							}
//						}
//					}

					///
				}

				i++;
				if (i == statuses.size()) {
					System.out.println(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss:SSS").format(java.time.ZonedDateTime.now()) + " " + i + " TWEETS crawled. Stop");
					if (tweets.size() != 0) {
						setLastCrawledTweet(user.getUserId(), dsp.getPageId(), lastCrawlTweetId);
						saveTweets(tweets);
					}
					tweets = new ArrayList<Tweet>();
					i = 0;
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
				Paging page = new Paging(pageno, 300);
				if (dsp.getLastSavedTweetId() == Initial_Last_Saved_ID) {
					statuses.addAll(twitter.getUserTimeline(dsp.getName(), page));
				} else {
					// get from
					Paging page2 = new Paging(pageno, dsp.getLastSavedTweetId());
					ResponseList<Status> aaaa = twitter.getUserTimeline(dsp.getName(), page2);
					if(aaaa!=null) {
						statuses.addAll(aaaa);
					}
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
				sleep(910000);
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
			sleep(910000);
		}
		return replies;
	}

	public List<String> getTweetKeywords(String message) {
		String trimedMessage = message.replaceAll("\\p{P}", " ").toLowerCase().trim().replaceAll("(\\s)+", "$1")
				.replaceAll("[\n\r]", "");
		String[] tweets = trimedMessage.split(" ");
		List<String> tweetKeywords = new ArrayList<String>();
		List<String> unWantedKeywords = Arrays.asList("ili","ali","ako","vas","nas","dok","još","iza","oko","vrh","kod","niz","nad","pri","pod");
		
		for (String s : tweets) {
			if (s.length() > 2 && !tweetKeywords.contains(s) && !unWantedKeywords.contains(s)) {
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
		
	public void updateCategoryForStatus(String userId, Long pageId, List<String> categoryId) {
		Query query = new Query(
				Criteria.where("user_id").is(userId).and("tweetId").is(pageId));
		mongoTemplate.updateFirst(query, new Update().set("categoryId", categoryId), Tweet.class,
				COLLECTION_NAME_);
		System.out.println("OK");
	}
	
	public void updateCriteriaForStatus(String userId, Long pageId, List<String> criteriaId) {
		Query query = new Query(
				Criteria.where("user_id").is(userId).and("tweetId").is(pageId));
		mongoTemplate.updateFirst(query, new Update().set("criteriaId", criteriaId), Tweet.class,
				COLLECTION_NAME_);
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

			gender = "noGender";
//			if (gender==null) {
//			    String myKey = "MZFXaPVcYHbtZMbsXC";
//			    URL url = new URL("https://gender-api.com/get?key=" + myKey + "&name=" + beforeFirstSpace);
//			    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//		
//			    if (conn.getResponseCode() != 200) {
//			      throw new RuntimeException("Error: " + conn.getResponseCode());
//			    }
//			    InputStreamReader input = new InputStreamReader(conn.getInputStream());
//			    BufferedReader reader = new BufferedReader(input);
//		
//			    Gson gson = new Gson();
//			    JsonObject json = gson.fromJson(reader, JsonObject.class);
//			    gender = json.get("gender").getAsString();
//			    conn.disconnect();
//			}
		}
	    return gender;
	}
	
public String checkLocation(Twitter twitter, Status status, String userId, String nameToCheck) throws IOException { 
		//need to check if location of user exists in DB
		String location = "";
		String beforeFirstSpace = nameToCheck.split("\\ ")[0];
		Query query = new Query().addCriteria(Criteria.where("user_id").is(userId).and("name").is(beforeFirstSpace)).limit(1);
		List<Tweet> tweets = mongoTemplate.find(query, Tweet.class, COLLECTION_NAME_);
		//if it doesn't exist proceed, if does, return value from DB
		if (!tweets.isEmpty()) {
			location = tweets.get(0).getUserLocation();	
		}
		else {

			try {
				location = twitter.showUser(status.getUser().getId()).getLocation();
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	    return location;
	}
	
	public void recategorize(DataSource user, Map<String, Map<String, String>> crawlCriteria, List<Tweet> listOfTweets)
			throws TwitterException, IOException {
		System.out.println("In method");
		List<String> tweetKeywords = new ArrayList<String>();
		List<String> criteriId = new ArrayList<String>();
		List<String> categoryId = new ArrayList<String>();

		for (int counter = 0; counter < listOfTweets.size(); counter++) {
			System.out.println("Tweet " + counter + " of " + listOfTweets.size());
			if (listOfTweets.get(counter).getTweetKeywords() != null) {
				tweetKeywords = listOfTweets.get(counter).getTweetKeywords();
				if (!tweetKeywords.isEmpty()) {
					for (Entry<String, Map<String, String>> ent : crawlCriteria.entrySet()) {
						for (Entry<String, String> ient : ent.getValue().entrySet()) {
							
							for (String f : tweetKeywords) {
								boolean similarWords = false;
								// 1st check if words are equal
								if (f.equals(ient.getValue().toLowerCase())) {
									similarWords = true;
									System.out.println(
											"Tweet: '" + f + "' is same as '" + ient.getValue().toLowerCase() + "'.");
								}
								// 2nd check if first word is substring of next one
								// For words that have more than 3 letters
								else if (f.length()>3 && (f.toLowerCase().substring(0, 3)
										.equals(ient.getValue().toLowerCase().substring(0, 3))) && (f.toLowerCase().contains(ient.getValue().toLowerCase()))) {
									similarWords = true;
									System.out.println("Tweet: '" + ient.getValue().toLowerCase()
											+ "' is a substring of '" + f + "'.");
								}
								// Fist 3 letters same + Simon White of Catalysoft algorithm
								// For words that have more than 3 letters
								else if (f.length()>3 && f.toLowerCase().substring(0, 3)
										.equals(ient.getValue().toLowerCase().substring(0, 3))) {
									double similarity = compareStrings(f.toLowerCase(), ient.getValue().toLowerCase());
									if (similarity >= 0.7) {
										similarWords = true;
										System.out.println("Tweet: '" + f + "' is similar to '"
												+ ient.getValue().toLowerCase() + "'.");
									}
								} else {
									similarWords = false;
								}

								if (similarWords == true) {
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
						updateCategoryForStatus(user.getUserId(), listOfTweets.get(counter).getTweetId(), categoryId);
						updateCriteriaForStatus(user.getUserId(), listOfTweets.get(counter).getTweetId(), criteriId);
						criteriId = new ArrayList<String>();
						categoryId = new ArrayList<String>();
						tweetKeywords = new ArrayList<String>();
					} else {
						categoryId.add("uncategorized");
						criteriId.add("uncategorized");
						updateCategoryForStatus(user.getUserId(), listOfTweets.get(counter).getTweetId(), categoryId);
						updateCriteriaForStatus(user.getUserId(), listOfTweets.get(counter).getTweetId(), criteriId);
						criteriId = new ArrayList<String>();
						categoryId = new ArrayList<String>();
						tweetKeywords = new ArrayList<String>();
					}
					System.out.println("Tweet: '" + counter + " out of " + listOfTweets.size() + " is recategorized.");
				}

			}
		}
	}
	
	public void lastTweetId(String user) {

		String location = "C:\\Users\\rkorda\\Desktop\\rad\\SMAMT podaci\\zadnji";
		File[] files = new File(location).listFiles();
		for (File file : files) {
			String FileName = location + "\\" + file.getName();
			try {
				ArrayList<JSONObject> jsons = ReadJSONLastTweet(new File(FileName), "UTF-8", user, file.getName());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}
		
	public void parse(String user) {

		String location = "C:\\Users\\rkorda\\Desktop\\rad\\SMAMT podaci\\ZaObraditi";
		File[] files = new File(location).listFiles();
		for (File file : files) {
			System.out.println("File: " + file.getName());
			String FileName = location + "\\" + file.getName();
			try {
				ArrayList<JSONObject> jsons = ReadJSON(new File(FileName), "UTF-8", user);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}
	
		public synchronized ArrayList<JSONObject> ReadJSON(File MyFile,String Encoding,String user) throws FileNotFoundException, ParseException {
		    Scanner scn=new Scanner(MyFile,Encoding);
		    ArrayList<JSONObject> json=new ArrayList<JSONObject>();
		    List<Tweet> tweets = new ArrayList<Tweet>();
			List<String> criteriId = new ArrayList<String>();
			List<String> categoryId = new ArrayList<String>();
			List<String> tweetKeywords = new ArrayList<String>();
		//Reading and Parsing Strings to Json
		    while(scn.hasNext()){
		        JSONObject obj= (JSONObject) new JSONParser().parse(scn.nextLine());
		        json.add(obj);
		    }
		//Here Printing Json Objects
		    int i;
		    i = 1;
		    for(JSONObject obj : json){
		    	System.out.println("PROCESSING: " + i + " of " + json.size());
		    	i++;
			    Long id = (long) obj.get("id");
			    JSONArray objs = (JSONArray) obj.get("reply_to");
			    JSONObject getFirstObject = (JSONObject) objs.get(0);
			    Object ime = getFirstObject.get("username");
			    String username = ime.toString();			    
			    String name = (String) obj.get("name");
			    String tweet = (String) obj.get("tweet");
			    
			    tweetKeywords = getTweetKeywords(tweet);
			    String link = (String) obj.get("link");
			    
			    Long dateCreatedString = (Long) obj.get("created_at");
				long test_timestamp = dateCreatedString;
				LocalDateTime triggerTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(test_timestamp), TimeZone.getDefault().toZoneId());  
				Date date = Date.from( triggerTime.atZone( ZoneId.systemDefault()).toInstant());	  
			  
			    String gender = returnGender(username);
			    String location = returnLocation(username);
			    
			    categoryId.add("uncategorized");
				criteriId.add("uncategorized");
				tweetKeywords.add("");
			    
			    tweets.add(new Tweet(new UID().toString(), user, id, tweet, tweetKeywords, date, link, username, "status", "twitter.com/" + username, 
			    		username, location, name, gender, categoryId, criteriId));
			    
			    criteriId = new ArrayList<String>();
				categoryId = new ArrayList<String>();
				tweetKeywords = new ArrayList<String>();
			    
		    }
		    saveTweets(tweets);
		    return json;
		}
		
		public synchronized ArrayList<JSONObject> ReadJSONLastTweet(File MyFile,String Encoding,String user, String fileName) throws FileNotFoundException, ParseException {
		    Scanner scn=new Scanner(MyFile,Encoding);
		    ArrayList<JSONObject> json=new ArrayList<JSONObject>();
		    List<Tweet> tweets = new ArrayList<Tweet>();
			List<String> criteriId = new ArrayList<String>();
			List<String> categoryId = new ArrayList<String>();
			List<String> tweetKeywords = new ArrayList<String>();
		//Reading and Parsing Strings to Json
		    JSONObject obj= (JSONObject) new JSONParser().parse(scn.nextLine());
		//Here Printing Json Objects
		    for(int i=1; i<=1; i++){
			    Long id = (long) obj.get("id");
			    JSONArray objs = (JSONArray) obj.get("reply_to");
			    JSONObject getFirstObject = (JSONObject) objs.get(0);
			    Object ime = getFirstObject.get("username");
			    String username = ime.toString();
			    
			    String date = (String) obj.get("date");
			    String time = (String) obj.get("time");
			    //when I want to run twint
//			    System.out.println("filename: " + fileName + "| " + "twint -u " + username + " --since \"" + date + " " + time + "\"" + " -o " + username +".json --json");
			    //only get last
			    System.out.println(username + ": " + id);

			    
		    }
		    return json;
		}
		
		public void spasiJedanTweet(String user) {
			ConfigurationBuilder cb = new ConfigurationBuilder();
			cb.setDebugEnabled(true).setOAuthConsumerKey(Twitter_API_key).setOAuthConsumerSecret(Twitter_API_secret_key)
			.setOAuthAccessToken(Twitter_API_access_token)
			.setOAuthAccessTokenSecret(Twitter_API_access_token_secret);
			TwitterFactory taf = new TwitterFactory(cb.build());
			Twitter twitter = taf.getInstance();
			List<Tweet> tweets = new ArrayList<Tweet>();
			Long id = 84294829L;
		    String username = "";			    
		    String name = "";
		    String tweet = "blaaaaa lsdkalkda";
		    String link = "";
			List<String> criteriId = new ArrayList<String>();
			List<String> categoryId = new ArrayList<String>();
			List<String> tweetKeywords = new ArrayList<String>();
			String location = "";
			String gender = "";
			
			try {
				Status aaa = twitter.showStatus(1248676147665256449L);
				Date blaaa = aaa.getCreatedAt();
				
				tweets.add(new Tweet(new UID().toString(), user, id, tweet, tweetKeywords, blaaa, link, username,	"status", "twitter.com/" + username, 
			    		username, location, name, gender, categoryId, criteriId));
				
				saveTweets(tweets);
				
				
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			


		}


		public String returnGender (String username) {
			String gender = "";
			if("Dragan_Covic".equals(username)){gender="male";}
			else if("klixba".equals(username)){gender="unknown";}
			else if("DraganMektic".equals(username)){gender="male";}
			else if("faktorba".equals(username)){gender="unknown";}
			else if("radiosarajevo".equals(username)){gender="unknown";}
			else if("Sourceba".equals(username)){gender="unknown";}
			else if("cinbih".equals(username)){gender="unknown";}
			else if("BIRN_BiH".equals(username)){gender="unknown";}
			else if("N1infoSA".equals(username)){gender="unknown";}
			else if("zurnal_info".equals(username)){gender="unknown";}
			else if("DnevniAvaz".equals(username)){gender="unknown";}
			else if("Oslobodjenjeba".equals(username)){gender="unknown";}
			else if("AJBalkans".equals(username)){gender="unknown";}
			else if("aa_balkans".equals(username)){gender="unknown";}
			else if("tv_face".equals(username)){gender="unknown";}
			else if("okanalba".equals(username)){gender="unknown";}
			else if("B_Izetbegovic".equals(username)){gender="male";}
			else if("DrZvizdic".equals(username)){gender="male";}
			else if("suljagicemir1".equals(username)){gender="male";}
			else if("NiksicN".equals(username)){gender="male";}
			else if("PedjaKojovic".equals(username)){gender="male";}
			else if("ADiskriminacija".equals(username)){gender="unknown";}
			else if("GlasSrpske".equals(username)){gender="unknown";}
			else if("RSE_Balkan".equals(username)){gender="unknown";}
			else if("AtvBanjaluka".equals(username)){gender="unknown";}
			else if("RSrpska".equals(username)){gender="unknown";}
			else if("rtvbn1".equals(username)){gender="unknown";}
			else if("RTRSvijesti".equals(username)){gender="unknown";}
			else if("AmirZukic".equals(username)){gender="male";}
			else if("harismrkonjan1".equals(username)){gender="male";}
			else if("MultimedijaBHRT".equals(username)){gender="unknown";}
			else if("analiziraj".equals(username)){gender="unknown";}
			else if("MdinoDino".equals(username)){gender="male";}
			else if("Ivana__Maric".equals(username)){gender="female";}
			else if("SrkiPuhalo".equals(username)){gender="male";}
			else if("ArduanaPribinja".equals(username)){gender="female";}
			else if("Ridjobrki".equals(username)){gender="male";}
			else if("Amra_Silajdzic".equals(username)){gender="female";}
			else if("AlmedinSisic".equals(username)){gender="male";}
			else if("dzoni____".equals(username)){gender="male";}
			else if("ceycky".equals(username)){gender="male";}
			else if("goghie_m".equals(username)){gender="male";}
			else if("pravnik_".equals(username)){gender="male";}
			else if("agencijafena".equals(username)){gender="unknown";}
			else if("bhnovinari".equals(username)){gender="unknown";}
			else if("dw_bhs".equals(username)){gender="unknown";}
			else if("BanjalukaNet".equals(username)){gender="unknown";}
			else if("srpska_cafe".equals(username)){gender="unknown";}
			else if("elmasarajevo".equals(username)){gender="female";}
			else if("DaniHadzovic".equals(username)){gender="male";}
			else if("DouceAlma".equals(username)){gender="female";}
			else if("BanjalukaCom".equals(username)){gender="unknown";}
			else if("tibihorg".equals(username)){gender="unknown";}
			else if("ImerM1".equals(username)){gender="male";}
			else if("darkokrznaric".equals(username)){gender="male";}
			else if("MirsadAbazovic".equals(username)){gender="male";}
			else if("Vijesti_ba".equals(username)){gender="unknown";}
			else if("dijalekticar".equals(username)){gender="male";}
			else if("DnevnikTVSA".equals(username)){gender="unknown";}
			else if("mediacentar".equals(username)){gender="unknown";}
			else if("BihDrustvo".equals(username)){gender="unknown";}
			else if("Direkcija_EI".equals(username)){gender="unknown";}
			else if("cyberbosanka".equals(username)){gender="female";}
			else if("JBorovina".equals(username)){gender="female";}
			else if("zgembolina".equals(username)){gender="female";}
			else if("MersihaN".equals(username)){gender="female";}
			else if("cufkatron".equals(username)){gender="female";}
			else if("Burrza1".equals(username)){gender="male";}
			else if("sirbega".equals(username)){gender="male";}
			else if("DrugogSistema".equals(username)){gender="female";}
			else if("Debela_Teta".equals(username)){gender="female";}
			else if("kenankadra".equals(username)){gender="male";}
			else if("helemnejsee".equals(username)){gender="male";}
			else if("ualittlemonster".equals(username)){gender="female";}
			else if("dodjoskaa".equals(username)){gender="female";}
			else if("NaKitiMiBor".equals(username)){gender="male";}
			else if("CharlieSixTango".equals(username)){gender="male";}
			else if("dobarpahajd".equals(username)){gender="male";}
			else if("BigEldinovsky".equals(username)){gender="male";}
			return gender;
		}
		
		public String returnLocation (String username) {
			String location = "";
			if("Dragan_Covic".equals(username)){location="Mostar";}
			else if("klixba".equals(username)){location="Bosna i Hercegovina";}
			else if("DraganMektic".equals(username)){location="Prnjavor";}
			else if("faktorba".equals(username)){location="Sarajevo";}
			else if("radiosarajevo".equals(username)){location="Sarajevo";}
			else if("Sourceba".equals(username)){location="Sarajevo";}
			else if("cinbih".equals(username)){location="Sarajevo";}
			else if("BIRN_BiH".equals(username)){location="Sarajevo";}
			else if("N1infoSA".equals(username)){location="Sarajevo";}
			else if("zurnal_info".equals(username)){location="Bosna i Hercegovina";}
			else if("DnevniAvaz".equals(username)){location="Sarajevo";}
			else if("Oslobodjenjeba".equals(username)){location="Sarajevo";}
			else if("AJBalkans".equals(username)){location="Sarajevo";}
			else if("aa_balkans".equals(username)){location="Sarajevo";}
			else if("tv_face".equals(username)){location="Sarajevo";}
			else if("okanalba".equals(username)){location="Bosna i Hercegovina";}
			else if("B_Izetbegovic".equals(username)){location="Sarajevo";}
			else if("DrZvizdic".equals(username)){location="Bosna i Hercegovina";}
			else if("suljagicemir1".equals(username)){location="Bratunac/Sarajevo";}
			else if("NiksicN".equals(username)){location="Bosna i Hercegovina";}
			else if("PedjaKojovic".equals(username)){location="Sarajevo";}
			else if("ADiskriminacija".equals(username)){location="x";}
			else if("GlasSrpske".equals(username)){location="Banja Luka";}
			else if("RSE_Balkan".equals(username)){location="Prag";}
			else if("AtvBanjaluka".equals(username)){location="Banja Luka";}
			else if("RSrpska".equals(username)){location="Republika Srpska";}
			else if("rtvbn1".equals(username)){location="Bijeljina";}
			else if("RTRSvijesti".equals(username)){location="Banja Luka";}
			else if("AmirZukic".equals(username)){location="Sarajevo";}
			else if("harismrkonjan1".equals(username)){location="Sarajevo";}
			else if("MultimedijaBHRT".equals(username)){location="Bosna i Hercegovina";}
			else if("analiziraj".equals(username)){location="x";}
			else if("MdinoDino".equals(username)){location="Sarajevo";}
			else if("Ivana__Maric".equals(username)){location="Sarajevo";}
			else if("SrkiPuhalo".equals(username)){location="Banja Luka";}
			else if("ArduanaPribinja".equals(username)){location="Sarajevo";}
			else if("Ridjobrki".equals(username)){location="Bosna i Hercegovina";}
			else if("Amra_Silajdzic".equals(username)){location="Rim";}
			else if("AlmedinSisic".equals(username)){location="Bosna i Hercegovina";}
			else if("dzoni____".equals(username)){location="Folsom Prison";}
			else if("ceycky".equals(username)){location="Tuzla";}
			else if("goghie_m".equals(username)){location="Tuzla";}
			else if("pravnik_".equals(username)){location="Bihać";}
			else if("agencijafena".equals(username)){location="Bosna i Hercegovina";}
			else if("bhnovinari".equals(username)){location="Bosna i Hercegovina";}
			else if("dw_bhs".equals(username)){location="Bonn";}
			else if("BanjalukaNet".equals(username)){location="Banja Luka";}
			else if("srpska_cafe".equals(username)){location="Banja Luka";}
			else if("elmasarajevo".equals(username)){location="x";}
			else if("DaniHadzovic".equals(username)){location="Bosna i Hercegovina";}
			else if("DouceAlma".equals(username)){location="Bosna i Hercegovina";}
			else if("BanjalukaCom".equals(username)){location="Banja Luka";}
			else if("tibihorg".equals(username)){location="Bosna i Hercegovina";}
			else if("ImerM1".equals(username)){location="Barcelona/Sarajevo";}
			else if("darkokrznaric".equals(username)){location="x";}
			else if("MirsadAbazovic".equals(username)){location="Sarajevo";}
			else if("Vijesti_ba".equals(username)){location="Sarajevo";}
			else if("dijalekticar".equals(username)){location="ETAR";}
			else if("DnevnikTVSA".equals(username)){location="Sarajevo";}
			else if("mediacentar".equals(username)){location="Sarajevo";}
			else if("BihDrustvo".equals(username)){location="x";}
			else if("Direkcija_EI".equals(username)){location="Sarajevo";}
			else if("cyberbosanka".equals(username)){location="Zenica";}
			else if("JBorovina".equals(username)){location="Sarajevo";}
			else if("zgembolina".equals(username)){location="Sarajevo";}
			else if("MersihaN".equals(username)){location="Sarajevo";}
			else if("cufkatron".equals(username)){location="Bosna i Hercegovina";}
			else if("Burrza1".equals(username)){location="Uhljebistan";}
			else if("sirbega".equals(username)){location="Gračanica/Lukavac";}
			else if("DrugogSistema".equals(username)){location="Sarajevo";}
			else if("Debela_Teta".equals(username)){location="x";}
			else if("kenankadra".equals(username)){location="Minhen";}
			else if("helemnejsee".equals(username)){location="Bosna i Hercegovina";}
			else if("ualittlemonster".equals(username)){location="x";}
			else if("dodjoskaa".equals(username)){location="x";}
			else if("NaKitiMiBor".equals(username)){location="Lukavac/Newcastle";}
			else if("CharlieSixTango".equals(username)){location="Sanski Most";}
			else if("dobarpahajd".equals(username)){location="x";}
			else if("BigEldinovsky".equals(username)){location="Visoko";}

			return location;
		}
		
		public void updateLastSavedTweet(String userId) {
			Map<String, Long> myMap = new HashMap<String, Long>();
			myMap.put("aa_balkans", 1248716298806267909L);
			myMap.put("ADiskriminacija", 1248144904963919872L);
			myMap.put("agencijafena", 1248677970048409600L);
			myMap.put("AJBalkans", 1248861101401559040L);
			myMap.put("AlmedinSisic", 1248861876823482369L);
			myMap.put("AmirZukic", 1244599694673797120L);
			myMap.put("Amra_Silajdzic", 1215165595815227392L);
			myMap.put("analiziraj", 1248687095469813760L);
			myMap.put("ArduanaPribinja", 1248654804760645632L);
			myMap.put("AtvBanjaluka", 1248567508556222464L);
			myMap.put("BanjalukaCom", 1248245335622733824L);
			myMap.put("BanjalukaNet", 1248863021440647168L);
			myMap.put("bhnovinari", 1248602487986118661L);
			myMap.put("BigEldinovsky", 1248863468205281282L);
			myMap.put("BihDrustvo", 1248628497154576384L);
			myMap.put("BIRN_BiH", 1248636445348241408L);
			myMap.put("Burrza1", 1248751118764646400L);
			myMap.put("B_Izetbegovic", 1148233400010117120L);
			myMap.put("ceycky", 1248840834646237184L);
			myMap.put("CharlieSixTango", 1248706839442796546L);
			myMap.put("cinbih", 1246061311311708160L);
			myMap.put("cufkatron", 1248856849119617024L);
			myMap.put("cyberbosanka", 1248641454899113986L);
			myMap.put("DaniHadzovic", 1248732597720698881L);
			myMap.put("darkokrznaric", 1248765803278807042L);
			myMap.put("Debela_Teta", 1248619372572069891L);
			myMap.put("dijalekticar", 1248258548359888900L);
			myMap.put("Direkcija_EI", 1243924595754795010L);
			myMap.put("DnevniAvaz", 1248862611640389632L);
			myMap.put("DnevnikTVSA", 1211918787592826880L);
			myMap.put("dobarpahajd", 1248861735466995714L);
			myMap.put("dodjoskaa", 1248861391215366145L);
			myMap.put("DouceAlma", 1248864017902419970L);
			myMap.put("DraganMektic", 1248681225310670849L);
			myMap.put("Dragan_Covic", 1248213877025845250L);
			myMap.put("DrugogSistema", 1248838591322763264L);
			myMap.put("DrZvizdic", 1247109874296766465L);
			myMap.put("dw_bhs", 1248618482373029888L);
			myMap.put("dzoni____", 1248733058506862594L);
			myMap.put("elmasarajevo", 1248710543403364353L);
			myMap.put("faktorba", 1248854115070984193L);
			myMap.put("GlasSrpske", 1248677206303399937L);
			myMap.put("goghie_m", 1248758770559303684L);
			myMap.put("harismrkonjan1", 1248625527251849221L);
			myMap.put("helemnejsee", 1248755040619282432L);
			myMap.put("ImerM1", 1248730403776729088L);
			myMap.put("Ivana__Maric", 1248676901255876611L);
			myMap.put("JBorovina", 1248697001665069056L);
			myMap.put("kenankadra", 1248708338847752195L);
			myMap.put("klixba", 1248861646602342400L);
			myMap.put("MdinoDino", 1248729487539388418L);
			myMap.put("mediacentar", 1248708563465392128L);
			myMap.put("MersihaN", 1248743065495449601L);
			myMap.put("MirsadAbazovic", 1248859952623554561L);
			myMap.put("MultimedijaBHRT", 1248863797173006341L);
			myMap.put("N1infoSA", 1248862878658166785L);
			myMap.put("NaKitiMiBor", 1248861304951050240L);
			myMap.put("NiksicN", 1248613194718797826L);
			myMap.put("okanalba", 1248587374193512450L);
			myMap.put("Oslobodjenjeba", 1248851595175120898L);
			myMap.put("PedjaKojovic", 1248648590538539008L);
			myMap.put("pravnik_", 1248773824386076673L);
			myMap.put("radiosarajevo", 1248699769951866882L);
			myMap.put("Ridjobrki", 1248696623359803393L);
			myMap.put("RSE_Balkan", 1248709111375593476L);
			myMap.put("RSrpska", 1248560994357477377L);
			myMap.put("RTRSvijesti", 1248858753153355776L);
			myMap.put("rtvbn1", 1248863891569999873L);
			myMap.put("sirbega", 1248823165582786560L);
			myMap.put("Sourceba", 1248535896795873280L);
			myMap.put("SrkiPuhalo", 1248862150078148610L);
			myMap.put("srpska_cafe", 1248700339655753730L);
			myMap.put("suljagicemir1", 1248859555427241985L);
			myMap.put("tv_face", 1175159737366863873L);
			myMap.put("ualittlemonster", 1248733059010236422L);
			myMap.put("Vijesti_ba", 1248864167718727680L);
			myMap.put("zgembolina", 1248694960679014400L);
			myMap.put("zurnal_info", 1248662231618850823L);
			
			Iterator it = myMap.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry pair = (Map.Entry)it.next();
		        System.out.println(pair.getKey() + " = " + pair.getValue());
		        it.remove(); // avoids a ConcurrentModificationException

				Query query = new Query(
						Criteria.where("_id").is(userId).and("twitterPages").elemMatch(Criteria.where("_id").is(pair.getKey())));
				mongoTemplate.updateFirst(query, new Update().set("twitterPages.$.lastSavedTweetId", pair.getValue()), DataSource.class,
						COLLECTION_NAME);
				System.out.println("OK");
			}
		}
}
