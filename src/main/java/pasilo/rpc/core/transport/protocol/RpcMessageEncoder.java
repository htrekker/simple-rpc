package pasilo.rpc.core.transport.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import pasilo.rpc.core.transport.domain.RpcMessage;
import pasilo.rpc.core.transport.serializer.KryoSerializer;
import pasilo.rpc.core.transport.serializer.Serializer;

import java.util.concurrent.atomic.AtomicInteger;


/**
 *  自定义数据结构：
 *    0        1        2        3        4        5        6        7        8
 *    +--------+--------+--------+--------+--------+--------+--------+--------+
 *    | magic  |version |         length                    | type   | codec  |
 *    +--------+--------+--------+--------+--------+--------+----- --+--------+
 *    |            RequestID              |                                   |
 *    +--------+--------+--------+--------+                                   +
 *    |                                body                                   |
 *    |                                                                       |
 *    +-----------------------------------------------------------------------+
 *    1B  magic code（魔法数）0x73      1B version（版本）       4B body length（消息长度）
 *    1B messageType（消息类型）    4B compress（压缩类型）  4B  requestId（请求的Id）
 *    body（object类型数据）
 */
public class RpcMessageEncoder extends MessageToByteEncoder<RpcMessage>{
	private static final AtomicInteger REQUEST_ID_GENERATOR = new AtomicInteger(0);
	@Override
	protected void encode(ChannelHandlerContext ctx, RpcMessage msg, ByteBuf buf) throws Exception {
		// set magic
		buf.writeByte(ProtocolConstants.MAGIC_NUMBER);
		// set version of rpc message
		buf.writeByte(ProtocolConstants.DEFAULT_VERSION);

		byte codec = msg.getCodec();
		byte[] content = null;
		if (codec == ProtocolConstants.KRYO_SERIALIZER) {
			Serializer serializer = new KryoSerializer();
			content = serializer.serialize(msg.getData());
		} else {
			content = null;
		}

		// set length
		buf.writeInt(content.length);
		// set msg type
		buf.writeByte(msg.getMsgType());
		// set codec
		buf.writeByte(codec);
		// set request id
		if (msg.getRequestId() == -1) {
			buf.writeInt(REQUEST_ID_GENERATOR.getAndIncrement());
		} else {
			buf.writeInt(msg.getRequestId());
		}
		// set content
		buf.writeBytes(content);
	}
}
