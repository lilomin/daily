package lilm.p.daily.consumer.controller;

import lilm.p.daily.consumer.manager.HelloConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by lilm on 18-7-20.
 */
@RestController("/api")
public class HelloController {
	
	@Autowired
	HelloConsumer helloConsumer;
	
	@GetMapping("/hello")
	public String hello() {
		return helloConsumer.invokeEchoHello();
	}
	
}
