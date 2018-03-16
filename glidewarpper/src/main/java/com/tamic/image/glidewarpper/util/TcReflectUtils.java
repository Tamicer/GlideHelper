/*
 * Filename:    ReflectUtils.java
 * Description:
 * Copyright:   Tamic MIC Copyright(c)2016
 * @author:     Tamic
 * @version:    1.0
 * Create at:   May 22, 2013 1:29:06 PM
 *
 * Modification History:
 * Date         Author      Version     Description
 * ------------------------------------------------------------------
 *
 * Created by Tamic on 2016-09-01.
 */

package com.tamic.image.glidewarpper.util;

import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 反射工具类
 */
public final class TcReflectUtils {

    /**
     * DEBUG mode
     */
    protected static final boolean DEBUG = false;
    /**
     * Log TAG
     */
    private static final String LOG_TAG = "TcReflectUtils";

    /**
     * Constructor
     */
    private TcReflectUtils() {

    }

    /**
     * 获取指定对象的属性
     *
     * @param object    指定对象
     * @param fieldName 对象的属性名
     * @return 指定对象的属性
     */
    public static Field getField(Object object, String fieldName) {
        Class<?> theClass = object.getClass();
        try {
            Field field = theClass.getDeclaredField(fieldName);
            return field;
        } catch (NoSuchFieldException e) {
            // do nothing, catch for loop
            if (DEBUG) {
                Log.w(LOG_TAG, "NoSuchFieldException: " + theClass.getName() + ", " + e.getMessage(), e);
            }
        } catch (SecurityException e) {
            // rethrow
            throw new IllegalArgumentException(theClass.getName() + "." + fieldName, e);
        }

        return null;
    }

    /**
     * 调用指定对象的方法
     *
     * @param aMethod       指定的方法
     * @param aObject       指定的对象
     * @param aDefaultValue 默认返回值
     * @param aArgs         方法传递参数
     * @return 方法返回值
     */
    public static Object invoke(Method aMethod, Object aObject, Object aDefaultValue, Object... aArgs) {
        try {
            return aMethod.invoke(aObject, aArgs);
        } catch (Exception e) {
            if (DEBUG) {
                Log.w(LOG_TAG, "invoke Exception", e);
            }
        } catch (Error e) {
            if (DEBUG) {
                Log.w(LOG_TAG, "invoke Error", e);
            }
        }
        return aDefaultValue;
    }

    /**
     * 获取指定类对象的属性
     *
     * @param aClass      类对象
     * @param aFileldName 属性名
     * @return 属性
     */
    public static Field getField(Class<?> aClass, String aFileldName) {
        try {
            Field field = aClass.getDeclaredField(aFileldName);
            field.setAccessible(true);
            return field;
        } catch (Exception e) {
            if (DEBUG) {
                Log.w(LOG_TAG, "getField Exception", e);
            }
        } catch (Error e) {
            if (DEBUG) {
                Log.w(LOG_TAG, "getField Error", e);
            }
        }
        return null;
    }

    /**
     * 获取指定类对象的属性值
     *
     * @param aField        属性
     * @param aObject       类对象
     * @param aDefaultValue 默认值
     * @return 属性值
     */
    public static Object getFieldValue(Field aField, Object aObject, Object aDefaultValue) {
        try {
            return aField.get(aObject);
        } catch (Exception e) {
            if (DEBUG) {
                Log.w(LOG_TAG, "getFieldValue Exception", e);
            }
        } catch (Error e) {
            if (DEBUG) {
                Log.w(LOG_TAG, "getFieldValue Error", e);
            }
        }
        return aDefaultValue;
    }

    /**
     * 获取指定类对象的属性值
     *
     * @param aObject       类对象
     * @param aFieldName    属性名
     * @param aDefaultValue 默认值
     * @return 属性值
     */
    public static Object getFieldValue(Object aObject, String aFieldName, Object aDefaultValue) {
        Field field = getField(aObject.getClass(), aFieldName);
        if (field != null) {
            return getFieldValue(field, aObject, aDefaultValue);
        }
        return aDefaultValue;
    }

    /**
     * 设置指定类对象的属性值
     *
     * @param aField  类对象
     * @param aObject 属性名
     * @param aValue  属性值
     * @return 设置成功则返回true，否则返回false
     */
    public static boolean setFieldValue(Field aField, Object aObject, Object aValue) {
        try {
            aField.set(aObject, aValue);
            return true;
        } catch (Exception e) {
            if (DEBUG) {
                Log.w(LOG_TAG, "setFieldValue Exception", e);
            }
        } catch (Error e) {
            if (DEBUG) {
                Log.w(LOG_TAG, "setFieldValue Error", e);
            }
        }
        return false;
    }

    /**
     * 设置指定类对象的属性值
     *
     * @param aObject    类对象
     * @param aFieldName 属性名
     * @param aValue     属性值
     * @return 如果设置成功则返回true，否则返回false
     */
    public static boolean setFieldValue(Object aObject, String aFieldName, Object aValue) {
        Field field = getField(aObject.getClass(), aFieldName);
        if (field != null) {
            return setFieldValue(field, aObject, aValue);
        }
        return false;
    }

    /**
     * invoke with result
     *
     * @param aCls         class
     * @param aObj         object
     * @param aMethodName  method name
     * @param aParamTypes  param type
     * @param aArgs        argument
     * @param defaultValue default value
     * @return object
     */
    @SuppressWarnings( {"rawtypes", "unchecked"})
    public static Object invokeWithResult(Class aCls, Object aObj, String aMethodName, Class[] aParamTypes,
                                          Object[] aArgs, Object defaultValue) {
        try {
            Method method = aCls.getDeclaredMethod(aMethodName, aParamTypes);
            method.setAccessible(true);
            return method.invoke(aObj, aArgs);
        } catch (Throwable t) {
            Log.e(LOG_TAG, t.getMessage(), t);
        }

        return defaultValue;
    }

    /**
     * invoke with declared
     *
     * @param aCls        class
     * @param aObj        object
     * @param aMethodName method name
     * @param aParamTypes param type
     * @param aArgs       arg
     */
    @SuppressWarnings( {"rawtypes", "unchecked"})
    public static void invokeDeclared(Class aCls, Object aObj, String aMethodName, Class[] aParamTypes,
                                      Object[] aArgs) {
        try {
            Method method = aCls.getDeclaredMethod(aMethodName, aParamTypes);
            method.setAccessible(true);
            method.invoke(aObj, aArgs);
        } catch (Throwable t) {
            Log.e(LOG_TAG, t.getMessage(), t);
        }
    }
}
