/*
 ***************************************************************************
 * \brief   Embedded Android I2C Exercise 5.2
 *	        Native basic i2c communication interface 
 *	        Only a minimal error handling is implemented.
 * \file    I2C.java
 * \version 1.0
 * \date    06.03.2014
 * \author  Martin Aebersold
 *
 * \remark  Last Modifications:
 * \remark  V1.0, AOM1, 06.03.2014
 ***************************************************************************
 */

package bfh.ti.shakytable;

/**
 * This is a I2C operation class
 */

public class I2C
 {

    public native int open(String deviceName);
    public native int SetSlaveAddres(int fileHande, int i2c_adr);
    public native int read(int fileHande, int buf[], int Length);
    public native int write(int fileHande, int buf[], int Length);  
    public native void close(int fileHande);

    static
     {
      System.loadLibrary("I2C");
     }
 }