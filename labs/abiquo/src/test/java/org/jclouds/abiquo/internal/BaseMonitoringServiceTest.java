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

package org.jclouds.abiquo.internal;

import static org.testng.Assert.assertNotNull;

import org.jclouds.abiquo.features.services.MonitoringService;
import org.jclouds.abiquo.monitor.MonitorStatus;
import org.testng.annotations.Test;

import com.google.common.base.Function;

/**
 * Unit tests for the {@link BaseMonitoringService} class.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "unit", testName = "BaseMonitoringServiceTest")
public class BaseMonitoringServiceTest extends BaseInjectionTest {
   public void testAllPropertiesInjected() {
      BaseMonitoringService service = (BaseMonitoringService) injector.getInstance(MonitoringService.class);

      assertNotNull(service.context);
      assertNotNull(service.scheduler);
      assertNotNull(service.pollingDelay);
      assertNotNull(service.eventBus);
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testAwaitCompletionWithNullFunction() {
      monitoringService().awaitCompletion(null, new Object[] {});
   }

   public void testAwaitCompletionWithoutTasks() {
      BaseMonitoringService service = monitoringService();

      service.awaitCompletion(new MockMonitor());
      service.awaitCompletion(new MockMonitor(), (Object[]) null);
      service.awaitCompletion(new MockMonitor(), new Object[] {});
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testMonitorWithNullCompleteCondition() {
      monitoringService().monitor(null, (Object[]) null);
   }

   public void testMonitorWithoutTasks() {
      monitoringService().monitor(new MockMonitor());
   }

   public void testDelegateToVirtualMachineMonitor() {
      assertNotNull(monitoringService().getVirtualMachineMonitor());
   }

   public void testDelegateToVirtualApplianceMonitor() {
      assertNotNull(monitoringService().getVirtualApplianceMonitor());
   }

   public void testDelegateToAsyncTaskMonitor() {
      assertNotNull(monitoringService().getAsyncTaskMonitor());
   }

   public void testDelegateToConversioMonitor() {
      assertNotNull(monitoringService().getConversionMonitor());
   }

   private BaseMonitoringService monitoringService() {
      return injector.getInstance(BaseMonitoringService.class);
   }

   private static class MockMonitor implements Function<Object, MonitorStatus> {
      @Override
      public MonitorStatus apply(final Object object) {
         return MonitorStatus.DONE;
      }
   }

}
