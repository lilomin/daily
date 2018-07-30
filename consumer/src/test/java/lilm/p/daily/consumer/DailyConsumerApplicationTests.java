package lilm.p.daily.consumer;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import lilm.p.daily.consumer.manager.HelloConsumer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DailyConsumerApplicationTests {
	
	@Autowired
	HelloConsumer helloConsumer;

	@Test
	public void contextLoads() {
		helloConsumer.invokeEchoHello();
	}

}
