package com.wang.web.controller;

import static org.springframework.web.bind.annotation.RequestMethod.*;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wang.center.factory.RegistryCenterFactory;
import com.wang.registry.config.EnableResultWrap;
import com.wang.registry.model.HeartBeat;
import com.wang.registry.model.URL;
import com.wang.registry.util.RequestUtil;

/**
 * @author wangju
 *
 */
@Controller
public class RegisterController {

	@Autowired
	private RequestUtil requestUtil;

	@Autowired
	private HeartBeat heartBeat;

	@RequestMapping(value = "/registry/batch", method = POST)
	@EnableResultWrap
	@ResponseBody
	public Object registerService(@RequestBody List<URL> items) {
		RegistryCenterFactory.getInstance().getRegistryCenter().registry(items);
		return null;
	}

	@RequestMapping(value = "/registry", method = POST)
	@EnableResultWrap
	@ResponseBody
	public Object registerService(@RequestBody URL item) {
		RegistryCenterFactory.getInstance().getRegistryCenter().registry(item);
		return null;
	}

	@RequestMapping(value = "/unregistry", method = POST)
	@EnableResultWrap
	@ResponseBody
	public Object unregisterService(@RequestBody URL item) {
		RegistryCenterFactory.getInstance().getRegistryCenter().unregistry(item);
		return null;
	}

	@RequestMapping(value = "/registry/heartbeat", method = GET)
	@ResponseBody
	public Object registerHeartBeat() {
		RegistryCenterFactory.getInstance().getRegistryCenter().heartBeat(requestUtil.getRemoteIp(), true);
		heartBeat.setLastTime(new Date());
		heartBeat.setUpdated(false);
		heartBeat.setLocation("");
		return heartBeat;
	}
}
