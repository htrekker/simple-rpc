package pasilo.rpc.core.transport.client;

import pasilo.rpc.core.transport.domain.RpcRequest;
import pasilo.rpc.core.transport.domain.RpcResponse;

import java.util.concurrent.CompletableFuture;

public interface RpcClient {
	public CompletableFuture<RpcResponse> send(RpcRequest rpcRequest);
}
