package pasilo.rpc.core.starter;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import pasilo.rpc.core.annotation.RpcService;
import pasilo.rpc.core.registry.Registry;
import pasilo.rpc.core.registry.cache.ServiceMeta;
import pasilo.rpc.core.transport.server.NettyServer;
import pasilo.rpc.core.transport.server.RpcServer;

import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class RpcBootStarter implements ApplicationListener<ContextRefreshedEvent> {

	private Registry zkRegistry;

	public RpcBootStarter(Registry registry) {
		this.zkRegistry = registry;
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		log.info("starting registry....");
		if (Objects.isNull(event.getApplicationContext().getParent())) {
			ApplicationContext context = event.getApplicationContext();
			// 注册服务
			registerService(context);
		}
	}

	private void registerService(ApplicationContext context) {
		Map<String, Object> beans = context.getBeansWithAnnotation(RpcService.class);
		if (beans.size() > 0) {
			for (Object obj : beans.values()) {
				Class<?> clazz = obj.getClass();
				ServiceMeta meta = ServiceMeta.builder()
						.version(1).build();
				zkRegistry.register(clazz.getName(), meta);
			}
		} else {
			throw new RuntimeException(
					"no service provider found.");
		}

		Thread serverThread = new Thread(new Runnable() {
			@Override
			public void run() {
				RpcServer server = new NettyServer();
				server.start();
			}
		});
		serverThread.start();
		log.info("server start success.");
	}
}
