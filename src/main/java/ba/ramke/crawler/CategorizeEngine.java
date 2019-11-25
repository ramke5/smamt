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
import com.restfb.Connection;
import com.restfb.FacebookClient;
import com.restfb.FacebookClient.AccessToken;
import com.restfb.Version;
import com.restfb.types.Comment;
import com.restfb.types.Message;
import com.restfb.types.Post;
import com.restfb.DefaultFacebookClient;

import ba.ramke.model.DataSource;
import ba.ramke.model.DataSourcePage;
import ba.ramke.model.Feed;




import static java.lang.System.out;
import com.restfb.types.User;


@Repository
public class CategorizeEngine {

	@Autowired
	private MongoTemplate mongoTemplate;
	//1053360284704688,5ff161f28bd46076fbc44a4cabe2b9a9
	private static final String APP_ID = "371307700457729";
	private static final String APP_SECRET = "e2100b59c8d1eea3f802d25f83410098";
	private static final String USER_ACCESS_TOKEN = "EAAFRs8ZBWkQEBADg6bAJNHrjZBB76eYoi21b7Wj5bAErz1A3eto4jfX8gOAOdsbJPIvKUqxXZBGZCNOZAysFYH7x99KqjiCEUzZCrtNWQwwZBKKdtc3ITa0nsckFA8uG6P2KsdMGNKGWr1QB8jy43sWUFatpaR8ZCDQVlB0oQXSj0nRUdBA7N91avgoVmQczBP99c1VjKTzVWS3qQxnXGWHVHzIANsTu6QVhjiVrmjXimQZDZD";
	private static final String URI = "https://www.facebook.com/connect/login_success.html";
	private final String COLLECTION_NAME = "datasource";
	private final String COLLECTION_NAME_ = "categorizedfeeds";
	private FacebookClient fbClient;

	public CategorizeEngine() {
		AccessToken token = new DefaultFacebookClient(Version.LATEST).obtainAppAccessToken(APP_ID, APP_SECRET);
		fbClient = new DefaultFacebookClient(token.getAccessToken(), Version.LATEST);
		
		AccessToken uat = new DefaultFacebookClient(Version.LATEST).obtainUserAccessToken(APP_ID, APP_SECRET, URI, USER_ACCESS_TOKEN);
		
		System.out.println("BECIR TOKEN");
		System.out.println(token);
	}

	public void setMongoTemplate(MongoTemplate mongoTemplate2) {
		mongoTemplate = mongoTemplate2;
	}
	

	@SuppressWarnings("null")
	public void fetchConnectionsEx() {
		  final FacebookClient facebookClient25 = null;
		    out.println("* Fetching connections *");

		    Connection<User> myFriends = facebookClient25.fetchConnection("me/friends", User.class);
		    Connection<Post> myFeed = facebookClient25.fetchConnection("me/feed", Post.class);

		    out.println("Count of my friends: " + myFriends.getData().size());

		    if (!myFeed.getData().isEmpty())
		      out.println("First item in my feed: " + myFeed.getData().get(0).getMessage());
		  }
	
