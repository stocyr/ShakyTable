LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_LDLIBS := -llog
LOCAL_MODULE := I2C
LOCAL_SRC_FILES := I2C.c

include $(BUILD_SHARED_LIBRARY)
