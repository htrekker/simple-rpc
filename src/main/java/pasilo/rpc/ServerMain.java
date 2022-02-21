package pasilo.rpc;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@ComponentScan
public class ServerMain {
	public static void main(String[] args) {
		ApplicationContext context = new AnnotationConfigApplicationContext(ServerMain.class);
	}
}
