package pasilo.rpc.core.loadbalance.loadbalancer;

import lombok.extern.slf4j.Slf4j;
import pasilo.rpc.core.loadbalance.LoadBalancePolicy;
import pasilo.rpc.core.registry.cache.ServiceMeta;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class RoundRobinPolicy implements LoadBalancePolicy {

	private AtomicInteger cyclicCounter = new AtomicInteger(0);

	@Override
	public ServiceMeta select(List<ServiceMeta> services) {
		if (services == null || services.isEmpty()) {
			log.warn("no available services.");
			return null;
		}

		int selectedIndex = incrementAndGet(services.size());
		ServiceMeta selected = services.get(selectedIndex);
		return selected;
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
