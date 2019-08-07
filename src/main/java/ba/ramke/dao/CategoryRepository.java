package ba.ramke.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import ba.ramke.model.Category;
import ba.ramke.model.User;

@Repository
public class CategoryRepository {

	@Autowired
	public MongoTemplate mongoTemplate;
	
	public void addCategoriesToUser(String id, List<Category> category) {
		
		Object[] categories = category.toArray();
		
		Query select = Query.query(Criteria.where("firstName").is("Ramiz"));
		Update update = new Update();
		update.set("lastName", "novooo");
		mongoTemplate.findAndModify(select, update, User.class);
		
//		mongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(id)), new Update().pushAll("categories", categories), User.class);
		System.out.println("Everything is ok. Collection is updated");
		
		
		
		
		
	}
	
}



//public void addCategoriesToUser(String id, List<Category> category) throws UnknownHostException {
//	Object[] categories = category.toArray();
//	BasicDBObject setNewFieldQuery = new BasicDBObject().append("$set", new BasicDBObject().append("categories", categories));
//	MongoClient mc = new MongoClient();
//	mc.getDB("smamt").getCollection("user").update(new BasicDBObject().append("_id", id), setNewFieldQuery);
//	DB db = mc.getDB("smamt");
//	DBCollection collection = db.getCollection("user");
//	String test = collection.findOne().get("categories").toString();
//	System.out.println("Test: " + test);
////	mongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(id)), new Update().pushAll("categories", categories), User.class);
//	System.out.println("Everything is ok. Collection is updated");
//}
