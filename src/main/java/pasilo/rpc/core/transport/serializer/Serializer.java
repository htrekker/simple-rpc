package pasilo.rpc.core.transport.serializer;

import java.io.Serializable;

public interface Serializer {

	public byte[] serialize(Object obj);

	public <T> T deserialize(byte[] data);

}
