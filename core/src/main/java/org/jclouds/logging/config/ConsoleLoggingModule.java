/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.logging.config;

import org.jclouds.logging.Logger;

/**
 * Configures logging of type {@link ConsoleLogger}
 * 
 * @author Adrian Cole
 * 
 */
public class ConsoleLoggingModule extends LoggingModule {

   public Logger.LoggerFactory createLoggerFactory() {
      return new Logger.LoggerFactory() {
         public Logger getLogger(String category) {
            return Logger.CONSOLE;
         }
      };
   }

   /**
    * note that we override, as if we are not logging, there's no use in the overhead of listening
    * for loggers
    */
   @Override
   protected void configure() {
   }
}
