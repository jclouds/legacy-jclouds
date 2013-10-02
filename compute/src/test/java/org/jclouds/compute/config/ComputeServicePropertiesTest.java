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
package org.jclouds.compute.config;

import static org.testng.Assert.assertEquals;

import java.util.Properties;

import org.jclouds.ContextBuilder;
import org.jclouds.compute.reference.ComputeServiceConstants.InitStatusProperties;
import org.jclouds.compute.reference.ComputeServiceConstants.PollPeriod;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ComputeServicePropertiesTest")
public class ComputeServicePropertiesTest {
   public void testDefaultInitStatusProperties() {
      InitStatusProperties props = ContextBuilder.newBuilder("stub").buildInjector()
            .getInstance(InitStatusProperties.class);
      assertEquals(props.initStatusInitialPeriod, 500);
      assertEquals(props.initStatusMaxPeriod, 5000);
   }

   public void testOverrideInitStatusProperties() {
      Properties overrides = new Properties();
      overrides.setProperty(ComputeServiceProperties.INIT_STATUS_INITIAL_PERIOD, "501");
      overrides.setProperty(ComputeServiceProperties.INIT_STATUS_MAX_PERIOD, "5001");
      
      InitStatusProperties props = ContextBuilder.newBuilder("stub").overrides(overrides).buildInjector()
            .getInstance(InitStatusProperties.class);
      
      assertEquals(props.initStatusInitialPeriod, 501);
      assertEquals(props.initStatusMaxPeriod, 5001);
   }

   public void testDefaultPollPeriod() {
	   PollPeriod props = ContextBuilder.newBuilder("stub").buildInjector()
            .getInstance(PollPeriod.class);
      assertEquals(props.pollInitialPeriod, 50);
      assertEquals(props.pollMaxPeriod, 1000);
   }

   public void testOverridePollPeriod() {
      Properties overrides = new Properties();
      overrides.setProperty(ComputeServiceProperties.POLL_INITIAL_PERIOD, "501");
      overrides.setProperty(ComputeServiceProperties.POLL_MAX_PERIOD, "5001");
      
      PollPeriod props = ContextBuilder.newBuilder("stub").overrides(overrides).buildInjector()
            .getInstance(PollPeriod.class);
      
      assertEquals(props.pollInitialPeriod, 501);
      assertEquals(props.pollMaxPeriod, 5001);
   }
}
