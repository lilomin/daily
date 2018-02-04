package lilm.p.daily.socket.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

/**
 * Created by lilm on 18-1-24.
 */
public class TimeServerHandler implements Runnable {
	
	private Socket socket;
	
	public TimeServerHandler(Socket socket) {
		this.socket = socket;
	}
	
	@Override
	public void run() {
		BufferedReader in = null;
		PrintWriter out = null;
		
		try {
			in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			out = new PrintWriter(this.socket.getOutputStream());
			
			String currentTime;
			String body;
			while (true) {
				body = in.readLine();
				if (body == null)
					break;
				System.out.println("The TimeServer receive order:" + body);
				currentTime = new Date().toString();
				out.println(currentTime);
				out.flush();
				System.out.println("Send time end!");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (out != null) {
				out.close();
				out = null;
			}
			if (this.socket != null) {
				try {
					this.socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				this.socket = null;
			}
		}
	}
}
