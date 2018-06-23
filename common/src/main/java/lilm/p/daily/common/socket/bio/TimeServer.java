package lilm.p.daily.common.socket.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by lilm on 18-1-24.
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
		
		ServerSocket server = null;
		try {
			server = new ServerSocket(port);
			System.out.println("TimeServer is start in port:" + port);
			Socket socket;
			while (true) {
				socket = server.accept();
				if (socket == null) {
					continue;
				}
				new Thread(new TimeServerHandler(socket)).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (server != null) {
				System.out.println("TimeServer is close");
				try {
					server.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				server = null;
			}
		}
	}
	
}
