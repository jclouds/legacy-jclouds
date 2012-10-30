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

import java.util.Properties;

import org.jclouds.abiquo.AbiquoContext;
import org.jclouds.apis.BaseViewLiveTest;
import org.jclouds.logging.config.LoggingModule;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;

import com.google.common.reflect.TypeToken;

/**
 * Base class for Abiquo live tests.
 * 
 * @author Ignasi Barrera
 */
public abstract class BaseAbiquoLiveApiTest extends BaseViewLiveTest<AbiquoContext> {
   public BaseAbiquoLiveApiTest() {
      provider = "abiquo";
   }

   @Override
   protected Properties setupProperties() {
      Properties overrides = super.setupProperties();
      // Wait at most one minute in Machine discovery
      overrides.put("jclouds.timeouts.InfrastructureApi.discoverSingleMachine", "60000");
      overrides.put("jclouds.timeouts.InfrastructureApi.discoverMultipleMachines", "60000");
      overrides.put("jclouds.timeouts.InfrastructureApi.createMachine", "60000");
      overrides.put("jclouds.timeouts.InfrastructureApi.updateMachine", "60000");
      overrides.put("jclouds.timeouts.InfrastructureApi.checkMachineState", "60000");
      overrides.put("jclouds.timeouts.CloudApi.listVirtualMachines", "60000");
      return overrides;
   }

   @Override
   protected LoggingModule getLoggingModule() {
      return new SLF4JLoggingModule();
   }

   @Override
   protected TypeToken<AbiquoContext> viewType() {
      return TypeToken.of(AbiquoContext.class);
   }

}
