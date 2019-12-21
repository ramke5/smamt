package ba.ramke.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import ba.ramke.helper.StringMap;
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
	
	public void changeCategoryName(String categoryId, String categoryName) {
		Query query = new Query(Criteria.where("categories").elemMatch(Criteria.where("categoryId").is(categoryId)));
		mongoTemplate.updateFirst(query, new Update().set("categories.$.categoryName", categoryName), User.class);
	}
	
	public void deleteCategoryById(String categoryId) {
		Query query = new Query(Criteria.where("categories").elemMatch(Criteria.where("categoryId").is(categoryId)));
		mongoTemplate.updateFirst(query, new Update().set("categories.$.categoryStatus", 0), User.class);
	}
	
	public List<User> getAllDeletedCategoriesByUserId(String userId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(userId).and("categories.categoryStatus").is(0).and("categories").elemMatch(Criteria.where("categoryStatus").is(0)));
		List<User> user = mongoTemplate.find(query, User.class);
		Iterator<User> userIterator = user.iterator();
		while (userIterator.hasNext()) {
			User p = userIterator.next();
			Iterator<Category> categoryIterator = p.getCategories().iterator();
			while (categoryIterator.hasNext()) {
				Category category = categoryIterator.next();
				if (category.categoryStatus == 1 || category.getCategoryName() == "") {
					categoryIterator.remove();
				}
			}
		}
		return user;
	}
	
	public void restoreCategory(String categoryId) {
		Query query = new Query(Criteria.where("categories").elemMatch(Criteria.where("categoryId").is(categoryId)));
		mongoTemplate.updateFirst(query, new Update().set("categories.$.categoryStatus", 1), User.class);
	}
	
	public List<StringMap> getCategoriesWithValidStatus(String userId) {
		List<StringMap> smap = new ArrayList<StringMap>();
		Category category = new Category();
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(userId).and("categories.categoryStatus").is(1).and("categories").elemMatch(Criteria.where("categoryStatus").is(1)));
		User user = mongoTemplate.findOne(query, User.class, "user");
		Iterator<Category> categoryIterator = user.getCategories().iterator();
		while (categoryIterator.hasNext()) {
			category = categoryIterator.next();
			if (category.categoryStatus == 1) {
				smap.add(new StringMap(category.categoryId, category.categoryName));
			}
		}
		return smap;
	}
	
	public List<User> allCategories(String id) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(id));
		List<User> user = mongoTemplate.find(query, User.class);

		return user;
	}
	
}
