package pasilo.rpc.core.transport.util;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class PendingResults {

	public static final Map<Integer, CompletableFuture<Object>> PENDING_RESULTS = new ConcurrentHashMap<>();

	public static void poolResult(Integer requestId, CompletableFuture pendingResult) {
		PENDING_RESULTS.put(requestId, pendingResult);
	}

	public static void complete(Integer requestId, Object response) {
		CompletableFuture<Object> pending = PENDING_RESULTS.get(requestId);
		if (pending != null) {
			pending.complete(response);
		} else {
			throw new IllegalStateException();
		}
	}

	public static void completeExceptionally(Integer requestId, Throwable cause) {
		CompletableFuture<Object> pending = PENDING_RESULTS.get(requestId);
		if (pending != null) {
			pending.completeExceptionally(cause);
		} else {
			throw new IllegalStateException();
		}
	}

}
