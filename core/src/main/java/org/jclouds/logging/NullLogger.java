/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.logging;

import com.google.common.base.Throwables;

/**
 * <tt>Logger</tt> that doesn't do anything.
 * <p />
 * Useful to get baseline performance unaffected by logging.
 * 
 * @author Adrian Cole
 * 
 */
public class NullLogger implements Logger {

   public void debug(String message, Object... args) {

   }

   public void error(String message, Object... args) {
      System.err.printf(message + "%n", args);
   }

   public void error(Throwable throwable, String message, Object... args) {
      System.err.printf(message, args);
      System.err.printf("%n%s", Throwables.getStackTraceAsString(throwable));
   }

   public String getCategory() {

      return null;
   }

   public void info(String message, Object... args) {

   }

   public boolean isDebugEnabled() {

      return false;
   }

   public boolean isErrorEnabled() {

      return true;
   }

   public boolean isInfoEnabled() {

      return false;
   }

   public boolean isTraceEnabled() {

      return false;
   }

   public boolean isWarnEnabled() {

      return false;
   }

   public void trace(String message, Object... args) {

   }

   public void warn(String message, Object... args) {

   }

   public void warn(Throwable throwable, String message, Object... args) {

   }
}
