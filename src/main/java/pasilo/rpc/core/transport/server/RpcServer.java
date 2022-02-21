package pasilo.rpc.core.transport.server;

public interface RpcServer {
	public void start();
	
	public void shutdownGracefully();
}
