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
package org.jclouds.compute.reference;
import static org.jclouds.compute.config.ComputeServiceProperties.INIT_STATUS_INITIAL_PERIOD;
import static org.jclouds.compute.config.ComputeServiceProperties.INIT_STATUS_MAX_PERIOD;
import static org.jclouds.compute.config.ComputeServiceProperties.OS_VERSION_MAP_JSON;
import static org.jclouds.compute.config.ComputeServiceProperties.POLL_INITIAL_PERIOD;
import static org.jclouds.compute.config.ComputeServiceProperties.POLL_MAX_PERIOD;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_IMAGE_AVAILABLE;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_IMAGE_DELETED;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_RUNNING;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_SUSPENDED;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_TERMINATED;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_PORT_OPEN;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_SCRIPT_COMPLETE;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.config.ComputeServiceProperties;
import org.jclouds.util.Predicates2;

import com.google.common.base.Supplier;
import com.google.inject.Inject;

/**
 * 
 * @author Adrian Cole
 */
public interface ComputeServiceConstants {

   public static final String COMPUTE_LOGGER = "jclouds.compute";
   public static final String LOCAL_PARTITION_GB_PATTERN = "disk_drive/%s/gb";

   @Singleton
   public static class NamingConvention {

      @Inject(optional = true)
      public final Supplier<String> randomSuffix = new Supplier<String>() {
         final SecureRandom random = new SecureRandom();

         @Override
         public String get() {
            return random.nextInt(100) + "";
         }
      };
   }

   @Singleton
   public static class InitStatusProperties {
      @Inject(optional = true)
      @Named(INIT_STATUS_INITIAL_PERIOD)
      public long initStatusInitialPeriod = 500;

      @Inject(optional = true)
      @Named(INIT_STATUS_MAX_PERIOD)
      public long initStatusMaxPeriod = 5000;
   }

   @Singleton
   public static class PollPeriod {
      @Inject(optional = true)
      @Named(POLL_INITIAL_PERIOD)
      public long pollInitialPeriod = Predicates2.DEFAULT_PERIOD;

      @Inject(optional = true)
      @Named(POLL_MAX_PERIOD)
      public long pollMaxPeriod = Predicates2.DEFAULT_MAX_PERIOD;
   }

   @Singleton
   public static class ReferenceData {
      @Inject(optional = true)
      @Named(OS_VERSION_MAP_JSON)
      // TODO: switch this to read from resource, failing back to string
      // constant on classpath problem
      public String osVersionMapJson = "{\"suse\":{\"\":\"\",\"11\":\"11\",\"11 SP1\":\"11 SP1\"},\"debian\":{\"\":\"\",\"lenny\":\"5.0\",\"6\":\"6.0\",\"squeeze\":\"6.0\"},\"centos\":{\"\":\"\",\"5\":\"5.0\",\"5.2\":\"5.2\",\"5.3\":\"5.3\",\"5.4\":\"5.4\",\"5.5\":\"5.5\",\"5.6\":\"5.6\",\"5.7\":\"5.7\",\"6\":\"6.0\",\"6.0\":\"6.0\"},\"rhel\":{\"\":\"\",\"5\":\"5.0\",\"5.2\":\"5.2\",\"5.3\":\"5.3\",\"5.4\":\"5.4\",\"5.5\":\"5.5\",\"5.6\":\"5.6\",\"5.7\":\"5.7\",\"6\":\"6.0\",\"6.0\":\"6.0\"},\"solaris\":{\"\":\"\",\"10\":\"10\"},\"ubuntu\":{\"\":\"\",\"hardy\":\"8.04\",\"karmic\":\"9.10\",\"lucid\":\"10.04\",\"10.04.1\":\"10.04\",\"maverick\":\"10.10\",\"natty\":\"11.04\",\"oneiric\":\"11.10\",\"precise\":\"12.04\"},\"windows\":{\"\":\"\",\"7\":\"7\",\"2003\":\"2003\",\"2003 Standard\":\"2003\",\"2003 R2\":\"2003 R2\",\"2008\":\"2008\",\"2008 Web\":\"2008\",\"2008 Server\":\"2008\",\"Server 2008\":\"2008\",\"2008 R1\":\"2008 R1\",\"2008 R2\":\"2008 R2\",\"Server 2008 R2\":\"2008 R2\",\"2008 Server R2\":\"2008 R2\",\"2008 SP2\":\"2008 SP2\",\"Server 2008 SP2\":\"2008 SP2\"}}";
   }

   @Singleton
   public static class Timeouts {
      @Inject(optional = true)
      @Named(TIMEOUT_NODE_TERMINATED)
      public long nodeTerminated = 30 * 1000;

      @Inject(optional = true)
      @Named(TIMEOUT_NODE_RUNNING)
      public long nodeRunning = 1200 * 1000;

      @Inject(optional = true)
      @Named(TIMEOUT_NODE_SUSPENDED)
      public long nodeSuspended = 30 * 1000;

      @Inject(optional = true)
      @Named(TIMEOUT_SCRIPT_COMPLETE)
      public long scriptComplete = 600 * 1000;

      @Inject(optional = true)
      @Named(TIMEOUT_PORT_OPEN)
      public long portOpen = 600 * 1000;
      
      /**
       * current value of {@link ComputeServiceProperties#TIMEOUT_IMAGE_DELETED} defaults to 30
       * seconds.
       */
      @Inject(optional = true)
      @Named(TIMEOUT_IMAGE_DELETED)
      public long imageDeleted = TimeUnit.SECONDS.toMillis(30);

      /**
       * current value of {@link ComputeServiceProperties#TIMEOUT_IMAGE_AVAILABLE} defaults to 45
       * minutes.
       */
      @Inject(optional = true)
      @Named(TIMEOUT_IMAGE_AVAILABLE)
      public long imageAvailable = TimeUnit.MINUTES.toMillis(45);
   }
}
