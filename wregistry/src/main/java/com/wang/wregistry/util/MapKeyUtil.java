package com.wang.wregistry.util;

/**
 * @author wangju
 *
 */
public class MapKeyUtil {

	public static String makeKey(Object... args) {
		return String.join("_", (CharSequence[]) args);
	}
}
