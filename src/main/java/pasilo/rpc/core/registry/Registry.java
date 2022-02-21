package pasilo.rpc.core.registry;

import pasilo.rpc.core.registry.cache.ServiceMeta;

import java.util.List;

public interface Registry {
	public void register(String serviceName, ServiceMeta host);

	public List<ServiceMeta> getServiceList(String serviceName);

	public void subscribeService(String serviceName);
}
