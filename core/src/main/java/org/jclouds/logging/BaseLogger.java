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

/**
 * Base implementation that constructs formatted log strings.
 * 
 * @author Adrian Cole
 * 
 */
public abstract class BaseLogger implements Logger {

   protected abstract void logError(String message, Throwable e);

   protected abstract void logError(String message);

   protected abstract void logWarn(String message, Throwable e);

   protected abstract void logWarn(String message);

   protected abstract void logInfo(String message);

   protected abstract void logDebug(String message);

   protected abstract void logTrace(String message);

   public void trace(String message, Object... args) {
      if (isTraceEnabled())
         logTrace(formatIfArgs(message, args));
   }

   private String formatIfArgs(String message, Object... args) {
      return args.length == 0 ? message : String.format(message, args);
   }

   public void debug(String message, Object... args) {
      if (isDebugEnabled())
         logDebug(formatIfArgs(message, args));
   }

   public void info(String message, Object... args) {
      if (isInfoEnabled())
         logInfo(formatIfArgs(message, args));
   }

   public void warn(String message, Object... args) {
      if (isWarnEnabled())
         logWarn(formatIfArgs(message, args));
   }

   public void warn(Throwable e, String message, Object... args) {
      if (isWarnEnabled())
         logWarn(formatIfArgs(message, args), e);
   }

   public void error(String message, Object... args) {
      if (isErrorEnabled())
         logError(formatIfArgs(message, args));
   }

   public void error(Throwable e, String message, Object... args) {
      if (isErrorEnabled())
         logError(formatIfArgs(message, args), e);
   }

}
