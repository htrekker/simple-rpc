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

import java.net.*;
import java.util.Enumeration;
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
			try {
				registerService(context);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取site-local address
	 *
	 * @return site-local地址
	 * @throws SocketException
	 * @throws UnknownHostException
	 */
	private InetAddress getLocalAddress() throws SocketException, UnknownHostException{
		InetAddress address = null;
		try {
			for (Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();ifaces.hasMoreElements();) {
				NetworkInterface iface = ifaces.nextElement();
				for (Enumeration<InetAddress> addressIter = iface.getInetAddresses();addressIter.hasMoreElements();) {
					InetAddress inetAddress = addressIter.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						if (inetAddress.isSiteLocalAddress() && inetAddress instanceof Inet4Address) {
							return inetAddress;
						} else if (address == null && inetAddress instanceof Inet4Address) {
							address = inetAddress;
						}
					}
				}
			}

			if (address != null) {
				return address;
			}

			address = InetAddress.getLocalHost();
			return address;
		} catch (SocketException | UnknownHostException e) {
			e.printStackTrace();
			throw e;
		}

	}

	private void registerService(ApplicationContext context) throws UnknownHostException {
		InetAddress address = null;
		try {
			address = getLocalAddress();
		} catch (SocketException |UnknownHostException e) {
			log.error("unable to get local address.");
			throw new UnknownHostException("Can't get host address.");
		}
		Map<String, Object> beans = context.getBeansWithAnnotation(RpcService.class);
		if (beans.size() > 0) {
			for (Object obj : beans.values()) {
				Class<?> clazz = obj.getClass();
				ServiceMeta meta = ServiceMeta.builder()
						.ipAddress(address.getHostAddress())
						.port(20001)
						.version(1).build();
				System.out.println(clazz.getName());
				zkRegistry.register(clazz.getInterfaces()[0].getName(), meta);
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
