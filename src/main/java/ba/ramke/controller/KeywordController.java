package ba.ramke.controller;

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

import ba.ramke.dao.KeywordRepository;
import ba.ramke.helper.Serialize;
import ba.ramke.model.Keyword;

@Controller
public class KeywordController {
	
	@Autowired
	private KeywordRepository keywordDao;

	@RequestMapping(value = "/smamt/category-keywords", method = RequestMethod.GET)
	public ModelAndView getKeywordsAndSynonmsOfCategory(@ModelAttribute("categoryId") String categoryId, @ModelAttribute("categoryName") String categoryName, HttpServletRequest request) {
		Cookie[] cookie = request.getCookies();
		boolean exists = false;
		String userId = "";
		String username = "";
		for (Cookie c : cookie) {
			if (c.getName().equals("session") && !c.getValue().equals("")) {
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
			ModelAndView modelAndView = new ModelAndView("category-keywords");
			modelAndView.addObject("userId", userId);
			modelAndView.addObject("categoryId", categoryId);
			modelAndView.addObject("categoryName", categoryName);
			return modelAndView;
		}
	}
	
	@RequestMapping(value = "/smamt/addkeywords", method = RequestMethod.POST)
	@ResponseBody
	public void addKeywordsToCategory(ModelMap modelMap, @ModelAttribute("userId") String userId, @ModelAttribute("categoryId") String categoryId, @ModelAttribute("keywords") String nameOfKeywords) {
		if(nameOfKeywords.length() != 0) {
			Keyword keyword = new Keyword(UUID.randomUUID().toString(), nameOfKeywords, 1);
			keywordDao.addKeywordToCategory(userId, categoryId, keyword);
		}
		else {
			System.out.println("Empty");
			modelMap.put("error", "Please fill field");
		}
	}
	
	@RequestMapping(value = "/smamt/deletekeyword", method = RequestMethod.POST)
	@ResponseBody
	public void deleteKeyword(ModelMap modelMap, @ModelAttribute("userId") String userId, @ModelAttribute("categoryId") String categoryId, @ModelAttribute("keywordId") String keywordId) {
		keywordDao.deleteKeyword(userId, categoryId, keywordId);
	}
	
	@RequestMapping(value = "/smamt/keywords", method = RequestMethod.GET)
	public @ResponseBody Serialize getAllActiveKeywords(ModelMap model, @ModelAttribute("userId") String userId, @ModelAttribute("categoryId") String categoryId) {
		Serialize serializedObj = new Serialize();
		serializedObj.setElements(keywordDao.getAllActiveKeywordsByCategoryId(categoryId));
		System.out.println("Obj is " + serializedObj.getElements());
		return serializedObj;
	}
	
	@RequestMapping(value = "/smamt/deletedkeywords", method = RequestMethod.GET)
	public @ResponseBody Serialize getAllDeletedKeywords(ModelMap model, @ModelAttribute("userId") String userId, @ModelAttribute("categoryId") String categoryId) {
		Serialize serializedObj = new Serialize();
		serializedObj.setElements(keywordDao.getAllDeletedKeywordsByCategoryId(categoryId));
		System.out.println("Obj is " + serializedObj.getElements());
		return serializedObj;
	}
	
	@RequestMapping(value = "/smamt/restoreKeyword", method = RequestMethod.POST)
	@ResponseBody
	public void restoreKeyword(ModelMap modelMap, @ModelAttribute("userId") String userId, @ModelAttribute("categoryId") String categoryId, @ModelAttribute("keywordId") String keywordId) {
		keywordDao.restoreKeyword(userId, categoryId, keywordId);
	}
	
	@RequestMapping(value = "/smamt/keywordName", method = RequestMethod.POST)
	@ResponseBody
	public void changeKeywordName(ModelMap modelMap, @ModelAttribute("categoryId") String categoryId, @ModelAttribute("keywordId") String keywordId, @ModelAttribute("keywordName") String keywordName) {
		keywordDao.changeKeywordName(categoryId, keywordId, keywordName);
	}
}
