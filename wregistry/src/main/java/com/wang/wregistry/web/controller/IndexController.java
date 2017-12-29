package com.wang.wregistry.web.controller;

import static org.springframework.web.bind.annotation.RequestMethod.*;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author wangju
 *
 */
@Controller
public class IndexController {
	private static final String INDEX_PAGE = "index";

	@RequestMapping(value = "/", method = GET)
	public String index() {
		return INDEX_PAGE;
	}
}
