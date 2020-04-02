package ba.ramke.controller;

import java.util.ArrayList;
import java.util.List;

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
	public @ResponseBody List<Categorized> statisiticsPerCategoryByDate(ModelMap model, @ModelAttribute("userId") String userId, @ModelAttribute("categoryId") String categoryId) {
		return statisticsDao.statisiticsPerCategoryByDate(userId, categoryId);
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
