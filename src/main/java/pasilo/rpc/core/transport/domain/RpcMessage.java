package pasilo.rpc.core.transport.domain;

import lombok.*;

import java.io.Serializable;


/**
 *  描述自定义协议的数据结构：
 *    0        1        2        3        4        5        6        7        8         9
 *    +--------+--------+--------+--------+--------+--------+--------+--------+
 *    | magic  |version |         length                    | type   | codec  |
 *    +--------+--------+--------+--------+--------+--------+----- --+--------+
 *    |            Request Id             |                                   |
 *    +--------+--------+--------+--------+                                   +
 *    |                                body                                   |
 *    |                                                                       |
 *    +-----------------------------------------------------------------------+
 *    1B  magic code（魔法数）      1B version（版本）       4B body length（消息长度）
 *    1B messageType（消息类型）    4B compress（压缩类型）  4B  requestId（请求的Id）
 *    body（object类型数据）
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
public class RpcMessage implements Serializable {
	private byte msgType;
	private byte codec;
	private int requestId = -1;
	private Object data;
}
