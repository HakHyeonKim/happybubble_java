import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.imageio.ImageIO;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class SocketTest {
	public static final int port = 8765;
	public static int[][] imgToArr = new int[200][200];
	Mat convertMat;

	public SocketTest() {
		ServerSocket serverSocket = null;
		Socket socket = null;
		try {
			serverSocket = new ServerSocket(port);
			System.out.println("수신 대기 중");
			socket = serverSocket.accept();
			InputStream in = socket.getInputStream();
			OutputStream out = socket.getOutputStream();
			byte[] sizeArr = new byte[4];
			in.read(sizeArr);
			int imgSize = getInt(sizeArr);
			System.out.println("이미지 사이즈 : " + imgSize);
			byte[] arrImg = new byte[imgSize];
			in.read(arrImg);
			InputStream convertImgStream = new ByteArrayInputStream(arrImg);
			BufferedImage bImg = ImageIO.read(convertImgStream);
			File outputfile = new File("test.png");
			ImageIO.write(bImg, "png", outputfile);
			
			for(int i = 0;i < bImg.getWidth();i++) {
				for(int j = 0;j < bImg.getHeight();j++) {
					if (bImg.getRGB(i, j) == -1) {
						imgToArr[i][j] = 9;
						System.out.print("1 ");
					}
					else {
						imgToArr[i][j] = 0;
						System.out.print("0 ");
					}
				}
				System.out.println("");
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
		} catch(Exception e) {
			e.printStackTrace();
		}
	    finally {
	    	try {
	    		socket.close();
    		} catch(Exception ignored) { }
    		try {
    			serverSocket.close();
			} catch(Exception ignored) { }
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
