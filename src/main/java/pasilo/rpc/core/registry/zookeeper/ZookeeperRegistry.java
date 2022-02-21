package pasilo.rpc.core.registry.zookeeper;

import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.stereotype.Component;
import pasilo.rpc.core.registry.Registry;
import pasilo.rpc.core.registry.cache.ServiceMeta;
import pasilo.rpc.core.transport.util.ServiceUtils;
import pasilo.rpc.core.registry.cache.ClientServiceCache;

import java.util.*;

@Slf4j
@Component
public class ZookeeperRegistry implements Registry {

	private static final ZkClient zkClient;
	private static final String ZK_ROOT_PATH = "/";
	private static final String ZK_PATH_DELIMITER = "/";

	static {
		ResourceBundle resource = ResourceBundle.getBundle("rpc");
		String host = resource.getString("config.server.host");
		if (host == null) {
			throw new ValueException("config server must be set.");
		}
		log.info("connecting to zookeeper: {}.", host);
		zkClient = new ZkClient(host);
	}

	@Override
	public void register(String serviceName, ServiceMeta meta) {
		// 创建服务的持久节点
		String servicePath = ZK_ROOT_PATH + serviceName;
		if (!zkClient.exists(servicePath)) {
			zkClient.createPersistent(servicePath, true);
		}

		String instanceName = ServiceUtils.encodeMeta(meta);
		String instancePath = ZK_ROOT_PATH+serviceName+ZK_PATH_DELIMITER+instanceName;
		if (zkClient.exists(instancePath)) {
			zkClient.delete(instancePath);
			log.info("Instance path already in use: {}.", instancePath);
		}
		// 服务实例使用临时节点
		zkClient.createEphemeral(instancePath);
	}

	@Override
	public List<ServiceMeta> getServiceList(String serviceName) {
		String servicePath = ZK_ROOT_PATH + serviceName;
		List<ServiceMeta> services = ClientServiceCache.get(servicePath);

		if (services.isEmpty()) {
			List<String> instancePaths = zkClient.getChildren(servicePath);
			if (instancePaths != null && instancePaths.isEmpty()) {
				throw new RuntimeException("No service provider for: " + serviceName + ".");
			}

			for (String path: instancePaths) {
				services.add(ServiceUtils.decodeMeta(path));
			}

			ClientServiceCache.put(serviceName, services);
		}

		return services;
	}

	@Override
	public void subscribeService(String serviceName) {
		zkClient.subscribeChildChanges(ZK_ROOT_PATH+serviceName, (parentPath, currentChilds) -> {
			List<ServiceMeta> services = new ArrayList<>();
			for (String child: currentChilds) {
				services.add(ServiceUtils.decodeMeta(child));
			}
			ClientServiceCache.update(serviceName, services);
		});
	}
}
