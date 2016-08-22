package android_serialport_api;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;

import android.content.SharedPreferences;

public class Application extends android.app.Application {

	public SerialPortFinder mSerialPortFinder = new SerialPortFinder();
	private SerialPort mSerialPort = null;

	public SerialPort getSerialPort() throws SecurityException, IOException, InvalidParameterException {
//		mSerialPort = new SerialPort(new File("/dev/ttySAC2"), Integer.decode("115200"),0);
		if (mSerialPort == null) {
			/* Read serial port parameters */
			SharedPreferences sp = getSharedPreferences("com.example.administrator.zigbeeboxgatway_preferences", MODE_PRIVATE);
			String path = sp.getString("DEVICE", "");
			int baudrate = Integer.decode(sp.getString("BAUDRATE", "-1"));
//			Log.d("GTA", "path:" + path);

			/* Check parameters */
			if ((path.length() == 0) || (baudrate == -1)) {
				System.out.println("串口为空");
				throw new InvalidParameterException();
			}

			/* Open the serial port */
			mSerialPort = new SerialPort(new File(/*"/dev/" + */path), baudrate/*Integer.decode("115200")*/,0);

		}
		return mSerialPort;
	}

	public void closeSerialPort() {
		if (mSerialPort != null) {
			mSerialPort.close();
			mSerialPort = null;
		}
	}
}
