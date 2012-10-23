/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.events.handlers;

import javax.annotation.Resource;

import org.jclouds.abiquo.events.monitor.MonitorEvent;
import org.jclouds.logging.Logger;

/**
 * Base class for all {@link MonitorEvent} handlers.
 * 
 * @author Ignasi Barrera
 */
public abstract class AbstractEventHandler<T> {
   @Resource
   protected Logger logger = Logger.NULL;

   /**
    * Checks if the current handler must handle the dispatched event.
    * 
    * @param event
    *           The event being dispatched.
    * @return Boolean indicating if the event must be handled by the current
    *         handler.
    */
   protected abstract boolean handles(MonitorEvent<T> event);

   // Public getters and setters to allow non-guice code to set the appropriate
   // logger

   public Logger getLogger() {
      return logger;
   }

   public void setLogger(final Logger logger) {
      this.logger = logger;
   }

}
