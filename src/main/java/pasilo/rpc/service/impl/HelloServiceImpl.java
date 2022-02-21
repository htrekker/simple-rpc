package pasilo.rpc.service.impl;

import pasilo.rpc.core.annotation.RpcService;
import pasilo.rpc.service.HelloService;

@RpcService
public class HelloServiceImpl implements HelloService {
	@Override
	public String hello(String clientSaid) {
		System.out.println(clientSaid);;
		return "hello from server!";
	}
}
