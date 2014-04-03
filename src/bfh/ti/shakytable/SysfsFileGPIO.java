/*
 ***************************************************************************
 * \brief   Embedded-Android (BTE5484)
 *          Accessing the GPIOs via sysfs with file i/o commands
 * \file    SysfsFileGpio.java
 * \version 1.0
 * \date    24.01.2014
 * \author  Martin Aebersold
 *
 * \remark  Last Modifications:
 * \remark  V1.1, STOLC2, 18.03.2014   changed read/write datatypes to int
 * \remark  V1.2, STOLC2, 18.03.2014   modified so that each instance means
 *                                     one independent GPIO.
 ***************************************************************************
 */
package bfh.ti.shakytable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SysfsFileGPIO {

	public String gpio;
	public char value;
	/*
	 * Define LEDs and Buttons
	 */
	static final String LED_L1 = "61";
	static final String LED_L2 = "44";
	static final String LED_L3 = "68";
	static final String LED_L4 = "67";

	static final String BUTTON_T1 = "49";
	static final String BUTTON_T2 = "112";
	static final String BUTTON_T3 = "51";
	static final String BUTTON_T4 = "7";

	/* Define some useful constants */
	private static final String GPIO_OUT = "out";
	private static final String GPIO_IN = "in";

	private static final String SysFsGpio = "/sys/class/gpio/gpio";
	private static final String SysFsDirection = "/direction";
	private static final String SysFsValue = "/value";
	private static final String SysFsExport = "/sys/class/gpio/unexport";
	private static final String SysFsUnexport = "/sys/class/gpio/unexport";

	SysfsFileGPIO(String gpio) {
		this.gpio = gpio;
		// export(); // not needed anymore - they're exported at startup
	}

	/*
	 * Export a gpio
	 */
	public boolean export() {
		try {
			/*
			 * Open file handles to GPIO port unexport and export controls
			 */
			FileWriter unexportFile = new FileWriter(SysFsUnexport);
			FileWriter exportFile = new FileWriter(SysFsExport);

			/*
			 * Clear the port, if needed
			 */
			File exportFileCheck = new File(SysFsGpio + gpio);

			if (exportFileCheck.exists()) {
				unexportFile.write(gpio);
				unexportFile.flush();
				unexportFile.close();
			}

			/*
			 * Set the port for use
			 */
			exportFile.write(gpio);
			exportFile.flush();
			exportFile.close();
			return true;
		} catch (Exception exception) {
			exception.printStackTrace();
			return false;
		}
	}

	/*
	 * Unexport a gpio
	 */
	public boolean unexport() {
		try {
			/*
			 * Open file handles to GPIO port unexport controls
			 */

			FileWriter unexportFile = new FileWriter(SysFsUnexport);
			unexportFile.write(gpio);
			unexportFile.flush();
			unexportFile.close();
			return true;
		} catch (Exception exception) {
			exception.printStackTrace();
			return false;
		}
	}

	/*
	 * Set gpio direction to output
	 */
	public boolean set_direction_out() {
		try {
			/*
			 * Open file handle to port input/output control
			 */
			FileWriter directionFile = new FileWriter(SysFsGpio + gpio
					+ SysFsDirection);

			/*
			 * Set port for output
			 */
			directionFile.write(GPIO_OUT);
			directionFile.flush();
			directionFile.close();
			return true;
		} catch (Exception exception) {
			exception.printStackTrace();
			return false;
		}
	}

	/*
	 * Set gpio direction to input
	 */
	public boolean set_direction_in() {
		try {
			/*
			 * Open file handle to port input/output control
			 */
			FileWriter directionFile = new FileWriter(SysFsGpio + gpio
					+ SysFsDirection);

			/*
			 * Set port for output
			 */
			directionFile.write(GPIO_IN);
			directionFile.flush();
			directionFile.close();
			return true;
		} catch (Exception exception) {
			exception.printStackTrace();
			return false;
		}
	}

	/*
	 * Write a gpio value (input datatype: integer!)
	 */
	public boolean write_value(int value) {
		try {
			/*
			 * Set up File I/O and write to the GPIO
			 */
			// THIS IS CLOSE TO A MEMORY LEAK! TRIGGERS GC EVERY 250ms
			FileWriter gpioNumber = new FileWriter(SysFsGpio + gpio
					+ SysFsValue);

			gpioNumber.write("" + value);
			gpioNumber.flush();
			gpioNumber.close();
			return true;
		} catch (Exception exception) {
			exception.printStackTrace();
			return false;
		}
	}

	/*
	 * Read a gpio value (output datatype: integer!)
	 */
	public int read_value() {
		/*
		 * Set up File I/O and read from the GPIO
		 */
		String value;
		try {
			// THIS IS CLOSE TO A MEMORY LEAK! TRIGGERS GC EVERY 50ms
			BufferedReader fileReader = new BufferedReader(new FileReader(
					SysFsGpio + gpio + SysFsValue));
			value = fileReader.readLine();
			fileReader.close();
		} catch (IOException ex) {
			return -1;
		}
		return Integer.valueOf(value);
	}
}
