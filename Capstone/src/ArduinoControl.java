import jssc.SerialPort;
import jssc.SerialPortList;

public class ArduinoControl {
	static String order;
	Thread write;
	static SerialPort serial_port = new SerialPort("COM3");

	public void jssc() throws Exception {

		String[] portNames = SerialPortList.getPortNames();
		
		for (int i = 0; i < portNames.length; i++) {
			System.out.println(portNames[i]);
		}
		serial_port.openPort();// Open serial port
		serial_port.setParams(SerialPort.BAUDRATE_9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
		new ReadThread(serial_port).start();
	}

	public void getOrder(String order) {
		write = new WriteThread(serialPort, order);
		write.start();
	}
}

class ReadThread extends Thread {
	SerialPort serial;

	ReadThread(SerialPort serial) {
		this.serial = serial;
	}

	public void run() {
		try {
			while (true) {
				byte[] read = serial.readBytes();
				if (read != null && read.length > 0)
					System.out.println(new String(read));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class WriteThread extends Thread {
	SerialPort serial;
	String order;

	WriteThread(SerialPort serial, String order) {
		this.serial = serial;
		this.order = order;
	}

	public void run() {
		try {
			String c = order;
			serial.writeString(c);

			if (c.equals("D") || c.equals("A") || c.equals("X")) {
				serial.writeString(c);
			}
			else if (c.equals("w")) {
				serial.writeString("W");
			}
			else if (c.equals("W")) {
				serial.writeString(c);
			}
			else if (c.equals("s")) {
				serial.writeString(c);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}