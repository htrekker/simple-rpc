package pasilo.rpc.core.registry.cache;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ClientServiceCache {

	private static Map<String, List<ServiceMeta>> serviceCache = new ConcurrentHashMap<>();

	public static List<ServiceMeta> get(String serviceName) {
		log.info("get data");
		return serviceCache.getOrDefault(serviceName, new ArrayList<>());
	}

	public static void put(String serviceName, List<ServiceMeta> services) {
		log.info("putting data");
		serviceCache.put(serviceName, services);
	}

	public static void update(String serviceName, List<ServiceMeta> services) {
		log.info("updating data");
		serviceCache.replace(serviceName, services);
	}

	public static boolean exists(String serviceName) {
		return serviceCache.containsKey(serviceName);
	}
}
