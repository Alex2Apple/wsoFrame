package com.wang.wregistry.web.controller;

import static org.springframework.web.bind.annotation.RequestMethod.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wang.wregistry.config.ErrorCode;
import com.wang.wregistry.config.ErrorCodeWrapper;
import com.wang.wregistry.model.DataCenterFactory;
import com.wang.wregistry.model.SubscriberCenterFactory;
import com.wang.wregistry.model.item.HeartBeat;
import com.wang.wregistry.model.item.Item;
import com.wang.wregistry.util.RequestUtil;

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

	@RequestMapping(value = "/subscriber", method = GET)
	@ResponseBody
	public Object subscribeService(@RequestParam("interface") String interfaceName) {
		try {
			String host = requestUtil.getRemoteIp();
			SubscriberCenterFactory.getInstance().getSubscriberCenter().subcribe(interfaceName, host);
			return DataCenterFactory.getInstance().getDataCenter().getItems(interfaceName);
		} catch (Exception e) {
			e.printStackTrace();
			return new ErrorCodeWrapper(ErrorCode.ERR_INIT, e.getMessage());
		}
	}

	@RequestMapping(value = "/subscriber", method = GET)
	@ResponseBody
	public Object subscribeServices(@RequestBody List<String> services) {
		try {
			String host = requestUtil.getRemoteIp();
			List<Item> list = new ArrayList<>();
			for (String interfaceName : services) {
				SubscriberCenterFactory.getInstance().getSubscriberCenter().subcribe(interfaceName, host);
				list.addAll(DataCenterFactory.getInstance().getDataCenter().getItems(interfaceName));
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return new ErrorCodeWrapper(ErrorCode.ERR_INIT, e.getMessage());
		}
	}

	@RequestMapping(value = "/subscribe/heartbeat", method = GET)
	public Object subscriberHeartBeat() {
		if (SubscriberCenterFactory.getInstance().getSubscriberCenter().notifyExecute(requestUtil.getRemoteIp())) {
			return "redirect:/subscribe/refresh";
		}
		heartBeat.setLastTime(new Date());
		return heartBeat;
	}

	@RequestMapping(value = "/subscribe/refresh", method = GET)
	public Object subscriberRefresh() {
		return SubscriberCenterFactory.getInstance().getSubscriberCenter().notifyFinish(requestUtil.getRemoteIp());
	}
}
