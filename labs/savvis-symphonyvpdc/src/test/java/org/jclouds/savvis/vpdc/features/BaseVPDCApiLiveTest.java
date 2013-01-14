/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.savvis.vpdc.features;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.util.Predicates2.retry;

import java.util.Properties;

import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.jclouds.rest.RestContext;
import org.jclouds.savvis.vpdc.VPDCApi;
import org.jclouds.savvis.vpdc.VPDCAsyncApi;
import org.jclouds.savvis.vpdc.predicates.TaskSuccess;
import org.jclouds.savvis.vpdc.reference.VPDCConstants;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;

/**
 * Tests behavior of {@code VPDCApi}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "BaseVPDCApiLiveTest")
public class BaseVPDCApiLiveTest extends BaseComputeServiceContextLiveTest {

   public BaseVPDCApiLiveTest() {
      provider = "savvis-symphonyvpdc";
   }

   protected RestContext<VPDCApi, VPDCAsyncApi> restContext;
   protected String email;
   protected Predicate<String> taskTester;

   @Override
   protected Properties setupProperties() {
      Properties overrides = super.setupProperties();
      overrides.setProperty(VPDCConstants.PROPERTY_VPDC_VDC_EMAIL, email);
      // unlimited timeouts
      overrides.setProperty("jclouds.connection-timeout", "0");
      overrides.setProperty("jclouds.so-timeout", "0");
      email = checkNotNull(System.getProperty("test." + VPDCConstants.PROPERTY_VPDC_VDC_EMAIL), "test."
            + VPDCConstants.PROPERTY_VPDC_VDC_EMAIL);
      return overrides;
   }

   @BeforeGroups(groups = { "integration", "live" })
   @Override
   public void setupContext() {
      super.setupContext();
      taskTester = retry(new TaskSuccess(restContext.getApi()), 7200, 10, SECONDS);
   }

}
