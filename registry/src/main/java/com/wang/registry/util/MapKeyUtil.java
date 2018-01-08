package com.wang.registry.util;

/**
 * @author wangju
 *
 */
public class MapKeyUtil {

	public static String makeKey(Object... args) {
		return String.join("_", (CharSequence[]) args);
	}
}
