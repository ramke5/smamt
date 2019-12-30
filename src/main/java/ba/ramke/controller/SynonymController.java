package ba.ramke.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import ba.ramke.dao.SynonymRepository;
import ba.ramke.helper.Serialize;
import ba.ramke.model.Synonym;

@Controller
public class SynonymController {
	
	@Autowired
	private SynonymRepository synonymDao;
	
	@RequestMapping(value = "/smamt/synonyms", method = RequestMethod.GET)
	public @ResponseBody Serialize getAllActiveSynoyms(ModelMap model, @ModelAttribute("userId") String userId, @ModelAttribute("categoryId") String categoryId, @ModelAttribute("keywordId") String keywordId) {
		Serialize serializedObj = new Serialize();
		serializedObj.setElements(synonymDao.getAllActiveSynonymsOfKeyword(userId, categoryId, keywordId));
		System.out.println("Obj is " + serializedObj.getElements());
		return serializedObj;
	}

	@RequestMapping(value = "/smamt/deletedsynonyms", method = RequestMethod.GET)
	public @ResponseBody Serialize getAllDeledSynonyms(ModelMap model, @ModelAttribute("userId") String userId, @ModelAttribute("categoryId") String categoryId, @ModelAttribute("keywordId") String keywordId) {
		Serialize serializedObj = new Serialize();
		serializedObj.setElements(synonymDao.getAllDeletedSynonymsOfKeyword(userId, categoryId, keywordId));
		System.out.println("Obj is " + serializedObj.getElements());
		return serializedObj;
	}

	@RequestMapping(value = "/smamt/addsynonyms", method = RequestMethod.POST)
	@ResponseBody
	public void addSynonymsToKeyword(ModelMap modelMap, @ModelAttribute("userId") String userId, @ModelAttribute("categoryId") String categoryId, @ModelAttribute("keywordId") String keywordId, @ModelAttribute("synonyms") String nameOfSynonyms) {
		
		if (nameOfSynonyms.length() != 0) {
			Synonym synonym = new Synonym(UUID.randomUUID().toString(), nameOfSynonyms, 1);
			synonymDao.addSynonymToKeyword(userId, categoryId, keywordId, synonym);
		} else {
			System.out.println("Empty");
			modelMap.put("error", "Please fill field");
		}
	}

	@RequestMapping(value = "/smamt/deletesynonym", method = RequestMethod.POST)
	@ResponseBody
	public void deleteSynonyms(ModelMap modelMap, @ModelAttribute("userId") String userId, @ModelAttribute("categoryId") String categoryId, @ModelAttribute("keywordId") String keywordId, @ModelAttribute("synonymId") String synonymId) {
		synonymDao.deleteSynonym(userId, categoryId, keywordId, synonymId);
	}

	@RequestMapping(value = "/smamt/restoresynonym", method = RequestMethod.POST)
	@ResponseBody
	public void restoreSynonyms(ModelMap modelMap, @ModelAttribute("userId") String userId, @ModelAttribute("categoryId") String categoryId, @ModelAttribute("keywordId") String keywordId, @ModelAttribute("synonymId") String synonymId) {
		synonymDao.restoreSynonym(userId, categoryId, keywordId, synonymId);
	}

	@RequestMapping(value = "/smamt/synonymName", method = RequestMethod.POST)
	@ResponseBody
	public void changeSynonymName(ModelMap modelMap, @ModelAttribute("userId") String userId, @ModelAttribute("categoryId") String categoryId, @ModelAttribute("keywordId") String keywordId, @ModelAttribute("synonymId") String synonymId, @ModelAttribute("synonymName") String synonymName) {
		synonymDao.changeSynonymName(userId, categoryId, keywordId, synonymId, synonymName);
	}

}
