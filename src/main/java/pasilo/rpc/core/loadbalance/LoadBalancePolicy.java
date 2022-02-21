package pasilo.rpc.core.loadbalance;

import pasilo.rpc.core.registry.cache.ServiceMeta;

import java.util.List;

public interface LoadBalancePolicy {
	public ServiceMeta select(List<ServiceMeta> services);
}
