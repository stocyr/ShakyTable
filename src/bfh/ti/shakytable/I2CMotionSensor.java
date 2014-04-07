package bfh.ti.shakytable;

import android.graphics.Point;


public class I2CMotionSensor {
	/* LIS302DL Register pointers */

	private static final char LIS302DL_CONFIG = 0x20;
													/*
													 * Sensor Configuration
													 * Register
													 */

	/* Sensor Configuration Register Bits */
	private static final char LIS302DL_12_BIT = 0x67;

	/* i2c Address of MCP9802 device */
	private static final char LIS302DL_I2C_ADDR = 0x1c;

	/* i2c device file name */
	private static final String LIS302DL_FILE_NAME = "/dev/i2c-3";

	private I2C i2c;
	private int[] i2cCommBuffer = new int[16];
	private int fileHande;

	private Point acc;
	private int Motion;

	public I2CMotionSensor() {
		/* Instantiate the new i2c device */
		acc = new Point();
		
		i2c = new I2C();
		if (i2c != null) {
			/* Open the i2c device */
			fileHande = i2c.open(LIS302DL_FILE_NAME);
			if (fileHande != 0) {

				/*
				 * Set the I2C slave address for all subsequent I2C device
				 * transfers
				 */
				i2c.SetSlaveAddres(fileHande, LIS302DL_I2C_ADDR);

				/* Setup i2c buffer for the configuration register */
				i2cCommBuffer[0] = LIS302DL_CONFIG;
				i2cCommBuffer[1] = LIS302DL_12_BIT;
				i2c.write(fileHande, i2cCommBuffer, 2);

			}

		} else {

		}

	}

	public Point getMotion() {
		if (i2c != null) {
			i2cCommBuffer[0] = 0x29;
			i2c.write(fileHande, i2cCommBuffer, 1);
			/* Read the current motion from the LIS302DL device */
			i2c.read(fileHande, i2cCommBuffer, 1);

			/* Assemble the motion values */
			Motion = (i2cCommBuffer[0] );
//			Motion >>= 4;
//	
//			/* Convert current motion to float */
			if(Motion > 127){
				Motion -= 255;
			}
			acc.x = Motion;
			
			i2cCommBuffer[0] = 0x2B;
			i2c.write(fileHande, i2cCommBuffer, 1);
			/* Read the current motion from the LIS302DL device */
			i2c.read(fileHande, i2cCommBuffer, 1);

			/* Assemble the motion values */
			Motion = (i2cCommBuffer[0] );
//			Motion = Motion >> 4;
//	
//			/* Convert current motion to float */
			if(Motion > 127){
				Motion -= 255;
			}
			acc.y = Motion;
		}

		return acc;

	}

	public void close() {
		/* Close the i2c file */
		i2c.close(fileHande);
	}
}