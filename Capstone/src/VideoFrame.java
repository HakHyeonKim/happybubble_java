import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import javax.swing.JFrame;
import org.opencv.core.Mat;

public class VideoFrame {
	private final JFrame frame;
	private final VideoPanel panel;
	
	public VideoFrame() {
		frame = new JFrame();
		frame.getContentPane().setLayout(new FlowLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		panel = new VideoPanel();
		frame.getContentPane().add(panel);
	}
	
	public void setVisible(boolean visible) {
		frame.setVisible(visible);
	}
	
	public void render(Mat video) {
		Image i = toBufferedImage(video);
		panel.setImage(i);
        panel.repaint();
        frame.pack();
	}

	private Image toBufferedImage(Mat m) {
		int type = BufferedImage.TYPE_BYTE_GRAY;
		if(m.channels() > 1)	type = BufferedImage.TYPE_3BYTE_BGR; 
		
		int bufferSize = m.channels() * m.cols() * m.rows();
		byte[] b = new byte[bufferSize];
		m.get(0, 0, b);
		BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();		
		
		
		
		System.arraycopy(b, 0, targetPixels, 0, b.length);
		return image;
	}
}
