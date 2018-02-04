package lilm.p.daily.socket.nio;

/**
 * Created by lilm on 18-1-27.
 */
public class TimeServer {
	
	public static void main(String[] args) {
		int port = 8000;
		if (args != null && args.length > 0) {
			try {
				port = Integer.valueOf(args[0]);
			} catch (NumberFormatException e) {
				// 采用默认
			}
		}
		MultiTimeService multiTimeService = new MultiTimeService(port);
		
		new Thread(multiTimeService, "NIO-MultiTimeServer").start();
	}
}
