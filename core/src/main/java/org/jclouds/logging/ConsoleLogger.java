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
 * <tt>Logger</tt> that logs to the console
 * <p />
 * Useful to get baseline performance unaffected by logging.
 * 
 * @author Adrian Cole
 * 
 */
public class ConsoleLogger implements Logger {

   /**
    * {@inheritDoc}
    */
   @Override
   public void debug(String message, Object... args) {

   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void error(String message, Object... args) {
      System.err.printf(String.format("ERROR: %s%n", message), args);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void error(Throwable throwable, String message, Object... args) {
      System.err.printf(String.format("ERROR: %s%n%s", message, Throwables
               .getStackTraceAsString(throwable)), args);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getCategory() {
      return null;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void info(String message, Object... args) {
      System.err.printf(String.format("INFO: %s%n", message), args);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean isDebugEnabled() {
      return false;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean isErrorEnabled() {

      return true;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean isInfoEnabled() {

      return true;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean isTraceEnabled() {

      return false;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean isWarnEnabled() {

      return true;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void trace(String message, Object... args) {

   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void warn(String message, Object... args) {
      System.err.printf(String.format("WARN: %s%n", message), args);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void warn(Throwable throwable, String message, Object... args) {
      System.err.printf(String.format("WARN: %s%n%s", message, Throwables
               .getStackTraceAsString(throwable)), args);
   }

}
