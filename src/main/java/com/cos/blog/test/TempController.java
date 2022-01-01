package com.cos.blog.test;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TempController {
	
	// http://localhost:8000/blog/temp/home
	@GetMapping("/temp/home")
	public String tempHome() {
		System.out.println("temphome");
		// 파일리턴 기본경로 : src/main/resources/static
		return "/home.html";
	}
	
	@GetMapping("/temp/jsp")
	public String tempJsp()
	{
		//prefix : /WEB-INF/views/
		// suffix : .jsp
		// full name : /WEB-INF/views/test.jsp
		return "/test";
	}
}
