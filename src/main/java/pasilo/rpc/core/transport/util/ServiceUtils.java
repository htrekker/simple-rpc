package pasilo.rpc.core.transport.util;

import com.alibaba.fastjson.JSON;
import pasilo.rpc.core.registry.cache.ServiceMeta;

public class ServiceUtils {


	public static String encodeMeta(ServiceMeta meta) {
		return JSON.toJSONString(meta);
	}

	public static ServiceMeta decodeMeta(String meta) {
		return JSON.parseObject(meta, ServiceMeta.class);
	}
}
