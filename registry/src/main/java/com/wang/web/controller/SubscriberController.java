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
public class SubscriberController {

	@Autowired
	private RequestUtil requestUtil;

	@Autowired
	private HeartBeat heartBeat;

	@RequestMapping(value = "/subscribe/batch", method = POST)
	@EnableResultWrap
	@ResponseBody
	public Object subscribeService(@RequestBody List<URL> items) {
		return RegistryCenterFactory.getInstance().getRegistryCenter().subscribe(items, requestUtil.getRemoteIp());
	}

	@RequestMapping(value = "/subscribe", method = POST)
	@EnableResultWrap
	@ResponseBody
	public Object subscribeService(@RequestBody URL item) {
		return RegistryCenterFactory.getInstance().getRegistryCenter().subscribe(item, requestUtil.getRemoteIp());
	}

	@RequestMapping(value = "/unsubscribe", method = POST)
	@EnableResultWrap
	@ResponseBody
	public Object unsubscribeService(@RequestBody URL item) {
		RegistryCenterFactory.getInstance().getRegistryCenter().unsubscribe(item, requestUtil.getRemoteIp());
		return null;
	}

	@RequestMapping(value = "/subscribe/heartbeat", method = GET)
	@ResponseBody
	public Object subscriberHeartBeat() {
		heartBeat.setLastTime(new Date());
		if (RegistryCenterFactory.getInstance().getRegistryCenter().heartBeat(requestUtil.getRemoteIp(), false)) {
			heartBeat.setLocation("/subscribe/refresh");
			heartBeat.setUpdated(true);
		} else {
			heartBeat.setUpdated(false);
			heartBeat.setLocation("");
		}

		return heartBeat;
	}

	@RequestMapping(value = "/subscribe/refresh", method = GET)
	@EnableResultWrap
	@ResponseBody
	public Object subscriberRefresh() {
		return RegistryCenterFactory.getInstance().getRegistryCenter().notifyListener(requestUtil.getRemoteIp());
	}
}
