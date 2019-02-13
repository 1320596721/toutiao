package com.project.toutiao.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

import com.project.toutiao.aspect.LogAspect;
import com.project.toutiao.model.User;
import com.project.toutiao.service.ToutiaoService;

import java.util.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@Controller
public class IndexController {
	private static final Logger logger = LoggerFactory.getLogger(IndexController.class);
	@Autowired
	private ToutiaoService toutiaoService;
	
	@RequestMapping(value = {"/", "/index"})
	@ResponseBody
	public String index(HttpSession session) {
		logger.info("Visit Index.");
		return "Hello World "+session.getAttribute("msg")
		+ "<br>"+toutiaoService.say();
	}
	
	@RequestMapping(value= {"/profile/{groupId}/{userId}"})
	@ResponseBody
	public String profile(@PathVariable("groupId") String groupId,
			@PathVariable("userId") int userId,
			@RequestParam(value="type",defaultValue="1") int type,
			@RequestParam(value="key",defaultValue="nowcoder") String key) {
		return String.format("{%s},{%d},{%d},{%s}",groupId,userId,type,key);
	}
	@RequestMapping(value= {"/vm"})
	public String news(Model model) {
		model.addAttribute("value1","vv1");
		List<String> colors = Arrays.asList(new String[] {"red","green","blue"});
		Map<String,String> map = new HashMap<String,String>();
		for(int i=0;i<4;++i) {
			map.put(String.valueOf(i), String.valueOf(i*i));
			
		}
		model.addAttribute("colors", colors);
		model.addAttribute("map",map);
		model.addAttribute("user",new User("Jim"));
		return "news";
	}
	
	@RequestMapping(value= {"/request"})
	@ResponseBody
	public String request(HttpServletRequest request,
			HttpServletResponse response
			)  {
		StringBuilder sb = new StringBuilder();
		Enumeration<String> headerNames = request.getHeaderNames();
		while(headerNames.hasMoreElements()) {
			String name = headerNames.nextElement();
			sb.append(name+":"+request.getHeader(name)+"<br>");
		}
		for(Cookie cookie:request.getCookies()) {
			sb.append("Cookie:");
			sb.append(cookie.getName()+":");
			sb.append(cookie.getValue()+"<br>");
		}
		return sb.toString();
	}

	@RequestMapping(value= {"/response"})
	@ResponseBody
	public String response(@CookieValue(value="nowcoderid",defaultValue="a") String nowcoderid,
			@RequestParam(value="key",defaultValue="key")String key,
			@RequestParam(value="value",defaultValue="value")String value,
			HttpServletResponse response) {
		response.addCookie(new Cookie(key,value));
		response.addHeader(key, value);
		return "NowCoderId from Cookie:"+nowcoderid;
	}
	
	@RequestMapping(value= {"/redirect/{code}"})
	public RedirectView redirect(@PathVariable("code")int code,
			HttpSession session) {
		RedirectView ret = new RedirectView("/",true);
		if(code==301) {
			ret.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
		}
		session.setAttribute("msg", "jump from redirect.");
		return ret;
	}
	@RequestMapping("/admin")
	@ResponseBody
	public String admin(@RequestParam(value="key",required=false)String key) {
		if("admin".equals(key)) {
			return "hello admin";
		}
		throw new IllegalArgumentException("key error");
	}
	
	@ExceptionHandler
	@ResponseBody
	public String error(Exception e) {
		return "error:"+e.getMessage();
	}
}

