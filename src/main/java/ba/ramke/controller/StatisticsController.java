package ba.ramke.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import ba.ramke.dao.StatisticsRepository;
import ba.ramke.helper.HeatMapResponse;
import ba.ramke.model.Categorized;
import ba.ramke.model.CategorizedWords;
import ba.ramke.model.DayAggregation;

@Controller
public class StatisticsController {

	@Autowired
	private StatisticsRepository statisticsDao;

	@RequestMapping(value = "/smamt/my-categories", method = RequestMethod.GET)
	public ModelAndView getMyCategories(ModelMap model, HttpServletRequest request) {
		Cookie[] cookie = request.getCookies();
		boolean exists = false;
		String userId = "";
		String username = "";
		for (Cookie c : cookie) {
			if (c.getName().equals("session")) {
				exists = true;
				userId = c.getValue();
			} else if (c.getName().equals("uname")) {
				exists = true;
				username = c.getValue();
			}
		}
		if (exists == false) {
			return new ModelAndView("redirect:/");
		} else {
			ModelAndView modelAndView = new ModelAndView("my-categories");
			modelAndView.addObject("userId", userId);
			modelAndView.addObject("username", username);
			return modelAndView;
		}
	}

	@RequestMapping(value = "/smamt/category", method = RequestMethod.GET)
	public ModelAndView dateCategoryCategorization(ModelMap model, @ModelAttribute("userId") String userId) {

		ModelAndView modelAndView = new ModelAndView("date-category-statistics");
		modelAndView.addObject("userId", userId);
		return modelAndView;
	}

	@RequestMapping(value = "/smamt/my-categories-stats", method = RequestMethod.GET)
	public @ResponseBody List<Categorized> statisiticsPerCategoryByUserId(ModelMap model, @ModelAttribute("userId") String userId) {
		return statisticsDao.statisiticsPerCategoryByUserId(userId);
	}

	@RequestMapping(value = "/smamt/date-stats", method = RequestMethod.GET)
	public @ResponseBody List<Categorized> statisiticsPerCategoryByDate(ModelMap model, @ModelAttribute("userId") String userId, @ModelAttribute("categoryId") String categoryId, @ModelAttribute("startDate") Date startDate, @ModelAttribute("endDate") Date endDate) {
		return statisticsDao.statisiticsPerCategoryByDate(userId, categoryId, startDate, endDate);
	}
	
	@RequestMapping(value = "/smamt/stats-keywords-per-date", method = RequestMethod.GET)
	public @ResponseBody List<CategorizedWords> statisticsPerDateByUserId(ModelMap model, @ModelAttribute("userId") String userId,  @ModelAttribute("accountId") String accountId, @ModelAttribute("startDate") Date startDate, @ModelAttribute("endDate") Date endDate) {
		List<CategorizedWords> aaaa = statisticsDao.statisticsKeywordsPerDateByUserId(userId, accountId, startDate, endDate);
		
		List<CategorizedWords> bbbb = new ArrayList<CategorizedWords>();
		for (CategorizedWords cw : aaaa) {
		    if(!cw.get_Id().equals("https") && !cw.get_Id().equals("twitter") && !cw.get_Id().equals("pic")
		    		&& !cw.get_Id().equals("com") && !cw.get_Id().equals("and") && !cw.get_Id().equals("video")
		    		&& !cw.get_Id().equals("BAEU") && !cw.get_Id().equals("with") && !cw.get_Id().equals("for")
		    		&& !cw.get_Id().equals("kako") && !cw.get_Id().equals("zbog") && !cw.get_Id().equals("biti")
		    		&& !cw.get_Id().equals("nije") && !cw.get_Id().equals("što") && !cw.get_Id().equals("nakon")
		    		&& !cw.get_Id().equals("sve") && !cw.get_Id().equals("koje") && !cw.get_Id().equals("sad")
		    		&& !cw.get_Id().equals("") && !cw.get_Id().equals("the") && !cw.get_Id().equals("smo")
		    		&& !cw.get_Id().equals("www") && !cw.get_Id().equals("sada") && !cw.get_Id().equals("više")
		    		&& !cw.get_Id().equals("koja") && !cw.get_Id().equals("kao") && !cw.get_Id().equals("promo")
		    		&& !cw.get_Id().equals("protiv") && !cw.get_Id().equals("godina") && !cw.get_Id().equals("novi")
		    		&& !cw.get_Id().equals("samo") && !cw.get_Id().equals("dana") && !cw.get_Id().equals("http")
		    		&& !cw.get_Id().equals("ba") && !cw.get_Id().equals("šta") && !cw.get_Id().equals("foto")
		    		&& !cw.get_Id().equals("ovo") && !cw.get_Id().equals("evo") && !cw.get_Id().equals("multimedia")
		    		&& !cw.get_Id().equals("avaz") && !cw.get_Id().equals("sam") && !cw.get_Id().equals("vise")
		    		&& !cw.get_Id().equals("n1info") && !cw.get_Id().equals("html") && !cw.get_Id().equals("vijesti")
		    		&& !cw.get_Id().equals("danu") && !cw.get_Id().equals("ajb") && !cw.get_Id().equals("ajbvideo")
		    		&& !cw.get_Id().equals("bez") && !cw.get_Id().equals("000") && !cw.get_Id().equals("traži")
		    		&& !cw.get_Id().equals("neće") && !cw.get_Id().equals("gdje") && !cw.get_Id().equals("već")
		    		&& !cw.get_Id().equals("sata") && !cw.get_Id().equals("sati") && !cw.get_Id().equals("danas")
		    		&& !cw.get_Id().equals("ukupno") && !cw.get_Id().equals("broj") && !cw.get_Id().equals("kada")
		    		&& !cw.get_Id().equals("piše") && !cw.get_Id().equals("može") && !cw.get_Id().equals("ajbmišljenje")
		    		&& !cw.get_Id().equals("net") && !cw.get_Id().equals("uživo") && !cw.get_Id().equals("utm")
		    		&& !cw.get_Id().equals("medium=twitter") && !cw.get_Id().equals("source=socialnetwork") && !cw.get_Id().equals("glassrpske")
		    		&& !cw.get_Id().equals("campaign=share") && !cw.get_Id().equals("button") && !cw.get_Id().equals("lat")
		    		&& !cw.get_Id().equals("još") && !cw.get_Id().equals("jos") && !cw.get_Id().equals("lica")
		    		&& !cw.get_Id().equals("ćemo") && !cw.get_Id().equals("atv") && !cw.get_Id().equals("atvbl")
		    		&& !cw.get_Id().equals("faktor") && !cw.get_Id().equals("radiosarajevo") && !cw.get_Id().equals("ima")
		    		&& !cw.get_Id().equals("kaže") && !cw.get_Id().equals("bilo") && !cw.get_Id().equals("rtvbn")
		    		&& !cw.get_Id().equals("klix") && !cw.get_Id().equals("clanak") && !cw.get_Id().equals("koji")){
		    			bbbb.add(cw);
		    }
		}
		
		return bbbb;
	}
	
