package com.wang.web.controller;

import static org.springframework.web.bind.annotation.RequestMethod.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author wangju
 *
 */
@Controller
public class HelpController {

	@RequestMapping(value = "/help", method = GET)
	public String help() {
		return "help";
	}
}
