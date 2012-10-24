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

package org.jclouds.abiquo.config;

import static org.testng.Assert.assertNotNull;

import java.util.concurrent.ScheduledExecutorService;

import org.jclouds.Constants;
import org.jclouds.abiquo.internal.BaseInjectionTest;
import org.testng.annotations.Test;

import com.google.inject.Key;
import com.google.inject.name.Names;

/**
 * Unit tests for the {@link SchedulerModule} class.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "unit", testName = "SchedulerModuleTest")
public class SchedulerModuleTest extends BaseInjectionTest {
   public void testScheduledExecutorIsProvided() {
      assertNotNull(injector.getInstance(Key.get(ScheduledExecutorService.class,
            Names.named(Constants.PROPERTY_SCHEDULER_THREADS))));
   }
}
