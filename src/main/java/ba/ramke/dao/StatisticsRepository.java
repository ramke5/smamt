package ba.ramke.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import ba.ramke.helper.HeatMapResponse;
import ba.ramke.helper.StringIntMap;
import ba.ramke.model.Categorized;
import ba.ramke.model.Category;
import ba.ramke.model.DateAggregation;
import ba.ramke.model.DayAggregation;
import ba.ramke.model.User;

@Repository
public class StatisticsRepository {

	@Autowired
	private MongoTemplate mongoTemplate;

	public List<Categorized> statisiticsPerCategoryByUserId(String userId) {
		Aggregation agg = Aggregation.newAggregation(
				Aggregation.match(Criteria.where("user_id").is(userId)), 
				Aggregation.unwind("categoryId"), 
				Aggregation.group("categoryId").count().as("y"), 
				Aggregation.project("y").and("name").previousOperation(),
				Aggregation.sort(Direction.DESC, "y"));

		AggregationResults<Categorized> result = mongoTemplate.aggregate(agg, "categorizedtweets", Categorized.class);
		List<Categorized> toRet = result.getMappedResults();

		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(userId).and("categories.categoryStatus").is(1).and("categories").elemMatch(Criteria.where("categoryStatus").is(1)));
		User user = mongoTemplate.findOne(query, User.class);
		Category category;
		Iterator<Category> categoryIterator;
		List<Categorized> categorized = new ArrayList<Categorized>();
		for (Categorized c : toRet) {
			categoryIterator = user.getCategories().iterator();
			while (categoryIterator.hasNext()) {
				category = categoryIterator.next();
				if (category.getCategoryId().equals(c.name) && category.getCategoryStatus() == 1) {
					categorized.add(new Categorized(category.getCategoryName(), c.y));
				}
			}

		}
		return categorized;
	}
	
