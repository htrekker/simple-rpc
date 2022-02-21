package pasilo.rpc;

import pasilo.rpc.core.stub.RpcServiceStub;
import pasilo.rpc.core.transport.client.NettyClient;
import pasilo.rpc.service.HelloService;

public class ClientMain {
	public static void main(String[] args) {
		HelloService helloService = RpcServiceStub.getStub(HelloService.class);
		String ret = helloService.hello("hello from client");
		System.out.println(ret);
		NettyClient.shutdown();
	}
}
