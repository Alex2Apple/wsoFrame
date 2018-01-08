package com.wang.frame.remote;

import java.io.IOException;

/**
 * @author wangju
 *
 */
public interface Server {

	void open() throws IOException;

	void close();
}
