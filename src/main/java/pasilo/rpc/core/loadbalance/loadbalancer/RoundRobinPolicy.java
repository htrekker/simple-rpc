package pasilo.rpc.core.loadbalance.loadbalancer;

import pasilo.rpc.core.loadbalance.LoadBalancePolicy;
import pasilo.rpc.core.registry.cache.ServiceMeta;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinPolicy implements LoadBalancePolicy {

	private AtomicInteger cyclicCounter = new AtomicInteger(0);

	@Override
	public ServiceMeta select(List<ServiceMeta> services) {

		return null;
	}

	public int incrementAndGet(int module) {
		for(;;) {
			int cur = cyclicCounter.get();
			int next = (cur + 1)%module;
			if (cyclicCounter.compareAndSet(cur, next)) {
				return next;
			}
		}
	}
}