	public void categorize(DataSource user, Map<String, Map<String, String>> crawlCriteria) {
		System.out.println("In method");
		String lastCrawlFeedId = "";
		List<String> feedKeywords = new ArrayList<String>();
		List<Feed> feeds = new ArrayList<Feed>();
		List<String> criteriId = new ArrayList<String>();
		List<String> categoryId = new ArrayList<String>();
		int i = 0;
		Iterator<DataSourcePage> dspIterator = user.getFacebookPages().iterator();
		mainLoop: while (dspIterator.hasNext()) {
			DataSourcePage dsp = dspIterator.next();
			int j = 1;
			System.out.println(dsp.getName());
//			fetchConnectionsEx();
//			Connection<Post> posts = fbClient.fetchConnection(dsp.getName().concat("/feed"), Post.class);
					
//			User user2 = fbClient.fetchObject("me", User.class);

//			Connection<Post> posts = fbClient.fetchConnection("me/feed", Post.class);
			
			Post pp = new Post();
			
			Connection<Post> posts = 



			for (List<Post> post : posts) {
				System.out.println(i + " feeds crawled ############################");
				for (Post feed : post) {
					if (j == 1) {
						lastCrawlFeedId = feed.getId();
						j++;
					} else if (i == 2000) {
						System.out.println(i + " FEEDS crawled. Stop");
						setLastCrawledFeed(user.getUserId(), dsp.getPageId(), lastCrawlFeedId);
						if (feeds.size() != 0)
							saveFeeds(feeds);
						feeds = new ArrayList<Feed>();
						i = 1;
						j = 1;
						continue mainLoop;
					} else if (dsp.getLastSavedFeedId().equals(feed.getId())) {
						System.out.println("We came to last crawled feed. Stop");
						setLastCrawledFeed(user.getUserId(), dsp.getPageId(), lastCrawlFeedId);
						i = 1;
						j = 1;
						continue mainLoop;
					} else if (feed.getMessage() != null) {
						feedKeywords = getFeedKeywords(feed.getMessage());
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
								feeds.add(new Feed(new UID().toString(), user.getUserId(), feed.getId(), feed.getMessage(), feedKeywords, feed.getCreatedTime(), "facebook.com/".concat(feed.getId()), dsp.getName(), "post", dsp.getName(), dsp.getName(), categoryId, criteriId));
								criteriId.toString();
								criteriId = new ArrayList<String>();
								categoryId = new ArrayList<String>();
								feedKeywords = new ArrayList<String>();
							} else {
								categoryId.add("uncategorized");
								criteriId.add("uncategorized");
								feeds.add(new Feed(new UID().toString(), user.getUserId(), feed.getId(), feed.getMessage(), feedKeywords, feed.getCreatedTime(), "facebook.com/".concat(feed.getId()), dsp.getName(), "post", dsp.getPageId(), dsp.getName(), categoryId, criteriId));
								criteriId = new ArrayList<String>();
								categoryId = new ArrayList<String>();
								feedKeywords = new ArrayList<String>();
							}
						}
					}

					Connection<Comment> feedComments = fbClient.fetchConnection(feed.getId().concat("/comments"), Comment.class);
					for (List<Comment> comments : feedComments) {
						for (Comment comment : comments) {
							if (comment.getMessage() != null) {
								feedKeywords = getFeedKeywords(comment.getMessage());
								if (!feedKeywords.isEmpty()) {
									for (Entry<String, Map<String, String>> ent : crawlCriteria.entrySet()) {
										for (Entry<String, String> ient : ent.getValue().entrySet()) {
											for (String f : feedKeywords) {
												if (f.equals(ient.getValue().toLowerCase())) {
													if (categoryId.contains(ent.getKey())) {
														criteriId.add(ient.getKey().toString());
														continue;
													} else {
														categoryId.add(ent.getKey().toString());
														criteriId.add(ient.getKey().toString());
														continue;
													}
												}
											}
										}
									}
									if (!criteriId.isEmpty()) {
										feeds.add(new Feed(new UID().toString(), user.getUserId(), comment.getId(), comment.getMessage(), feedKeywords, comment.getCreatedTime(), "facebook.com/".concat(comment.getId()), dsp.getName(), "comment", (comment.getFrom() == null) ? "N/A" : comment.getFrom().getId(), (comment.getFrom() == null) ? "N/A" : comment.getFrom().getName(), categoryId, criteriId));
										criteriId = new ArrayList<String>();
										categoryId = new ArrayList<String>();
										feedKeywords = new ArrayList<String>();
									} else {
										categoryId.add("uncategorized");
										criteriId.add("uncategorized");
										feeds.add(new Feed(new UID().toString(), user.getUserId(), comment.getId(), comment.getMessage(), feedKeywords, comment.getCreatedTime(), "facebook.com/".concat(comment.getId()), dsp.getName(), "comment", (comment.getFrom() == null) ? "N/A" : comment.getFrom().getId(), (comment.getFrom() == null) ? "N/A" : comment.getFrom().getName(), categoryId, criteriId));
										criteriId = new ArrayList<String>();
										categoryId = new ArrayList<String>();
										feedKeywords = new ArrayList<String>();
									}
								}
							}
						}
					}
					i++;
				}
			}
			 setLastCrawledFeed(user.getUserId(), dsp.pageId,
			 lastCrawlFeedId);
		}
	}

	public List<String> getFeedKeywords(String message) {
		String trimedMessage = message.replaceAll("\\p{P}", " ").toLowerCase().trim().replaceAll("(\\s)+", "$1").replaceAll("[\n\r]", "");
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

	public void setLastCrawledFeed(String userId, String pageId, String feedId) {
		Query query = new Query(Criteria.where("_id").is(userId).and("facebookPages").elemMatch(Criteria.where("_id").is(pageId)));
		mongoTemplate.updateFirst(query, new Update().set("facebookPages.$.lastSavedFeedId", feedId), DataSource.class, COLLECTION_NAME);
		System.out.println("OK");
	}

	public DataSource getLastCrawlFeedId(String userId, String pageId) {
		Query query = new Query(Criteria.where("_id").is(userId).and("facebookPages").elemMatch(Criteria.where("lastSavedFeedId").is(pageId)));
		// query.fields().include("facebookPages.facebooklastSavedFeedId");
		DataSource pg = mongoTemplate.findOne(query, DataSource.class, COLLECTION_NAME);
		System.out.println("OK");
		return pg;
	}

}
