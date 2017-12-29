package com.wang.wregistry.model.item;

import java.util.List;

import org.springframework.stereotype.Component;

/**
 * @author wangju
 *
 */
@Component
public class Items {

	private boolean hasNext;

	private int page;

	private List<Item> items;

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

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}

}
