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

package bfh.ti.i2c_jni_template;

/**
 * This is a I2C operation class
 */

public class I2C
 {
    /**
     * @param  deviceName
     *         device path name
     * @return return file hander else return <0 on fail
     */
    public native int open(String deviceName);
    
    /**
     * @param  fileHandle
     *         file handle
     *         i2c_adr
     *         I2C slave address of the device
     * @return return 0 if OK else return <0 on fail
     */
    public native int SetSlaveAddres(int fileHande, int i2c_adr);
    
    /**
     * @param  fileHande
     *         file handle
     * @param  buf
     * 		   Read buffer
     * @param  Length
     *         Number of bytes to read from the device
     * @return read number of bytes read
     */
    public native int read(int fileHande, int buf[], int Length);

    /**
     * @param  fileHande
     *         file handle
     * @param  buf
     * 		   Write buffer
     * @param  Length
     *         Number of bytes to write to the device
     * @return read number of bytes read
     */
    public native int write(int fileHande, int buf[], int Length);

    /**
     * @param  fileHande
     *         file handle
     * @return None
     */    
    public native void close(int fileHande);

    static
     {
      System.loadLibrary("i2c");
     }
 }