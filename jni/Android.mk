LOCAL_PATH := $(call my-dir)
 
include $(CLEAR_VARS)

LOCAL_LDLIBS    := -llog
LOCAL_MODULE    := i2c
LOCAL_SRC_FILES := i2cInterface.c
 
include $(BUILD_SHARED_LIBRARY)