//	public List<Categorized> statisiticsPerCategoryByDate(String userId, String categoryId){
	public List<Categorized> statisiticsPerCategoryByDate(String userId, String categoryId, Date dateFrom, Date dateUntil){
		
	        // add one day to dateUntil
	        Calendar c = Calendar.getInstance();
	        c.setTime(dateUntil);
	        c.add(Calendar.DATE, 1); //same with c.add(Calendar.DAY_OF_MONTH, 1);
	        dateUntil = c.getTime();
	        
	        // add two hours to dateFrom
	        Calendar c2 = Calendar.getInstance();
	        c2.setTime(dateFrom);
	        c2.add(Calendar.HOUR, 2); //same with c.add(Calendar.DAY_OF_MONTH, 1);
	        dateFrom = c2.getTime();
		
		Aggregation agg = Aggregation.newAggregation
						(Aggregation.match(Criteria.where("user_id").is(userId)),
						Aggregation.unwind("categoryId"),
						Aggregation.match(Criteria.where("categoryId").is(categoryId)),
						Aggregation.match(Criteria.where("dateOfCreation").gt(dateFrom).lt(dateUntil)),
						//Aggregation.limit(50000),
						Aggregation.project("dateOfCreation").andExpression("year(dateOfCreation)").as("year")
															.andExpression("month(dateOfCreation)").as("month")
															.andExpression("dayOfMonth(dateOfCreation)").as("day"),
						Aggregation.group(Fields.fields().and("day").and("month").and("year")).count().as("count"),
						Aggregation.sort(Direction.DESC,"year").and(Direction.DESC,"month").and(Direction.DESC,"day")
						);
		AggregationResults<DateAggregation> result = mongoTemplate.aggregate(agg, "categorizedtweets", DateAggregation.class);
		List<DateAggregation> aggregationResponse = result.getMappedResults();
		List<Categorized> dateAggregation = new ArrayList<Categorized>();
		for(DateAggregation date : aggregationResponse){
			dateAggregation.add(new Categorized(date.day + "-" + date.month + "-" + date.year, date.count));
		}
		return dateAggregation;
	}
	
	public List<DayAggregation> statisticsPerWeekDay(){
		Aggregation aggregate = Aggregation.newAggregation(
				Aggregation.project("dateOfCreation").and("dateOfCreation").extractDayOfWeek().as("day"),
				//Aggregation.limit(50000),
				Aggregation.group("day").count().as("occurences"),
				Aggregation.project("occurences").and("day").previousOperation(),
				Aggregation.sort(Direction.ASC, "day")
				);
		System.out.println(aggregate.toString());
		AggregationResults<DayAggregation> result = mongoTemplate.aggregate(aggregate, "categorizedtweets", DayAggregation.class);			
		List<DayAggregation> aggregationResponse = result.getMappedResults();
		return aggregationResponse;
	}
	
	public List<DayAggregation> statisticsPerDayHour(){
		Aggregation aggregate = Aggregation.newAggregation(
				Aggregation.project("dateOfCreation").and("dateOfCreation").extractHour().as("hour"),
				//Aggregation.limit(10000),
				Aggregation.group("hour").count().as("occurences"),
				Aggregation.project("occurences").and("hour").previousOperation(),
				Aggregation.sort(Direction.ASC, "hour")
				//,Aggregation.limit(50000)
				);
		AggregationResults<DayAggregation> result = mongoTemplate.aggregate(aggregate, "categorizedtweets", DayAggregation.class);
		List<DayAggregation> aggregationResponse = result.getMappedResults();
		return aggregationResponse;
	}
	
	public List<StringIntMap> statisticsPerCriteria(){
		Aggregation aggregate = Aggregation.newAggregation(
				Aggregation.unwind("criteriaId"),
				Aggregation.group("criteriId").count().as("occurences"),
				Aggregation.project("occurences").and("criteriId").previousOperation(),
				Aggregation.sort(Direction.ASC, "occurences")
				//,Aggregation.limit(10)
				);
		
		AggregationResults<StringIntMap> result = mongoTemplate.aggregate(aggregate, "categorizedtweets", StringIntMap.class);
		List<StringIntMap> aggregationResponse = result.getMappedResults();
		return aggregationResponse;
	}
	
	public List<HeatMapResponse> statisticsForHeatMap(){
		Aggregation aggregate = Aggregation.newAggregation(
					//Aggregation.match(Criteria.where("dateOfCreation").gt(sdate).lt(edate)),
					Aggregation.project("dateOfCreation").and("dateOfCreation").extractDayOfWeek().as("day").and("dateOfCreation").extractHour().as("hour"),
					Aggregation.group(Fields.fields("day","hour")).count().as("occurences"),
					Aggregation.sort(Direction.ASC, "_id.hour").and(Direction.ASC, "_id.day")
				);
						
		System.out.println("QUERY ## " + aggregate.toString());
		AggregationResults<HeatMapResponse> result = mongoTemplate.aggregate(aggregate, "categorizedtweets", HeatMapResponse.class);
		List<HeatMapResponse> aggregationResponse = result.getMappedResults();
		return aggregationResponse;
		
	}
	
	public List<HeatMapResponse> statisticsForEachCategoryByDate(String userId){
		Aggregation aggregate = Aggregation.newAggregation(
				Aggregation.match(Criteria.where("user_id").is(userId)),
				Aggregation.unwind("categoryId"),
				Aggregation.project("categoryId").and("dateOfCreation").extractYear().as("year")
												 .and("dateOfCreation").extractMonth().as("month")
												 .and("dateOfCreation").extractDayOfMonth().as("day"),
				Aggregation.group(Fields.fields("year","month","day","categoryId")).count().as("occurences"),
				Aggregation.group(Fields.fields("_id.year","_id.month","_id.day")).push("_id.categoryId").as("categories").push("occurences").as("categoryOccurences"),
				Aggregation.sort(Direction.DESC, "_id.year", "_id.month", "_id.day")
				//,Aggregation.limit(20)
				);
		System.out.println("QUERY ## " + aggregate.toString());
		AggregationResults<HeatMapResponse> result = mongoTemplate.aggregate(aggregate, "categorizedtweets", HeatMapResponse.class);
		List<HeatMapResponse> aggregationResponse = result.getMappedResults();
		return aggregationResponse;		
	}
	
	//All categories impl. goes here
	public List<StringIntMap> statisticsForAllCategories(){
		Aggregation aggregate = Aggregation.newAggregation(
				Aggregation.unwind("criteriaId"),
				Aggregation.group("criteriId").count().as("occurences"),
				Aggregation.project("occurences").and("criteriId").previousOperation(),
				Aggregation.sort(Direction.ASC, "occurences")
				//,Aggregation.limit(10)
				);
		
		AggregationResults<StringIntMap> result = mongoTemplate.aggregate(aggregate, "categorizedtweets", StringIntMap.class);
		List<StringIntMap> aggregationResponse = result.getMappedResults();
		return aggregationResponse;
	}
	
	public List<Categorized> statisticsGenderByUserId(String userId) {
		Aggregation agg = Aggregation.newAggregation(
				Aggregation.match(new Criteria().orOperator(Criteria.where("userGender").is("male"), Criteria.where("userGender").is("female"))),
				Aggregation.group("userGender").count().as("y"),
				Aggregation.project("y").and("name").previousOperation(),
				Aggregation.sort(Direction.DESC, "y"));

		AggregationResults<Categorized> result = mongoTemplate.aggregate(agg, "categorizedtweets", Categorized.class);
		List<Categorized> toRet = result.getMappedResults();

		return toRet;
	}
	

	public List<Categorized> statisticsAccountByUserId(String userId) {
		Aggregation agg = Aggregation.newAggregation(
				Aggregation.group("name").count().as("y"),
				Aggregation.project("y").and("name").previousOperation(),
				Aggregation.sort(Direction.DESC, "y"),
				Aggregation.limit(20));

		AggregationResults<Categorized> result = mongoTemplate.aggregate(agg, "categorizedtweets", Categorized.class);
		List<Categorized> toRet = result.getMappedResults();
		for(int i=0; i<toRet.size(); i++) {
			System.out.println(toRet.get(i).getName());
		}

		return toRet;
	}
	
	public List<Categorized> statisticsLocationByUserId(String userId) {
		Aggregation agg = Aggregation.newAggregation(
//				Aggregation.match(Criteria.where("userLocation").ne("x")),	
				Aggregation.match(new Criteria().andOperator(Criteria.where("userLocation").ne("x"), Criteria.where("userLocation").ne("Uhljebistan"), Criteria.where("userLocation").ne("Folsom Prison"), Criteria.where("userLocation").ne(""))),				
				Aggregation.group("userLocation").count().as("y"),
				Aggregation.project("y").and("name").previousOperation(),
				Aggregation.sort(Direction.DESC, "y"),
				Aggregation.limit(20));

		AggregationResults<Categorized> result = mongoTemplate.aggregate(agg, "categorizedtweets", Categorized.class);
		List<Categorized> toRet = result.getMappedResults();
//		for(int i=0; i<toRet.size(); i++) {
//			System.out.println(toRet.get(i).getName());
//		}

		return toRet;
	}	
}

