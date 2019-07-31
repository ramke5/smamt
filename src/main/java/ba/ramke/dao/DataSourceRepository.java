package ba.ramke.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import ba.ramke.model.DataSource;

@Repository
public class DataSourceRepository {
	@Autowired
	private MongoTemplate mongoTemplate;
	public static final String COLLECTION_NAME = "datasource";
	
	public void addPersonToCollection(DataSource ds) {
		mongoTemplate.insert(ds, COLLECTION_NAME);
	}
}
