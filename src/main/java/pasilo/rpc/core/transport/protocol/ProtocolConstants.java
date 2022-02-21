package pasilo.rpc.core.transport.protocol;

public class ProtocolConstants {
	public static final byte MAGIC_NUMBER = 0x73;
	public static final byte DEFAULT_VERSION = 0x01;

	public static final byte KRYO_SERIALIZER = 0x01;
	public static final byte JDK_SERIALIZER = 0x02;
	// type field
	public static final byte REQUEST_TYPE = 0x01;
	public static final byte RESPONSE_TYPE = 0x02;
}
