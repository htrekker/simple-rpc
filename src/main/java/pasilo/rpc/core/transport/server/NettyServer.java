package pasilo.rpc.core.transport.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import pasilo.rpc.core.registry.Registry;
import pasilo.rpc.core.registry.zookeeper.ZookeeperRegistry;
import pasilo.rpc.core.transport.protocol.RpcMessageDecoder;
import pasilo.rpc.core.transport.protocol.RpcMessageEncoder;

import java.util.Enumeration;
import java.util.ResourceBundle;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyServer implements RpcServer{

	private final NioEventLoopGroup boss = new NioEventLoopGroup(1);
	private final NioEventLoopGroup workers = new NioEventLoopGroup();

	@Override
	public void start() {
		try {
			ResourceBundle config = ResourceBundle.getBundle("rpc");

//			int serverPort = Integer.valueOf(config.getString("rpc.server.port"));

			ServerBootstrap server = new ServerBootstrap();
			server.group(boss, workers)
					.channel(NioServerSocketChannel.class)
					.handler(new LoggingHandler(LogLevel.INFO))
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel socketChannel) throws Exception {
							ChannelPipeline pipeline = socketChannel.pipeline();
							pipeline.addLast(new RpcMessageEncoder());
							pipeline.addLast(new RpcMessageDecoder());
							pipeline.addLast(new NettyServerRequestHandler());
						}
					});

			ChannelFuture f = server.bind(20001).sync();
			f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			boss.shutdownGracefully();
			workers.shutdownGracefully();
		}
	}

	@Override
	public void shutdownGracefully() {
		boss.shutdownGracefully();
		workers.shutdownGracefully();

	}

	public static void main(String[] args) {
		RpcServer server = new NettyServer();
		System.out.println("starting server");
		server.start();
		System.out.println("server start success!");
	}
}
