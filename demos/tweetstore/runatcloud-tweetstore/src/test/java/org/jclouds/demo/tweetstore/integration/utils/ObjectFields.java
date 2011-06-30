/*
 * @(#)ObjectFields.java     26 May 2011
 *
 * Copyright © 2010 Andrew Phillips.
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.demo.tweetstore.integration.utils;

import java.lang.reflect.Field;

public class ObjectFields {

    public static Object valueOf(String fieldName, Object source) {
        return valueOf(fieldName, source, source.getClass());
    }
    
    public static Object valueOf(String fieldName, Object source,
            Class<?> fieldDeclaringClass) {
        try {
            return getAccessibleField(fieldName, fieldDeclaringClass).get(source);
        } catch (Exception exception) {
            throw new IllegalArgumentException(exception);
        }
    }
    
    private static Field getAccessibleField(String name, Class<?> declaringClass) throws SecurityException, NoSuchFieldException {
        Field field = declaringClass.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }
    
    public static void set(String fieldName, Object target, Object value) {
        set(fieldName, target, value, target.getClass());
    }
    
    public static void set(String fieldName, Object target, Object value, 
            Class<?> fieldDeclaringClass) {
        try {
            getAccessibleField(fieldName, fieldDeclaringClass).set(target, value);
        } catch (Exception exception) {
            throw new IllegalArgumentException(exception);
        }
    }
}
