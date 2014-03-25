/*
 ***************************************************************************
 * \brief   Embedded Android I2C Exercise 5.2
 *	        Basic i2c communication interface.
 *	        Only a minimal error handling is implemented.
 * \file    i2Interface.c
 * \version 1.0
 * \date    06.03.2014
 * \author  Martin Aebersold
 *
 * \remark  Last Modifications:
 * \remark  V1.0, AOM1, 06.03.2014
 ***************************************************************************
 */

#undef __cplusplus

#include <stdio.h>
#include <string.h>
#include <fcntl.h>
#include <stdlib.h>
#include <unistd.h>

#include <linux/i2c.h>
#include <memory.h>
#include <malloc.h>

#include <jni.h>

#include <android/log.h>

#include "bfh_ti_i2c_jni_template_I2C.h"

/* Define if we use the emulator */
#undef EMULATOR

#define JNIEXPORT __attribute__ ((visibility ("default")))

/* Define Log macros */
#define  LOG_TAG    "i2c"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

/************************************************************************************************************************************/
/* Open the i2c device
/************************************************************************************************************************************/

JNIEXPORT jint JNICALL Java_bfh_ti_i2c_1jni_1template_I2C_open(JNIEnv *env, jobject obj, jstring file)
  {
   #ifndef EMULATOR

   /* File descriptor to i2c-dev */
   int i2c_fd;

   char fileName[64];
   const jbyte *str;

   /* Get device file name */
   str = (*env)->GetStringUTFChars(env, file, NULL);
   if (str == NULL)
    {
     LOGE("Can't get file name!");
     return -1;
    }

   /* Get the device file name */
   sprintf(fileName, "%s", str);
   LOGI("Open i2c device node %s", fileName);
   (*env)->ReleaseStringUTFChars(env, file, str);

   i2c_fd = open(fileName, O_RDWR);
   return (jint) i2c_fd;
   #else
    return 0;
   #endif
  }

/************************************************************************************************************************************/
/* Set the i2c slave address																										*/
/************************************************************************************************************************************/

JNIEXPORT jint JNICALL Java_bfh_ti_i2c_1jni_1template_I2C_SetSlaveAddres(JNIEnv *env, jobject obj, jint file, jint slaveAddr)
  {
   #ifndef EMULATOR

   int res = 0;

   /* Set the I2C slave address for all subsequent I2C device transfers */
   res = ioctl(file, I2C_SLAVE, slaveAddr);
   if (res != 0)
    {
     LOGE("I2C: Can't set slave address!");
     return -1;
    }
   else
    {
	 /* Inform user about successful result */
	 LOGI("I2C: Set slave address %x", slaveAddr);
	 return 0;
    }
   #else
    return 0;
   #endif
  }


/************************************************************************************************************************************/
/* Read from the i2c device																											*/
/************************************************************************************************************************************/

JNIEXPORT jint JNICALL Java_bfh_ti_i2c_1jni_1template_I2C_read(JNIEnv * env, jobject obj, jint fileHander, jintArray bufArray, jint len)

  {
   #ifndef EMULATOR

    jint *bufInt;
    char *bufByte;
    char bytesRead = 0;
    int  i=0;

    /* Check for a valid array size */
    if (len <= 0)
     {
  	  LOGE("I2C: array size <= 0");
      return -1;
     }

    /* Allocate the necessary buffers */
    bufInt = (jint *) malloc(len * sizeof(int));
    if (bufInt == 0)
     {
      LOGE("I2C: Out of memory!");
      goto err0;
     }
    bufByte = (char*) malloc(len);
    if (bufByte == 0)
     {
      LOGE("I2C: Out of memory!");
      goto err1;
     }

    (*env)->GetIntArrayRegion(env, bufArray, 0, len, bufInt);

    /* Clear the i2c buffer */
    memset(bufByte, '\0', len);
    if ((bytesRead = read(fileHander, bufByte, len)) != len)
     {
      LOGE("I2C read failed!");
      goto err2;
     }
    else
     {
      /* Copy the i2c data elements to the Java array */
      for (i=0; i<bytesRead ; i++)
       bufInt[i] = bufByte[i];
      (*env)->SetIntArrayRegion(env, bufArray, 0, len, bufInt);
  }
  /* Cleanup and return number of bytes read */
  free(bufByte);
  free(bufInt);
  return bytesRead;

err2:
  free(bufByte);
err1:
  free(bufInt);
err0:
  return -1;

   #else
    return 0;
   #endif
  }

/************************************************************************************************************************************/
/* Write to the i2c device																											*/
/************************************************************************************************************************************/

JNIEXPORT jint JNICALL Java_bfh_ti_i2c_1jni_1template_I2C_write(JNIEnv *env, jobject obj, jint fileHander, jintArray inJNIArray, jint len)
 {
  #ifndef EMULATOR

  jbyte *bytePtr;
  jbyte i2cCommBuffer[255];
  jint  i, bytesWritten;

  /* Check for a valid array size */
  if ((len <= 0) || (len > 255))
   {
	LOGE("I2C: array size <= 0 | > 255");
    return -1;
   }

  /* Convert the incoming JNI jbyteArray to C's jbyte[] */
  jint *inCArray = (*env)->GetIntArrayElements(env, inJNIArray, NULL);

  /* Return on error */
  if (NULL == inCArray)
   return (jint) NULL;

  /* Get the array length */
  jsize length = (*env)->GetArrayLength(env, inJNIArray);

  /* Get the i2c data elements from the java array*/
  for (i=0; i<length; i++)
   {
	i2cCommBuffer[i] = (jbyte) inCArray[i];
   }

  /* Release resources */
  (*env)->ReleaseIntArrayElements(env, inJNIArray, inCArray, 0);

  /* Write data to the i2c device */
  bytesWritten = write(fileHander, i2cCommBuffer, len);

  /* Inform user if not all bytes are written */
  if (bytesWritten != len)
   {
    LOGE("Write to the i2c device failed! %d", bytesWritten);
    return -1;
   }
  return bytesWritten;

  #else
   return 0;
  #endif
 }

/************************************************************************************************************************************/
/* Close the i2c interface																										    */
/************************************************************************************************************************************/

JNIEXPORT void JNICALL Java_bfh_ti_i2c_1jni_1template_I2C_close(JNIEnv *env, jobject obj, jint fileHander)
 {
   #ifndef EMULATOR
    close(fileHander);
   #endif
 }
