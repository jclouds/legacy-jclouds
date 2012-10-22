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

import org.jclouds.ContextBuilder;
import org.jclouds.abiquo.AbiquoApiMetadata;
import org.jclouds.abiquo.AbiquoContext;
import org.jclouds.lifecycle.Closer;
import org.jclouds.logging.config.NullLoggingModule;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * Unit tests for the {@link BaseCloudService} class.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "unit", testName = "BaseEventServiceTest")
public class BaseInjectionTest {
   protected Injector injector;

   @BeforeClass
   public void setup() {
      injector = ContextBuilder.newBuilder(new AbiquoApiMetadata()) //
            .credentials("identity", "credential") //
            .modules(ImmutableSet.<Module> of(new NullLoggingModule())) //
            .overrides(buildProperties()) //
            .build(AbiquoContext.class).getUtils().getInjector();
   }

   protected Properties buildProperties() {
      return new Properties();
   }

   @AfterClass
   public void tearDown() throws Exception {
      if (injector != null) {
         injector.getInstance(Closer.class).close();
      }
   }

}
