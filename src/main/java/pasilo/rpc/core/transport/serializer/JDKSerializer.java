package pasilo.rpc.core.transport.serializer;

import java.io.*;

public class JDKSerializer implements Serializer{
	@Override
	public byte[] serialize(Object obj) {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try {
			ObjectOutputStream stream = new ObjectOutputStream(buffer);
			stream.writeObject(obj);
			return buffer.toByteArray();
		} catch (NotSerializableException e) {
			System.out.println("your entity has not implemented Serializable interface.");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public <T> T deserialize(byte[] data) {
		ByteArrayInputStream buffer = new ByteArrayInputStream(data);
		try(ObjectInputStream stream = new ObjectInputStream(buffer)){
			return (T) stream.readObject();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

}
