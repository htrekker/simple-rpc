import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

public class CompeltableFuture {
	@Test
	public void test1() {
		CompletableFuture f = CompletableFuture.supplyAsync(new Supplier<Object>() {
			@Override
			public Object get() {
				try {
					throw new Exception("throwed exception");
				} catch (Exception e) {
					e.printStackTrace();
				}
				return "hello";
			}
		});

		f.exceptionally(new Function() {
			@Override
			public Object apply(Object o) {
				System.out.println(o.toString());
				return null;
			}
		});
	}
}
