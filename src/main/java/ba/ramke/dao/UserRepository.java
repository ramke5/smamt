package ba.ramke.dao;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import ba.ramke.helper.PasswordConversion;
import ba.ramke.model.Category;
import ba.ramke.model.User;

@Repository
public class UserRepository {
	
	@Autowired
	public MongoTemplate mongoTemplate;
	public static final String COLLECTION_NAME = "user";

	public void addUser(User user) throws NoSuchAlgorithmException {
		System.out.println("Adding user - start");
		if (!mongoTemplate.collectionExists(User.class)) {
			mongoTemplate.createCollection(User.class);
			System.out.println("COLLECTION DOES NOT EXISTS");
		}

		String password = PasswordConversion.hashPassword(user.getPassword());
		user.setPassword(password);
		
		//Setting initial category
		user = setInitialCategory(user);

		mongoTemplate.insert(user, COLLECTION_NAME);
	}
	
	public User login(String username, String password) {
		Query query = new Query();
		query.addCriteria(Criteria.where("name").is(username).and("password").is(password)).fields()
				.exclude("categories");

		User u = mongoTemplate.findOne(query, User.class);

		if (u != null) {
			System.out.println("Found and obj is " + u.toString());
			return u;
		} else {
			System.out.println("No requested object.");
			return null;
		}
	}
	
	public List<User> getPerson(String string) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(string));
		return mongoTemplate.find(query, User.class);
	}
	
	private User setInitialCategory(User user) {
		String categoryId = UUID.randomUUID().toString();
		String categoryName = "initialCategory";
		int categoryStatus = 1;
		
		Category initCategory = new Category(categoryId, categoryName, categoryStatus);
		List<Category> categories = new ArrayList<Category>();
		categories.add(initCategory);
		user.setCategories(categories);
		
		return user;
	}
	
}
