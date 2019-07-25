package ba.ramke.dao;

import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import ba.ramke.helper.PasswordConversion;
import ba.ramke.model.User;

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

		mongoTemplate.insert(user, COLLECTION_NAME);
	}
	

}
