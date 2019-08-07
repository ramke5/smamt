package ba.ramke.controller;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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

import ba.ramke.dao.CategoryRepository;
import ba.ramke.model.Category;

@Controller
public class CategoryController {
	
	@Autowired
	private CategoryRepository categoryDao;
	
	@RequestMapping(value = "/smamt/home", method = RequestMethod.GET)
	public ModelAndView home(ModelMap model, HttpServletRequest request) {
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
			ModelAndView modelAndView = new ModelAndView("home");
			modelAndView.addObject("userId", userId);
			modelAndView.addObject("username", username);
			return modelAndView;
		}
	}
	
	@RequestMapping(value = "/smamt/active-categories", method = RequestMethod.GET)
	public ModelAndView getActiveCategoriesView(ModelMap model, HttpServletRequest request) {

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
			ModelAndView modelAndView = new ModelAndView("manage-categories");
			modelAndView.addObject("userId", userId);
			modelAndView.addObject("username", username);
			return modelAndView;
		}
	}
	
	@RequestMapping(value = "/smamt/addcategory", method = RequestMethod.POST)
	@ResponseBody
	public void addCategory(ModelMap modelMap, @ModelAttribute("userId") String userId, @ModelAttribute("categoryName") String categoryName) throws UnknownHostException {

		List<Category> categories = new ArrayList<Category>();

		if (categoryName.length() != 0) {
			List<String> rawCategories = Arrays.asList(categoryName.split(","));
			for (String category : rawCategories) {
				String trimedCategoryName = category.replaceAll("\\s", "");
				categories.add(new Category(UUID.randomUUID().toString(), trimedCategoryName, 1));
			}
			categoryDao.addCategoriesToUser(userId, categories);
		} else {
			System.out.println("Empty");
			modelMap.put("error", "Please fill field");
		}
	}

}