	@RequestMapping(value = "/smamt/week-day", method = RequestMethod.GET)
	public @ResponseBody List<Long> statisiticsPerWeekDay() {
		List<DayAggregation> aggregated = statisticsDao.statisticsPerWeekDay();
		List<Long> dailyOccurences = new ArrayList<Long>();
		for (DayAggregation day : aggregated)
			dailyOccurences.add(day.occurences);
		return dailyOccurences;
	}

	@RequestMapping(value = "/smamt/day-hour", method = RequestMethod.GET)
	public @ResponseBody List<Long> statisiticsPerDayHour() {
		List<DayAggregation> aggregated = statisticsDao.statisticsPerDayHour();
		List<Long> hourlyOccurences = new ArrayList<Long>();
		for (DayAggregation hour : aggregated)
			hourlyOccurences.add(hour.occurences);
		return hourlyOccurences;
	}

	@RequestMapping(value = "/smamt/heat-map", method = RequestMethod.GET)
	public @ResponseBody List<ArrayList<Long>> statisiticsForHeatMap() {
		List<HeatMapResponse> response = statisticsDao.statisticsForHeatMap();
		ArrayList<Long> innerHeatMap = new ArrayList<Long>();
		List<ArrayList<Long>> heatMap = new ArrayList<ArrayList<Long>>();
		long i = 0;
		long j = 0;

		for (HeatMapResponse hm : response) {
			if (j == 7) {
				i++;
				j = 0;
				innerHeatMap = new ArrayList<Long>();
				innerHeatMap.add(i);
				innerHeatMap.add(j);
				innerHeatMap.add((long) hm.occurences);
				heatMap.add(innerHeatMap);
				j++;
			} else if (i == 24) {
				break;
			} else {
				innerHeatMap = new ArrayList<Long>();
				innerHeatMap.add(i);
				innerHeatMap.add(j);
				innerHeatMap.add((long) hm.occurences);
				heatMap.add(innerHeatMap);
				j++;
			}
		}
		return heatMap;
	}
	
	@RequestMapping(value = "/smamt/gender-stats", method = RequestMethod.GET)
	public @ResponseBody List<Categorized> statisticsGenderByUserId(ModelMap model, @ModelAttribute("userId") String userId) {
		return statisticsDao.statisticsGenderByUserId(userId);
	}
	
	@RequestMapping(value = "/smamt/account-stats", method = RequestMethod.GET)
	public @ResponseBody List<Categorized> statisticsAccountByUserId(ModelMap model, @ModelAttribute("userId") String userId) {
		return statisticsDao.statisticsAccountByUserId(userId);
	}
	
	@RequestMapping(value = "/smamt/location-stats", method = RequestMethod.GET)
	public @ResponseBody List<Categorized> statisticsLocationByUserId(ModelMap model, @ModelAttribute("userId") String userId) {
		return statisticsDao.statisticsLocationByUserId(userId);
	}
}
