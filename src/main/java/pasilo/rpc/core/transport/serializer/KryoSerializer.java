package pasilo.rpc.core.transport.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class KryoSerializer implements Serializer{

	private static final ThreadLocal<Kryo> kryoLocal = new ThreadLocal<Kryo>() {
		@Override
		protected Kryo initialValue() {
			Kryo kryo = new Kryo();

			kryo.setReferences(true);

			kryo.setRegistrationRequired(false);

			((Kryo.DefaultInstantiatorStrategy) kryo.getInstantiatorStrategy())
					.setFallbackInstantiatorStrategy(new StdInstantiatorStrategy());
			return kryo;
		}
	};

	private Kryo getInstance() {
		return kryoLocal.get();
	}

	@Override
	public byte[] serialize(Object obj) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		Output output = new Output(byteArrayOutputStream);

		Kryo kryo = getInstance();
		kryo.writeClassAndObject(output, obj);
		output.flush();

		return byteArrayOutputStream.toByteArray();
	}

	@Override
	public <T> T deserialize(byte[] data) {
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
		Input input = new Input(byteArrayInputStream);

		Kryo kryo = getInstance();
		return (T) kryo.readClassAndObject(input);
	}
}
