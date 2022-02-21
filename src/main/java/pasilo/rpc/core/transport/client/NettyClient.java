package pasilo.rpc.core.transport.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import pasilo.rpc.core.transport.domain.RpcMessage;
import pasilo.rpc.core.transport.protocol.ProtocolConstants;
import pasilo.rpc.core.transport.protocol.RpcMessageDecoder;
import pasilo.rpc.core.transport.protocol.RpcMessageEncoder;
import pasilo.rpc.core.transport.domain.RpcRequest;
import pasilo.rpc.core.transport.domain.RpcResponse;
import pasilo.rpc.core.transport.util.PendingResults;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class NettyClient implements RpcClient {

	private static final AtomicInteger REQUEST_ID_GENERATOR = new AtomicInteger(0);
	private static NioEventLoopGroup group;
	private static Bootstrap bootstrap;

	static {
		group = new NioEventLoopGroup(1);
		bootstrap = new Bootstrap();
		bootstrap.group(group)
				.channel(NioSocketChannel.class)
				.handler(new LoggingHandler(LogLevel.INFO))
				.handler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ChannelPipeline pipeline = ch.pipeline();
						pipeline.addLast(new RpcMessageEncoder());
						pipeline.addLast(new RpcMessageDecoder());
						pipeline.addLast(new NettyClientRequestHandler());
					}
				});
	}

	public Channel doConnect(InetSocketAddress inetSocketAddress) {
		bootstrap.connect(inetSocketAddress).addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				if (future.isSuccess()) {
					log.info("Client connect {} connected successful.", inetSocketAddress);
				} else {
					throw new IllegalStateException();
				}
			}
		});
		return null;
	}

	public static void shutdown() {
		group.shutdownGracefully();
	}

	@Override
	public CompletableFuture<RpcResponse> send(RpcRequest rpcRequest) {
		CompletableFuture<RpcResponse> resultFuture = new CompletableFuture<>();

		int requestId = REQUEST_ID_GENERATOR.getAndIncrement();
		RpcMessage rpcMsg = RpcMessage.builder()
				.requestId(requestId).msgType(ProtocolConstants.REQUEST_TYPE)
				.codec(ProtocolConstants.KRYO_SERIALIZER)
				.data(rpcRequest).build();

		PendingResults.poolResult(requestId, resultFuture);
		try {
			// 这里进行service discovery、load balance
			ChannelFuture f = bootstrap.connect("127.0.0.1", 20001).sync();
			f.channel().writeAndFlush(rpcMsg).addListener((ChannelFutureListener) future -> {
				if (future.isSuccess()) {
					log.info("client send message success.");
				} else {
					future.channel().close();
					resultFuture.completeExceptionally(future.cause());
					log.error("Send failed with cause:", future.cause());
				}
			});
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return resultFuture;
	}
}
