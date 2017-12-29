package com.wang.wregistry.web.controller;

import static org.springframework.web.bind.annotation.RequestMethod.*;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.wang.wregistry.model.RegistryCenterFactory;
import com.wang.wregistry.model.item.HeartBeat;
import com.wang.wregistry.model.item.Item;
import com.wang.wregistry.util.RequestUtil;

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

	@RequestMapping(value = "/register", method = POST)
	public void registerService(@RequestBody List<Item> items) {
		RegistryCenterFactory.getInstance().getRegistryCenter().registry(items);
		return;
	}

	@RequestMapping(value = "/unregister", method = POST)
	public void unregister() {
		RegistryCenterFactory.getInstance().getRegistryCenter().unregistry(requestUtil.getRemoteIp());
		return;
	}

	@RequestMapping(value = "/register/heartbeat", method = GET)
	public Object registerHeartBeat() {
		RegistryCenterFactory.getInstance().getRegistryCenter().heartBeat(requestUtil.getRemoteIp());

		heartBeat.setLastTime(new Date());
		return heartBeat;
	}
}
