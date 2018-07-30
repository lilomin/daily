package lilm.p.daily.consumer.manager;

import com.alibaba.dubbo.config.annotation.Reference;
import lilm.p.daily.common.dubbo.provider.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by lilm on 18-6-23.
 */
@Component
public class HelloConsumer {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(HelloConsumer.class);
	
	@Autowired
	private HelloService helloService;
	
	public String invokeEchoHello() {
		String val = helloService.echoHello(this.getClass().getName());
		LOGGER.info("result:{}", val);
		return val;
	}
	
}
