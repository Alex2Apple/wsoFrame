package com.wang.registry.model;

import java.util.List;

import org.springframework.stereotype.Component;

/**
 * @author wangju
 *
 */
@Component
public class URLs {

	private boolean hasNext;

	private int page;

	private List<URL> items;

	public boolean isHasNext() {
		return hasNext;
	}

	public void setHasNext(boolean hasNext) {
		this.hasNext = hasNext;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public List<URL> getItems() {
		return items;
	}

	public void setItems(List<URL> items) {
		this.items = items;
	}

}
