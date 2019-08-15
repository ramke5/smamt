package ba.ramke.dao;

import java.util.Iterator;
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
	
	public void addCategoryToUser(String id, Category category) {
		
		mongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(id)), new Update().push("categories", category),  User.class);
		System.out.println("Everything is ok. Collection is updated");
	}	
	
	
	
	public List<User> getAllCategoriesWithValidStatusByUserId(String id) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(id).and("categories.categoryStatus").is(1).and("categories").elemMatch(Criteria.where("categoryStatus").is(1)));
		List<User> user = mongoTemplate.find(query, User.class);
		Iterator<User> personIterator = user.iterator();
		while (personIterator.hasNext()) {
			User u = personIterator.next();
			Iterator<Category> categoryIterator = u.getCategories().iterator();
			while (categoryIterator.hasNext()) {
				Category category = categoryIterator.next();
				if (category.categoryStatus == 0 || category.getCategoryName() == "") {
					categoryIterator.remove();
				}
			}
		}
		return user;
	}
	
}
