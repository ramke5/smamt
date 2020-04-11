package ba.ramke.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;;

@Configuration
public class Config {
	
//	MongoClientURI uri = new MongoClientURI("mongodb://ramkeAdmin:Supermen.888@cluster0-shard-00-00-2hrz3.mongodb.net:27017,cluster0-shard-00-01-2hrz3.mongodb.net:27017,cluster0-shard-00-02-2hrz3.mongodb.net:27017/smamt?ssl=true&replicaSet=Cluster0-shard-0&authSource=admin&retryWrites=true&w=majority");
	MongoClientURI uri = new MongoClientURI("mongodb://ramkeAdmin:Supermen.888@clusterramke-shard-00-00-2hrz3.mongodb.net:27017,clusterramke-shard-00-01-2hrz3.mongodb.net:27017,clusterramke-shard-00-02-2hrz3.mongodb.net:27017/smamt?replicaSet=ClusterRamke-shard-0&ssl=true&authSource=admin&retryWrites=true&w=majority");

	
	public @Bean MongoClient mongo() throws Exception {
        return new MongoClient(uri);
    }

    public @Bean MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongo(), "smamt");
    }
}

//@Configuration
//public class Config {
//	
//	public @Bean MongoClient mongo() throws Exception {
//        return new MongoClient("localhost");
//    }
//
//    public @Bean MongoTemplate mongoTemplate() throws Exception {
//        return new MongoTemplate(mongo(), "smamt");
//    }
//}

//mongodb://<username>:<password>@clusterramke-shard-00-00-2hrz3.mongodb.net:27017,clusterramke-shard-00-01-2hrz3.mongodb.net:27017,clusterramke-shard-00-02-2hrz3.mongodb.net:27017/test?replicaSet=ClusterRamke-shard-0&ssl=true&authSource=admin
