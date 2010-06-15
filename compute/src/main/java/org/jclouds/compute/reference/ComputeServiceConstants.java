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
package org.jclouds.compute.reference;

import javax.inject.Named;
import javax.inject.Singleton;

import com.google.inject.Inject;

/**
 * 
 * @author Adrian Cole
 */
public interface ComputeServiceConstants {

   public static final String COMPUTE_LOGGER = "jclouds.compute";
   public static final String LOCAL_PARTITION_GB_PATTERN = "disk_drive/%s/gb";
   public static final String PROPERTY_TIMEOUT_NODE_TERMINATED = "jclouds.compute.timeout.node-terminated";
   public static final String PROPERTY_TIMEOUT_NODE_RUNNING = "jclouds.compute.timeout.node-running";
   public static final String PROPERTY_TIMEOUT_SCRIPT_COMPLETE = "jclouds.compute.timeout.script-complete";
   public static final String PROPERTY_TIMEOUT_PORT_OPEN = "jclouds.compute.timeout.port-open";

   @Singleton
   static class Timeouts {
      @Inject(optional = true)
      @Named(PROPERTY_TIMEOUT_NODE_TERMINATED)
      public long nodeTerminated = 600 * 1000;

      @Inject(optional = true)
      @Named(PROPERTY_TIMEOUT_NODE_RUNNING)
      public long nodeRunning = 600 * 1000;

      @Inject(optional = true)
      @Named(PROPERTY_TIMEOUT_SCRIPT_COMPLETE)
      public long scriptComplete = 600 * 1000;

      @Inject(optional = true)
      @Named(PROPERTY_TIMEOUT_PORT_OPEN)
      public long portOpen = 130 * 1000;

   }
}
