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
package org.jclouds.logging.slf4j;

import org.jclouds.logging.BaseLogger;
import org.jclouds.logging.Logger;

/**
 * {@link org.slf4j.LoggerFactory} implementation of {@link Logger}.
 * 
 * @author Adrian Cole
 * 
 */
public class SLF4JLogger extends BaseLogger {
   static {
      // force initialization to avoid http://www.slf4j.org/codes.html#substituteLogger messages
      org.slf4j.LoggerFactory.getILoggerFactory();
   }
   
   private final org.slf4j.Logger logger;
   private final String category;

   public static class SLF4JLoggerFactory implements LoggerFactory {
      public Logger getLogger(String category) {
         return new SLF4JLogger(category, org.slf4j.LoggerFactory.getLogger(category));
      }
   }

   public SLF4JLogger(String category, org.slf4j.Logger logger) {
      this.category = category;
      this.logger = logger;
   }

   @Override
   protected void logTrace(String message) {
      logger.trace(message);
   }

   public boolean isTraceEnabled() {
      return logger.isTraceEnabled();
   }

   @Override
   protected void logDebug(String message) {
      logger.debug(message);
   }

   public boolean isDebugEnabled() {
      return logger.isDebugEnabled();
   }

   @Override
   protected void logInfo(String message) {
      logger.info(message);
   }

   public boolean isInfoEnabled() {
      return logger.isInfoEnabled();
   }

   @Override
   protected void logWarn(String message) {
      logger.warn(message);
   }

   @Override
   protected void logWarn(String message, Throwable e) {
      logger.warn(message, e);
   }

   public boolean isWarnEnabled() {
      return logger.isWarnEnabled();
   }

   @Override
   protected void logError(String message) {
      logger.error(message);
   }

   @Override
   protected void logError(String message, Throwable e) {
      logger.error(message, e);
   }

   public boolean isErrorEnabled() {
      return logger.isErrorEnabled();
   }

   public String getCategory() {
      return category;
   }
}
