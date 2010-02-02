/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.util;

import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

import javax.inject.Named;

import com.google.inject.Binder;
import com.google.inject.Key;

/**
 * Utility methods for use with {@code @}{@link Named}.
 * 
 * @author crazybob@google.com (Bob Lee) - original code taken from
 *         {@code com.google.inject.name.Names}
 * 
 * @see com.google.inject.util.Jsr330#named
 * @author Adrian Cole
 */
public class Jsr330 {

   /**
    * @see com.google.inject.util.Jsr330#named
    */
   public static Named named(String name) {
      return com.google.inject.util.Jsr330.named(name);
   }

   /**
    * Creates a constant binding to {@code @Named(key)} for each entry in {@code properties}.
    */
   public static void bindProperties(Binder binder, Map<String, String> properties) {
      binder = binder.skipSources(Jsr330.class);
      for (Map.Entry<String, String> entry : properties.entrySet()) {
         String key = entry.getKey();
         String value = entry.getValue();
         binder.bind(Key.get(String.class, com.google.inject.util.Jsr330.named(key))).toInstance(
                  value);
      }
   }

   /**
    * Creates a constant binding to {@code @Named(key)} for each property. This method binds all
    * properties including those inherited from {@link Properties#defaults defaults}.
    */
   public static void bindProperties(Binder binder, Properties properties) {
      binder = binder.skipSources(Jsr330.class);

      // use enumeration to include the default properties
      for (Enumeration<?> e = properties.propertyNames(); e.hasMoreElements();) {
         String propertyName = (String) e.nextElement();
         String value = properties.getProperty(propertyName);
         binder.bind(Key.get(String.class, com.google.inject.util.Jsr330.named(propertyName)))
                  .toInstance(value);
      }
   }

}
