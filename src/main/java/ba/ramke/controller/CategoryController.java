package ba.ramke.controller;

import java.net.UnknownHostException;
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
import ba.ramke.helper.Serialize;
import ba.ramke.helper.StringMap;
import ba.ramke.model.Category;
import ba.ramke.model.User;

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
	
	@RequestMapping(value = "/smamt/activecategories", method = RequestMethod.GET)
	public @ResponseBody Serialize getAllActiveCategories(ModelMap model, @ModelAttribute("userId") String userId) {
		Serialize serializedObj = new Serialize();
		serializedObj.setElements(categoryDao.getAllCategoriesWithValidStatusByUserId(userId));
		System.out.println("Obj is " + serializedObj.getElements());
		return serializedObj;
	}
	
	@RequestMapping(value = "/smamt/addcategory", method = RequestMethod.POST)
	@ResponseBody
	public void addCategory(ModelMap modelMap, @ModelAttribute("userId") String userId, @ModelAttribute("categoryName") String categoryName) throws UnknownHostException {
		if (categoryName.length() != 0) {
			Category cat = new Category(UUID.randomUUID().toString(), categoryName, 1);
			categoryDao.addCategoryToUser(userId, cat);
		}
		else {
			System.out.println("Empty");
			modelMap.put("error", "Please fill field");
		}
	}
	
	@RequestMapping(value = "/smamt/categoryName", method = RequestMethod.POST)
	@ResponseBody
	public void changeCategoryName(ModelMap modelMap, @ModelAttribute("categoryId") String categoryId, @ModelAttribute("categoryName") String categoryName) {
		categoryDao.changeCategoryName(categoryId, categoryName);
	}
	
	@RequestMapping(value = "/smamt/deleteCategory", method = RequestMethod.POST)
	@ResponseBody
	public void deleteCategory(ModelMap modelMap, @ModelAttribute("categoryId") String categoryId) {
		categoryDao.deleteCategoryById(categoryId);
	}
	
	@RequestMapping(value = "/smamt/deleted-categories", method = RequestMethod.GET)
	public ModelAndView getDeletedCategoriesView(ModelMap model, HttpServletRequest request) {

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
			ModelAndView modelAndView = new ModelAndView("deleted-categories");
			modelAndView.addObject("userId", userId);
			modelAndView.addObject("username", username);
			return modelAndView;
		}
	}
	
	@RequestMapping(value = "/smamt/deletedcategories", method = RequestMethod.GET)
	public @ResponseBody Serialize getAllCategories(ModelMap model, @ModelAttribute("userId") String userId) {
		Serialize serializedObj = new Serialize();
		serializedObj.setElements(categoryDao.getAllDeletedCategoriesByUserId(userId));
		System.out.println("Obj is DELETED CATEGORIES " + serializedObj.getElements());
		return serializedObj;
	}
	
	@RequestMapping(value = "/smamt/restoreCategory", method = RequestMethod.POST)
	@ResponseBody
	public void restoreCategory(ModelMap modelMap, @ModelAttribute("categoryId") String categoryId) {
		categoryDao.restoreCategory(categoryId);
	}
	
	//currently only for user
	@RequestMapping(value = "/smamt/categories", method = RequestMethod.GET)
	public @ResponseBody List<User> categories(ModelMap model) {
		return categoryDao.allCategories("92e145d7-e33e-4ed3-b005-d4d7c4553de5");
	}

	//for statistics and home page + search
	@RequestMapping(value = "/smamt/get-categories", method = RequestMethod.GET)
	public @ResponseBody List<StringMap> categories(ModelMap model, HttpServletRequest request) {
		String userId = "";
		for (Cookie c : request.getCookies()) {
			if (c.getName().equals("session"))
				userId = c.getValue();
		}
		return categoryDao.getCategoriesWithValidStatus(userId);
	}

}
