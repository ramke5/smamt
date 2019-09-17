package ba.ramke.controller;

import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import ba.ramke.dao.DataSourceRepository;
import ba.ramke.model.DataSource;

@Controller
public class DataSourceController {
	
	@Autowired
	private DataSourceRepository dataSourceDao;
	
	@RequestMapping(value = "/smamt/datasources", method = RequestMethod.GET)
	public ModelAndView dataSources(HttpServletRequest request) {
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
			ModelAndView modelAndView = new ModelAndView("datasources");
			modelAndView.addObject("userId", userId);
			modelAndView.addObject("username", username);
			return modelAndView;
		}
	}
	
	@RequestMapping(value = "/smamt/addfacebookpage", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> addFacebookPage(ModelMap modelMap, @ModelAttribute("userId") String userId, @ModelAttribute("pageUrl") String pageUrl) throws Exception {
		if (dataSourceDao.isFacebookPageValid(userId, pageUrl) == false)
			return new ResponseEntity<String>("hamdija", HttpStatus.BAD_REQUEST);
		return new ResponseEntity<String>("beca", HttpStatus.OK);
	}
	
	@RequestMapping(value = "/smamt/deletefbpage", method = RequestMethod.POST)
	@ResponseBody
	public void deleteFacebookPage(ModelMap modelMap, @ModelAttribute("userId") String userId, @ModelAttribute("pageId") String pageId) {
		dataSourceDao.deleteFacebookPageById(userId, pageId);
	}

	@RequestMapping(value = "/smamt/restorefbpage", method = RequestMethod.POST)
	@ResponseBody
	public void restoreFacebookPage(ModelMap modelMap, @ModelAttribute("userId") String userId, @ModelAttribute("pageId") String pageId) {
		dataSourceDao.restoreFacebookPageById(userId, pageId);
	}
	
//	@RequestMapping(value = "/sma/facebookpages", method = RequestMethod.GET)
//	@ResponseBody
//	public List<DataSource> getActiveFacebookPages(ModelMap modelMap, @ModelAttribute("userId") String userId) {
//		return dataSourceDao.getAllFacebookPagesWithValidStatusByUserId(userId);
//	}
//
//	@RequestMapping(value = "/sma/deletedfacebookpages", method = RequestMethod.GET)
//	@ResponseBody
//	public List<DataSource> getDeletedFacebookPages(ModelMap modelMap, @ModelAttribute("userId") String userId) {
//		return dataSourceDao.getAllDeletedFacebookPagesByUserId(userId);
//	}

}
