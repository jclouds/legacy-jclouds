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

import org.jclouds.abiquo.domain.cloud.VirtualAppliance;
import org.jclouds.abiquo.features.services.MonitoringService;
import org.jclouds.abiquo.monitor.internal.BaseVirtualApplianceMonitor;

import com.google.inject.ImplementedBy;

/**
 * {@link VirtualAppliance} monitoring features.
 * 
 * @author Ignasi Barrera
 */
@ImplementedBy(BaseVirtualApplianceMonitor.class)
public interface VirtualApplianceMonitor extends MonitoringService {
   /**
    * Monitor the given {@link VirtualAppliance}s and block until the deploy
    * finishes.
    * 
    * @param vapp
    *           The {@link VirtualAppliance}s to monitor.
    */
   void awaitCompletionDeploy(final VirtualAppliance... vapp);

   /**
    * Monitor the given {@link VirtualAppliance}s and populate an event when the
    * deploy finishes.
    * 
    * @param VirtualAppliance
    *           The {@link VirtualAppliance}s to monitor.
    */
   public void monitorDeploy(final VirtualAppliance... vapps);

   /**
    * Monitor the given {@link VirtualAppliance}s and block until the deploy
    * finishes.
    * 
    * @param maxWait
    *           The maximum time to wait.
    * @param timeUnit
    *           The time unit for the maxWait parameter.
    * @param vapp
    *           The {@link VirtualAppliance}s to monitor.
    */
   void awaitCompletionDeploy(final Long maxWait, final TimeUnit timeUnit, final VirtualAppliance... vapp);

   /**
    * Monitor the given {@link VirtualAppliance}s and populate an event when
    * deploy finishes.
    * 
    * @param maxWait
    *           The maximum time to wait.
    * @param timeUnit
    *           The time unit for the maxWait parameter.
    * @param vapps
    *           The {@link VirtualAppliance}s to monitor.
    */
   public void monitorDeploy(final Long maxWait, final TimeUnit timeUnit, final VirtualAppliance... vapps);

   /**
    * Monitor the given {@link VirtualAppliance}s and block until the undeploy
    * finishes.
    * 
    * @param vapp
    *           The {@link VirtualAppliance}s to monitor.
    */
   void awaitCompletionUndeploy(final VirtualAppliance... vapp);

   /**
    * Monitor the given {@link VirtualAppliance}s and call populate an event
    * when undeploy finishes.
    * 
    * @param vapps
    *           The {@link VirtualAppliance}s to monitor.
    */
   public void monitorUndeploy(final VirtualAppliance... vapps);

   /**
    * Monitor the given {@link VirtualAppliance}s and blocks until the undeploy
    * finishes.
    * 
    * @param maxWait
    *           The maximum time to wait.
    * @param timeUnit
    *           The time unit for the maxWait parameter.
    * @param vapp
    *           The {@link VirtualAppliance}s to monitor.
    */
   void awaitCompletionUndeploy(final Long maxWait, final TimeUnit timeUnit, final VirtualAppliance... vapp);

   /**
    * Monitor the given {@link VirtualAppliance}s and populate an event when
    * undeploy finishes.
    * 
    * @param maxWait
    *           The maximum time to wait.
    * @param timeUnit
    *           The time unit for the maxWait parameter.
    * @param callback
    *           The callback.
    * @param vapps
    *           The {@link VirtualAppliance}s to monitor.
    */
   public void monitorUndeploy(final Long maxWait, final TimeUnit timeUnit, final VirtualAppliance... vapps);
}
