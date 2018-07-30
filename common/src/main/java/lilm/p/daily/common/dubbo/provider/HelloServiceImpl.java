package lilm.p.daily.common.dubbo.provider;

import org.springframework.stereotype.Component;

/**
 * Created by lilm on 18-6-23.
 */
@Component
public class HelloServiceImpl implements HelloService {
	
	@Override
	public String echoHello(String param) {
		return "Hello Dubbo";
	}
}
