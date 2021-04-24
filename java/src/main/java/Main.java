import java.awt.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

	@SuppressWarnings("InfiniteLoopStatement")
	public static void main(String[] args) throws IOException {
		var converter = new ImageRGB565Converter(96, 64);
		var gifBeam = new GifBeam("pika_sized.gif", 96, 64, converter);
		var staticImageBeam = new StaticImageBeam("test2.png", 96, 64, converter);
		var screenBeam = new ScreenBeam(new Rectangle(360, 240), 96, 64, converter);

		ServerSocket serverSocket = new ServerSocket(9800);

		// In error case wait for next connection
		while(true) {
			System.out.println("Wait for connection");
			Socket clientSocket = serverSocket.accept();
			System.out.println("Connected");
			var outputStream = clientSocket.getOutputStream();
			try {
				// Loop the beaming
				while(true) {
					staticImageBeam.beam(outputStream);
					Thread.sleep(10000);
					gifBeam.beam(outputStream);
					Thread.sleep(1000);
					screenBeam.beam(outputStream);
					Thread.sleep(4000);
				}
			} catch(Exception e) {
				System.out.println("Disconnected cause of exception");
				e.printStackTrace();
			}
		}
	}
}