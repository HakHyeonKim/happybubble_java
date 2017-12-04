import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
//import gnu.io.CommPort;
//import gnu.io.CommPortIdentifier;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.plaf.synth.SynthSeparatorUI;

import jssc.SerialPort;
import jssc.SerialPortList;

public class jssctest {
	static String order;
	Thread write;
	static SerialPort serialPort = new SerialPort("COM7");

	public void jssc() throws Exception {

		String[] portNames = SerialPortList.getPortNames();
		for (int i = 0; i < portNames.length; i++) {
			System.out.println(portNames[i]);
		}
		serialPort.openPort();// Open serial port
		serialPort.setParams(SerialPort.BAUDRATE_9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
				SerialPort.PARITY_NONE);
		new ReadThread(serialPort).start();

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
	/*
	 * public void run() { try {
	 * 
	 * String arr[]; //배열 레퍼런스 변수 선언 // arr=Algoritm.result(); //메소드를 호출하고 메소드가 리턴한
	 * 배열을 전달받는다.
	 * 
	 * arr = dukmyung2.result(); System.out.println(arr.length); ArrayList<String>
	 * orderSplit = new ArrayList<String>(); // ArrayList<Integer> timeSplit = new
	 * ArrayList<Integer>(); for(int k=0;k<arr.length;k++){
	 * if(!arr[k].equals("End")) {
	 * orderSplit.add(arr[k].replaceAll("[^A-Za-z]","")); //
	 * timeSplit.add(Integer.parseInt((arr[k].replaceAll("[^0-9]","")))); }
	 * 
	 * }
	 * 
	 * System.out.println(orderSplit); // System.out.println(timeSplit);
	 * 
	 * String c = ""; int i = 0; System.out.println("\nLet's get it!!!!");
	 * if(c.equals("R")) { c = "D"; serial.writeString("Z"); try {
	 * Thread.sleep(1000); //serial.writeString("S"); }catch(InterruptedException e)
	 * { System.out.println("error"); }
	 * 
	 * serial.writeString(c); System.out.println(c); //System.out.println(i); }
	 * 
	 * 
	 * else if(c.equals("L")) { i = (i+5)/10;
	 * 
	 * c = "A"; serial.writeString("Z"); try { Thread.sleep(1000); //
	 * serial.writeString("S"); }catch(InterruptedException e) {
	 * System.out.println("error"); }
	 * 
	 * serial.writeString(c); System.out.println(c); //System.out.println(i);
	 * 
	 * }
	 * 
	 * else if(c.equals("W")){ serial.writeString("C"); try { Thread.sleep(1000);
	 * serial.writeString("S"); }catch(InterruptedException e) {
	 * System.out.println("error"); }
	 * 
	 * c = "W"; serial.writeString(c); System.out.println(c);
	 * 
	 * }
	 * 
	 * 
	 * /* else if(c.equals("Z") && i == 1) { c = "Z"; serial.writeString(c);
	 * System.out.println(c); System.out.println(i); try { Thread.sleep(1000);
	 * //serial.writeString("S"); } catch(InterruptedException e) {
	 * System.out.println("error"); } } else if(c.equals("Z") && i == 0) { c = "C";
	 * serial.writeString(c); System.out.println(c); System.out.println(i); try {
	 * Thread.sleep(1000); //serial.writeString("Z"); } catch(InterruptedException
	 * e) { System.out.println("error"); } }
	 * 
	 * 
	 * } catch (Exception e) { e.printStackTrace(); } }
	 */

	public void run() {
		try {

			String c = order;

			//System.out.println("\nKeyborad Input Read!!!!");
			serial.writeString(c);
			if (c.equals("D") || c.equals("A") || c.equals("X")) {
			/*	serial.writeString("Z");
				try {
					Thread.sleep(1000);
					// serial.writeString("S");
				} catch (InterruptedException e) {
					System.out.println("error");
				}
*/
				serial.writeString(c);
				//System.out.println(c);
			}

			else if (c.equals("w")) {
			/*	serial.writeString("Z");
				try {
					Thread.sleep(1000);
					serial.writeString("S");
				} catch (InterruptedException e) {
					System.out.println("error");
				}*/
				serial.writeString("W");
				//System.out.println(c);
			}

			else if (c.equals("W")) {
			/*	serial.writeString("C");
				try {
					Thread.sleep(1000);
					serial.writeString("S");
				} catch (InterruptedException e) {
					System.out.println("error");
				}*/
				serial.writeString(c);
				//System.out.println(c);
			}

			else if (c.equals("s")) {
				serial.writeString(c);
				//System.out.println(c);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
