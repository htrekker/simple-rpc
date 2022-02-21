package pasilo.rpc.core.transport.domain;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@Builder
@ToString
public class RpcResponse implements Serializable {
	// result
	private Object result;
	private Class<?> clz;

	// exception
	private Throwable exception;

	public boolean isSuccess() {
		return this.result != null;
	}
	public boolean isError() {
		return exception != null;
	}
}