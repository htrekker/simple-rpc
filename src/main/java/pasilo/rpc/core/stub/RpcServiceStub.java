package pasilo.rpc.core.stub;

import lombok.extern.slf4j.Slf4j;
import pasilo.rpc.core.transport.client.NettyClient;
import pasilo.rpc.core.transport.client.RpcClient;
import pasilo.rpc.core.transport.domain.RpcRequest;
import pasilo.rpc.core.transport.domain.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

@Slf4j
public class RpcServiceStub implements InvocationHandler {

	private RpcClient client;
	public RpcServiceStub() {
		client = new NettyClient();
	}

	public static <T> T getStub(Class<T> clz) {
		System.out.println(clz.getInterfaces());
		return (T) Proxy.newProxyInstance(clz.getClassLoader(), new Class<?>[]{clz}, new RpcServiceStub());
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		String methodName = method.getName();
		Class<?> interfaceClz = method.getDeclaringClass();
		String interfaceName = interfaceClz.getName();

		log.info("Calling class {}'s function {}", interfaceName, methodName);


		RpcRequest rpcRequest = RpcRequest.builder()
				.interfaceType(interfaceClz)
				.interfaceName(interfaceName)
				.methodName(methodName)
				.argTypes(method.getParameterTypes())
				.args(args).build();

		CompletableFuture<RpcResponse> resultFuture = this.client.send(rpcRequest);
		RpcResponse response = resultFuture.get();

		throwIfExceptionally(response);
		return response.getResult();
	}

	private void throwIfExceptionally(RpcResponse response) throws Throwable {
		if (response == null) {
			throw new IllegalStateException("Rpc response with null.");
		}
		if (response.isError()) {
			throw response.getException();
		}
	}
}
