package ba.ramke.controller;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import ba.ramke.helper.PasswordConversion;
import ba.ramke.model.User;

import ba.ramke.dao.UserRepository;
@Controller
public class LoginController {

	@Autowired
	private UserRepository userDao;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView getLogin(ModelMap model, HttpServletRequest request) {

		request.getSession();

		Cookie[] cookies = request.getCookies();
		boolean ctr = false;
		if (cookies != null) {
			for (Cookie c : cookies) {
				if (c.getName().equals("session")) {
					ctr = true;
				}
			}
			if (ctr == true) {
				return new ModelAndView("redirect:/smamt/home");
			} else
				return new ModelAndView("login");
		} else {
			return new ModelAndView("login");
		}
	}
	
	@RequestMapping(value = "registration", method = RequestMethod.GET)
	public String registration(ModelMap model) {
		return "registration";
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ModelAndView login(@ModelAttribute User user, ModelMap model, HttpServletResponse response) throws NoSuchAlgorithmException, IOException {

		User u = userDao.login(user.getName(), PasswordConversion.hashPassword(user.getPassword()));

		if (u != null) {
			ModelAndView modelAndView = new ModelAndView("manage-categories");
			modelAndView.addObject("username", u.getName());
			modelAndView.addObject("userId", u.getId());
			Cookie cookieUid = new Cookie("session", u.getId());
			Cookie cookieSession = new Cookie("uname", u.getName());
			cookieUid.setMaxAge(20000);
			cookieSession.setMaxAge(20000);
			response.addCookie(cookieUid);
			response.addCookie(cookieSession);
			return new ModelAndView("redirect:/smamt/home");
		} else {
			return new ModelAndView("login", "error", "Wrong username or password.");
		}
	}
	
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public void logout(ModelMap model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		Cookie[] cookies = request.getCookies();
		for (Cookie c : cookies) {
			c.setMaxAge(0);
			c.setValue("");
			response.addCookie(c);
		}
		response.sendRedirect("/smamt/");
	}
}
