package pasilo.rpc.core.transport.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import pasilo.rpc.core.transport.domain.RpcMessage;
import pasilo.rpc.core.transport.domain.RpcRequest;
import pasilo.rpc.core.transport.domain.RpcResponse;
import pasilo.rpc.core.transport.serializer.JDKSerializer;
import pasilo.rpc.core.transport.serializer.KryoSerializer;
import pasilo.rpc.core.transport.serializer.Serializer;

import java.util.List;

/**
 *  自定义数据结构：
 *    0        1        2        3        4        5        6        7        8
 *    +--------+--------+--------+--------+--------+--------+--------+--------+
 *    | magic  |version |         length                    | type   | codec  |
 *    +--------+--------+--------+--------+--------+--------+----- --+--------+
 *    |            request ID             |                                   |
 *    +--------+--------+--------+--------+                                   +
 *    |                                body                                   |
 *    |                                                                       |
 *    +-----------------------------------------------------------------------+
 *    1B  magic code（魔法数）0x73      1B version（版本）       4B body length（消息长度）
 *    1B messageType（消息类型）    4B compress（压缩类型）  4B  requestId（请求的Id）
 *    body（object类型数据）
 */
@Slf4j
public class RpcMessageDecoder extends ByteToMessageDecoder {

	private static final int HEADER_LENGTH = 12;

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		if (in.readableBytes() < HEADER_LENGTH) {
			return;
		}
		in.markReaderIndex();

		checkMagic(in);
		byte version = in.readByte();

		int length = in.readInt();
		byte msgType = in.readByte();
		byte codec = in.readByte();

		int requestId = in.readInt();
		if (in.readableBytes() < length) {
			in.resetReaderIndex();
			return;
		}

		byte[] data = new byte[length];
		in.readBytes(data);

		RpcMessage rpcMsg = RpcMessage.builder()
				.msgType(msgType)
				.requestId(requestId)
				.codec(codec).build();

		if (codec == ProtocolConstants.KRYO_SERIALIZER) {
			Serializer serializer = new KryoSerializer();
			if (msgType == ProtocolConstants.REQUEST_TYPE) {
				RpcRequest content = serializer.deserialize(data);
				rpcMsg.setData(content);
			} else {
				RpcResponse content = serializer.deserialize(data);
				rpcMsg.setData(content);
			}
		} else if (codec == ProtocolConstants.JDK_SERIALIZER) {
			Serializer serializer = new JDKSerializer();
			if (msgType == ProtocolConstants.REQUEST_TYPE) {
				RpcRequest content = serializer.deserialize(data);
				rpcMsg.setData(content);
			} else {
				RpcResponse content = serializer.deserialize(data);
				rpcMsg.setData(content);
			}
		}

		log.info("server received a message: {}", rpcMsg);
		out.add(rpcMsg);
	}

	private void checkMagic(ByteBuf in){
		byte magic = in.readByte();
		if(magic != ProtocolConstants.MAGIC_NUMBER) {
			throw new IllegalStateException();
		}
	}
}
