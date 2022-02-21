package pasilo.rpc.core.transport.server;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import pasilo.rpc.core.registry.Registry;
import pasilo.rpc.core.registry.cache.ServiceMeta;
import pasilo.rpc.core.registry.zookeeper.ZookeeperRegistry;
import pasilo.rpc.core.transport.domain.RpcMessage;
import pasilo.rpc.core.transport.domain.RpcRequest;
import pasilo.rpc.core.transport.domain.RpcResponse;
import pasilo.rpc.core.transport.protocol.ProtocolConstants;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.concurrent.CompletableFuture.runAsync;

@Slf4j
@Component
public class NettyServerRequestHandler extends ChannelInboundHandlerAdapter implements ApplicationContextAware {

	private static final Executor pool = Executors.newCachedThreadPool();
	private static ApplicationContext container;

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object obj) throws Exception {
		RpcMessage msg = (RpcMessage) obj;
		if (msg.getMsgType() == ProtocolConstants.REQUEST_TYPE) {
			RpcRequest rpcRequest = (RpcRequest) msg.getData();
			msg.setMsgType(ProtocolConstants.RESPONSE_TYPE);
			CompletableFuture<Object> future = CompletableFuture.supplyAsync(() -> {
				Object result = null;

				try {
					Object bean = getBean(rpcRequest.getInterfaceType());
					Method method = getMethod(bean, rpcRequest.getMethodName(),
							rpcRequest.getArgTypes());
					result = method.invoke(bean, rpcRequest.getArgs());
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}

				return result;
			}, pool);
			future.thenAccept((result) -> {
				RpcResponse response = RpcResponse.builder()
						.result(result).clz(result.getClass()).build();

				msg.setData(response);
				ctx.writeAndFlush(msg).addListener(new ChannelFutureListener() {
					@Override
					public void operationComplete(ChannelFuture future) throws Exception {
						if (future.isSuccess()) {
							log.info("Send successful.");
						} else {
							log.error("Send error: {}.", future.cause());
						}
					}
				});
			});
			future.exceptionally(new Function<Throwable, Object>() {
				@Override
				public Object apply(Throwable throwable) {
					RpcResponse response = RpcResponse.builder()
							.exception(throwable).build();

					msg.setData(response);
					msg.setMsgType(ProtocolConstants.RESPONSE_TYPE);
					ChannelFuture f = ctx.writeAndFlush(msg);
					log.error("server execution with error: [{}]", throwable);
					return null;
				}
			});

		}
	}

	public Object getBean(Class<?> type) {
		return container.getBean(type);
	}

	public Method getMethod(Object bean, String methodName, Class<?>... parametersType) {
		Method calledMethod = null;
		try {
			calledMethod = bean.getClass().getMethod(methodName, parametersType);
		} catch (NoSuchMethodException e) {
			log.error("{} has no method named: {}.", bean.getClass().getName(), methodName);
		}
		return calledMethod;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		container = applicationContext;
	}
}
