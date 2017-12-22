import java.io.File;
import java.net.Socket;
import java.util.Arrays;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.awt.image.BufferedImage;

public class SocketComm {

	public static final int port = 8765;
	public static int[][] imgToArr;
	public static int error = 0;

	public SocketComm() {
		ServerSocket serverSocket = null;
		java.net.Socket socket = null;

		try {
			System.out.println("수신 대기 중");
			serverSocket = new ServerSocket(port);
			socket = serverSocket.accept();

		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		while (true) {
			try {
				System.out.println("수신 대기 중");

				InputStream in = socket.getInputStream();

				byte[] sizeArr = new byte[4];
				in.read(sizeArr);
				int imgSize = getInt(sizeArr);
				byte[] arrImg = new byte[imgSize];
				in.read(arrImg);

				InputStream convertImgStream = new ByteArrayInputStream(arrImg);
				BufferedImage bImg = ImageIO.read(convertImgStream);
				bImg.flush();

				File outputfile = new File("binary_img.png");
				ImageIO.write(bImg, "png", outputfile);

				imgToArr = new int[bImg.getWidth()][bImg.getHeight()];
				for (int a = 0; a < imgToArr.length; a++) {
					Arrays.fill(imgToArr[a], 0);
				}
				for (int i = 0; i < bImg.getWidth(); i++) {
					for (int j = 0; j < bImg.getHeight(); j++) {
						if (bImg.getRGB(i, j) == -1) {
							imgToArr[i][j] = 9;
						} else {
							imgToArr[i][j] = 0;
						}
					}
				}
				System.out.println("이미지 수신 완료");
				byte[] arrPaperWidth = new byte[4];
				byte[] arrPaperHeight = new byte[4];
				in.read(arrPaperWidth);
				in.read(arrPaperHeight);
				int width = getInt(arrPaperWidth);
				int height = getInt(arrPaperHeight);

				System.out.println("Image Info");
				System.out.println("Width : " + width);
				System.out.println("Height : " + height);

				error = 0;

			} catch (Exception e) {
				try {
					OutputStream out = socket.getOutputStream();
					System.out.println("error 발생");
					error = 100;
					out.write(error);
					// bImg.flush();
					out.flush();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					// e1.printStackTrace();
				}
				// e.printStackTrace();

			}

			finally {

				try {
					System.out.println("socket close");
					if (error != 100)
						socket.close();
				} catch (Exception ignored) {
				}
				try {
					System.out.println("serversocket close");
					if (error != 100)
						serverSocket.close();
				} catch (Exception ignored) {
				}
			}
			if (error == 0) {
				break;
			}
		}
	}

	public static int getInt(byte[] data) {
		int s1 = data[0] & 0xFF;
		int s2 = data[1] & 0xFF;
		int s3 = data[2] & 0xFF;
		int s4 = data[3] & 0xFF;

		return ((s1 << 24) + (s2 << 16) + (s3 << 8) + (s4 << 0));
	}

	public static int[][] getImgToArr() {
		return imgToArr;
	}
}
