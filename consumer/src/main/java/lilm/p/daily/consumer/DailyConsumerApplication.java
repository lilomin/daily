package lilm.p.daily.consumer;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@EnableDubboConfiguration
@ImportResource("classpath:spring-dubbo.xml")
public class DailyConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(DailyConsumerApplication.class, args);
	}
}
