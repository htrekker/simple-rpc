package pasilo.rpc.core.transport.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import pasilo.rpc.core.transport.domain.RpcMessage;
import pasilo.rpc.core.transport.domain.RpcResponse;
import pasilo.rpc.core.transport.protocol.ProtocolConstants;
import pasilo.rpc.core.transport.util.PendingResults;

@Slf4j
public class NettyClientRequestHandler extends SimpleChannelInboundHandler<RpcMessage> {
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, RpcMessage msg) throws Exception {
		log.info("client receive request id: {}", msg.getRequestId());

		System.out.println(msg);
		if (msg.getMsgType() == ProtocolConstants.RESPONSE_TYPE) {
			RpcResponse rpcResponse = (RpcResponse) msg.getData();

			int requestId = msg.getRequestId();
			if (rpcResponse.isError()) {
				log.error("request {} complete with error. {}", requestId, rpcResponse.getException());
				PendingResults.completeExceptionally(requestId, rpcResponse.getException());
			} else {
				log.info("Request {} complete successful.", requestId);
				PendingResults.complete(requestId, rpcResponse);
			}
		}
	}
}
