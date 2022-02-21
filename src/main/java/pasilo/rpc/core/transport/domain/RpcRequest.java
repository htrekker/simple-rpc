package pasilo.rpc.core.transport.domain;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@Builder
@ToString
public class RpcRequest implements Serializable {
	private String interfaceName;
	private Class<?> interfaceType;
	private String methodName;
	
	private Object[] args;
	private Class<?>[] argTypes;
}
