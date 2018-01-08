package com.wang.registry.config;

public class RegistryConstants {
	public static final String RESULT_CODE_KEY = "code";
	public static final String RESULT_MESSAGE_KEY = "message";
	public static final String RETURN_RESULT_ATTACH_KEY = "data";
	public static final String ERROR_RESULT_ATTACH_KEY = "detail";
	public static final int DEFAULT_RETURN_RESULT_CODE = 0;
	public static final String DEFAULT_RETURN_RESULT_MESSAGE = "ok";

	public static final int DEFAULT_INTERFACE_NUM = 200; // 默认接口数量
	public static final int DEFATULT_PROVIDER_HOST = 10; // 默认提供者数量
	public static final int DEFAULT_SUBSCRIBER_HOST = 20; // 默认订阅者数量

	public static final long DEFAULT_IDLE_TIMEOUT = 5 * 60 * 1000000; // 空闲时间5分钟, 单位微秒

	public static final int DEFAULT_THREAD_NUM = 4;
	public static final int DEFAILT_SCHEDULE_THREAD_NUM = 4;
	public static final int DEFAULT_SCHEDULE_DEDAY = 5 * 60;
	public static final int DEFAULT_SCHEDULE_PERIOD = 5 * 60 + 30;

	public static final int DEFAULT_REFRESH_PERIOD = 30; // 管理端列表刷新间隔

	public static final String SERVER_CONFIG_FILE = "registry.properties"; // 配置文件
	public static final String CLUSTER_NODE_NAME_KEY = "registry.cluster.node";
	public static final String CLUSTER_MSATER_NODE_KEY = "registry.cluster.master";
	public static final String CLUSTER_SLAVES_NODE_KEY = "registry.cluster.slave";
	public static final String CLUSTER_NODE_MASTER_VALUE = "master";
	public static final String CLUSTER_NODE_SLAVE_VALUE = "slave";

	public static final int DEFAULT_CLUSTER_HEARTBEAT_PERIOD = 10;
	public static final String CLUSTER_HEARTBEAT_HELLO_MAPPING = "/cluster/heartbeat";
	public static final String CLUSTER_REPLICATION_MAPPING = "/cluster/replication";
	public static final String CLUSTER_RECOVER_CHECK_MAPPING = "/cluster/recover";
}
