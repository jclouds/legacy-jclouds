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
 * JClouds log abstraction layer.
 * <p/>
 * Implementations of logging are optional and injected if they are configured.
 * <p/>
 * <code> @Resource Logger logger = Logger.NULL;</code> The above will get you a null-safe instance
 * of <tt>Logger</tt>. If configured, this logger will be swapped with a real Logger implementation
 * with category set to the current class name. This is done post-object construction, so do not
 * attempt to use these loggers in your constructor.
 * <p/>
 * If you wish to initialize loggers like these yourself, do not use the @Resource annotation.
 * <p/>
 * This implementation first checks to see if the level is enabled before issuing the log command.
 * In other words, don't do the following
 * <code>if (logger.isTraceEnabled()) logger.trace("message");.
 * <p/>
 * 
 * @author Adrian Cole
 */
public interface Logger {

   /**
    * Assign to member to avoid NPE when no logging module is configured.
    */
   public static final Logger NULL = new NullLogger();

   /**
    * Assign to member to avoid NPE when no logging module is configured.
    */
   public static final Logger CONSOLE = new ConsoleLogger();

   String getCategory();

   void trace(String message, Object... args);

   boolean isTraceEnabled();

   void debug(String message, Object... args);

   boolean isDebugEnabled();

   void info(String message, Object... args);

   boolean isInfoEnabled();

   void warn(String message, Object... args);

   void warn(Throwable throwable, String message, Object... args);

   boolean isWarnEnabled();

   void error(String message, Object... args);

   void error(Throwable throwable, String message, Object... args);

   boolean isErrorEnabled();

   /**
    * Produces instances of {@link Logger} relevant to the specified category
    * 
    * @author Adrian Cole
    * 
    */
   public static interface LoggerFactory {
      Logger getLogger(String category);
   }
}
