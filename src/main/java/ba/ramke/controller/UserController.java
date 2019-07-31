package ba.ramke.controller;

import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import ba.ramke.dao.DataSourceRepository;
import ba.ramke.dao.UserRepository;
import ba.ramke.model.DataSource;
import ba.ramke.model.User;

@Controller
public class UserController {
	
	@Autowired
	private UserRepository userDao;

	@Autowired
	private DataSourceRepository dataSourceDao;
	
	@RequestMapping(value = "/user/save", method = RequestMethod.POST)
	public View createPerson(@ModelAttribute User user, ModelMap model) throws NoSuchAlgorithmException {
		user.setId(UUID.randomUUID().toString());
		System.out.println(user.toString());
		DataSource ds = new DataSource(user.getId());
		userDao.addUser(user);
		dataSourceDao.addPersonToCollection(ds);
		return new RedirectView("/");
	}

}
