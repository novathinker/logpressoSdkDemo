package com.example.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/sdk")
public class SdkViewController {
	
	@RequestMapping(method = RequestMethod.GET)
	public String sdk() {
		return "sdkView";
	}
}
