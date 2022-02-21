package pasilo.rpc.core.registry.cache;

import com.alibaba.fastjson.JSON;
import lombok.*;
import pasilo.rpc.core.transport.protocol.ProtocolConstants;

import java.net.InetSocketAddress;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceMeta {
	String ipAddress;
	int port;
	int version;
	int timeout = 10000; // micro seconds
	byte serializer = ProtocolConstants.KRYO_SERIALIZER;
}
