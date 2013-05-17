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
package org.jclouds.logging.jdk;

import java.util.logging.Level;

import org.jclouds.logging.BaseLogger;
import org.jclouds.logging.Logger;

/**
 * {@link java.util.logging.Logger} implementation of {@link Logger}.
 * 
 * @author Adrian Cole
 * 
 */
public class JDKLogger extends BaseLogger {
   private final java.util.logging.Logger logger;

   public static class JDKLoggerFactory implements LoggerFactory {
      public Logger getLogger(String category) {
         return new JDKLogger(java.util.logging.Logger.getLogger(category));
      }
   }

   public JDKLogger(java.util.logging.Logger logger) {
      this.logger = logger;
   }

   @Override
   protected void logTrace(String message) {
      logger.finest(message);
   }

   public boolean isTraceEnabled() {
      return logger.isLoggable(Level.FINEST);
   }

   @Override
   protected void logDebug(String message) {
      logger.fine(message);
   }

   public boolean isDebugEnabled() {
      return logger.isLoggable(Level.FINE);
   }

   @Override
   protected void logInfo(String message) {
      logger.info(message);
   }

   public boolean isInfoEnabled() {
      return logger.isLoggable(Level.INFO);
   }

   @Override
   protected void logWarn(String message) {
      logger.warning(message);
   }

   @Override
   protected void logWarn(String message, Throwable e) {
      logger.log(Level.WARNING, message, e);
   }

   public boolean isWarnEnabled() {
      return logger.isLoggable(Level.WARNING);
   }

   @Override
   protected void logError(String message) {
      logger.severe(message);
   }

   @Override
   protected void logError(String message, Throwable e) {
      logger.log(Level.SEVERE, message, e);
   }

   public boolean isErrorEnabled() {
      return logger.isLoggable(Level.SEVERE);
   }

   public String getCategory() {
      return logger.getName();
   }
}
