package bfh.ti.shakytable;

import android.graphics.Point;


public class I2CTempSensor {
	/* MCP9800 Register pointers */

	private static final char MCP9800_CONFIG = 0x20;/*0x01; 
													/*
													 * Sensor Configuration
													 * Register
													 */

	/* Sensor Configuration Register Bits */
	private static final char MCP9800_12_BIT = 0x67;//0x60;

	/* i2c Address of MCP9802 device */
	private static final char MCP9800_I2C_ADDR = 0x1c;//0x48;

	/* i2c device file name */
	private static final String MCP9800_FILE_NAME = "/dev/i2c-3";

	private I2C i2c;
	private int[] i2cCommBuffer = new int[16];
	private int fileHande;

	private Point acc;
	private int Temperature;

	public I2CTempSensor() {
		/* Instantiate the new i2c device */
		acc = new Point();
		
		i2c = new I2C();
		if (i2c != null) {
			/* Open the i2c device */
			fileHande = i2c.open(MCP9800_FILE_NAME);
			if (fileHande != 0) {

				/*
				 * Set the I2C slave address for all subsequent I2C device
				 * transfers
				 */
				i2c.SetSlaveAddres(fileHande, MCP9800_I2C_ADDR);

				/* Setup i2c buffer for the configuration register */
				i2cCommBuffer[0] = MCP9800_CONFIG;
				i2cCommBuffer[1] = MCP9800_12_BIT;
				i2c.write(fileHande, i2cCommBuffer, 2);

//				/* Setup mcp9800 register to read the temperature */
//				i2cCommBuffer[0] = MCP9800_TEMP;
//				i2c.write(fileHande, i2cCommBuffer, 1);
			}

		} else {

		}

	}

	public Point getTemp() {
		if (i2c != null) {
			i2cCommBuffer[0] = 0x29;
			i2c.write(fileHande, i2cCommBuffer, 1);
			/* Read the current temperature from the mcp9800 device */
			i2c.read(fileHande, i2cCommBuffer, 1);

			/* Assemble the temperature values */
			Temperature = (i2cCommBuffer[0] );
//			Temperature = Temperature >> 4;
//	
//			/* Convert current temperature to float */
//			TempC = 1.0 * Temperature * 0.0625;
			if(Temperature > 127){
				Temperature -= 255;
			}
			acc.x = Temperature;
			
			i2cCommBuffer[0] = 0x2B;
			i2c.write(fileHande, i2cCommBuffer, 1);
			/* Read the current temperature from the mcp9800 device */
			i2c.read(fileHande, i2cCommBuffer, 1);

			/* Assemble the temperature values */
			Temperature = (i2cCommBuffer[0] );
//			Temperature = Temperature >> 4;
//	
//			/* Convert current temperature to float */
//			TempC = 1.0 * Temperature * 0.0625;
			if(Temperature > 127){
				Temperature -= 255;
			}
			acc.y = Temperature;
		}

		return acc;

	}

	public void close() {
		/* Close the i2c file */
		i2c.close(fileHande);
	}
}