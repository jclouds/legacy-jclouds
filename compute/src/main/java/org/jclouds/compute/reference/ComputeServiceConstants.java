/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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
   public static final String PROPERTY_TIMEOUT_NODE_SUSPENDED = "jclouds.compute.timeout.node-suspended";
   public static final String PROPERTY_TIMEOUT_SCRIPT_COMPLETE = "jclouds.compute.timeout.script-complete";
   public static final String PROPERTY_TIMEOUT_PORT_OPEN = "jclouds.compute.timeout.port-open";
   /**
    * comma-separated nodes that we shouldn't attempt to list as they are dead in the provider for
    * some reason.
    */
   public static final String PROPERTY_BLACKLIST_NODES = "jclouds.compute.blacklist-nodes";

   /**
    * os to map of version input string to output string
    * <p/>
    * ex.
    * 
    * <pre>
    * {"centos":{"5.4":"5.4","5.5":"5.5"},"rhel":{"5.4":"5.4","5.5":"5.5"},"ubuntu":{"karmic":"9.10","lucid":"10.04","maverick":"10.10","natty":"11.04"},"windows":{"2008":"2008","Server 2008":"2008","2008 R2":"2008 R2","Server 2008 R2":"2008 R2","2008 SP2":"2008 SP2","Server 2008 SP2":"2008 SP2"}}
    * </pre>
    */
   public static final String PROPERTY_OS_VERSION_MAP_JSON = "jclouds.compute.os-version-map-json";

   @Singleton
   public static class ReferenceData {
      @Inject(optional = true)
      @Named(PROPERTY_OS_VERSION_MAP_JSON)
      public String osVersionMapJson = "{\"centos\":{\"\":\"\",\"5\":\"5.0\",\"5.2\":\"5.2\",\"5.3\":\"5.3\",\"5.4\":\"5.4\",\"5.5\":\"5.5\"},\"rhel\":{\"\":\"\",\"5\":\"5.0\",\"5.2\":\"5.2\",\"5.3\":\"5.3\",\"5.4\":\"5.4\",\"5.5\":\"5.5\"},\"solaris\":{\"\":\"\",\"10\":\"10\"},\"ubuntu\":{\"\":\"\",\"hardy\":\"8.04\",\"karmic\":\"9.10\",\"lucid\":\"10.04\",\"maverick\":\"10.10\",\"natty\":\"11.04\"},\"windows\":{\"\":\"\",\"2003\":\"2003\",\"2003 Standard\":\"2003\",\"2003 R2\":\"2003 R2\",\"2008\":\"2008\",\"2008 Web\":\"2008\",\"2008 Server\":\"2008\",\"Server 2008\":\"2008\",\"2008 R2\":\"2008 R2\",\"Server 2008 R2\":\"2008 R2\",\"2008 Server R2\":\"2008 R2\",\"2008 SP2\":\"2008 SP2\",\"Server 2008 SP2\":\"2008 SP2\"}}";
   }

   @Singleton
   public static class Timeouts {
      @Inject(optional = true)
      @Named(PROPERTY_TIMEOUT_NODE_TERMINATED)
      public long nodeTerminated = 30 * 1000;

      @Inject(optional = true)
      @Named(PROPERTY_TIMEOUT_NODE_RUNNING)
      public long nodeRunning = 1200 * 1000;

      @Inject(optional = true)
      @Named(PROPERTY_TIMEOUT_NODE_SUSPENDED)
      public long nodeSuspended = 30 * 1000;

      @Inject(optional = true)
      @Named(PROPERTY_TIMEOUT_SCRIPT_COMPLETE)
      public long scriptComplete = 600 * 1000;

      @Inject(optional = true)
      @Named(PROPERTY_TIMEOUT_PORT_OPEN)
      public long portOpen = 600 * 1000;

   }
}
