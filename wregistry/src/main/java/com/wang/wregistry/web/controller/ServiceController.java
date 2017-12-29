package com.wang.wregistry.web.controller;

import static org.springframework.web.bind.annotation.RequestMethod.*;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wang.wregistry.config.ErrorCode;
import com.wang.wregistry.config.ErrorCodeWrapper;
import com.wang.wregistry.model.DataCenterFactory;

/**
 * @author wangju
 *
 */
@Controller
public class ServiceController {

	@RequestMapping(value = "/services", method = GET)
	@ResponseBody
	public Object itemsByPage(@RequestParam("page") int page) {
		try {
			return DataCenterFactory.getInstance().getDataCenter().getItems(page);
		} catch (Exception e) {
			e.printStackTrace();
			return new ErrorCodeWrapper(ErrorCode.ERR_INIT, e.getMessage());
		}
	}
}
