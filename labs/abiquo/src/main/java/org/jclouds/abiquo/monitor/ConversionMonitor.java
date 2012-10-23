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

package org.jclouds.abiquo.monitor;

import java.util.concurrent.TimeUnit;

import org.jclouds.abiquo.domain.cloud.Conversion;
import org.jclouds.abiquo.features.services.MonitoringService;
import org.jclouds.abiquo.monitor.internal.BaseConversionMonitor;

import com.google.inject.ImplementedBy;

/**
 * {@link Conversion} monitoring features.
 * 
 * @author Sergi Castro
 */
@ImplementedBy(BaseConversionMonitor.class)
public interface ConversionMonitor extends MonitoringService {
   /**
    * Monitor the given {@link Conversion}s and block until they finishes.
    * 
    * @param conversions
    *           The {@link Conversion}s to monitor.
    */
   void awaitCompletion(final Conversion... conversions);

   /**
    * Monitor the given {@link Conversion}s and populate an event when they
    * finish.
    * 
    * @param conversions
    *           The {@link Conversion}s to monitor.
    */
   public void monitor(final Conversion... conversions);

   /**
    * Monitor the given {@link Conversion}s and block until they finish.
    * 
    * @param maxWait
    *           The maximum time to wait.
    * @param timeUnit
    *           The time unit for the maxWait parameter.
    * @param conversions
    *           The {@link Conversion}s to monitor.
    */
   void awaitCompletion(final Long maxWait, final TimeUnit timeUnit, final Conversion... conversions);

   /**
    * Monitor the given {@link Conversion}s and populate an event when they
    * finish.
    * 
    * @param maxWait
    *           The maximum time to wait.
    * @param timeUnit
    *           The time unit for the maxWait parameter.
    * @param conversions
    *           The {@link Conversion}s to monitor.
    */
   public void monitor(final Long maxWait, final TimeUnit timeUnit, final Conversion... conversions);
}
