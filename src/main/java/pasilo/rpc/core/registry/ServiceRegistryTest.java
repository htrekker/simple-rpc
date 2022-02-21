package pasilo.rpc.core.registry;

import lombok.SneakyThrows;
import pasilo.rpc.core.registry.cache.ServiceMeta;
import pasilo.rpc.core.registry.zookeeper.ZookeeperRegistry;
import pasilo.rpc.core.transport.protocol.ProtocolConstants;
import sun.awt.windows.ThemeReader;

import java.util.HashMap;

public class ServiceRegistryTest {

	public static volatile int counter = 1;
	public static Object lock = new Object();

	public static void main(String[] args) throws InterruptedException {


//		ZookeeperRegistry registry = new ZookeeperRegistry();
//
//		ServiceMeta meta = ServiceMeta.builder()
//				.ipAddress("127.0.0.1")
//				.port(24856)
//				.serializer(ProtocolConstants.KRYO_SERIALIZER).build();
//		registry.register("HelloService", meta);
//
//		registry.subscribeService("HelloService");
//
//		meta.setPort(24866);
//		registry.register("HelloService", meta);
//
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}

//		System.out.println(registry.getServiceList("HelloService"));
	}
//	public static void main(String[] args) throws InterruptedException {
//		ZkClient client = new ZkClient("10.99.8.28:21810");
//		System.out.println("success");
//
//		if (!client.exists("/service1")){
//			client.createPersistent("/service1");
//		}
//
//		Map<String, InetSocketAddress> cachedData = new ConcurrentHashMap<>();
//
//		client.subscribeChildChanges("/service1", (parentPath, currentChilds) -> {
//			System.out.println(currentChilds);
//			System.out.println(parentPath);
//		});
//
//		InetSocketAddress address = new InetSocketAddress("192.168.10.1", 3440);
//		String instancePath = client.createEphemeralSequential("/service1/instance", address);
//		cachedData.put(instancePath, address);
//		System.out.println(instancePath);
//
//		instancePath = client.createPersistentSequential("/service1/instance", address);
//		cachedData.put(instancePath, address);
//
//		System.out.println(instancePath);
//		client.delete(instancePath);
//
//		Thread.sleep(10000);
//	}
}
