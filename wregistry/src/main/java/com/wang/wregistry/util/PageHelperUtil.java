package com.wang.wregistry.util;

/**
 * @author wangju
 *
 */
public class PageHelperUtil {
	public static int DEFAULT_PAGE_SIZE = 30;

	public static int page2Offset(int page) {
		return (page <= 0) ? 0 : (page - 1) * DEFAULT_PAGE_SIZE;
	}
}
