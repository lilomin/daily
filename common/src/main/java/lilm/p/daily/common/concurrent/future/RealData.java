package lilm.p.daily.common.concurrent.future;

import java.util.concurrent.Callable;

/**
 * Created by lilm on 17-12-18.
 */
public class RealData implements Callable<String> {
	
	private String param;
	
	public RealData(String param) {
		this.param = param;
	}
	
	@Override
	public String call() throws Exception {
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (;;) {
			sb.append(i).append(param);
			if (++i > 9)
				break;
		}
		return sb.toString();
	}
}
