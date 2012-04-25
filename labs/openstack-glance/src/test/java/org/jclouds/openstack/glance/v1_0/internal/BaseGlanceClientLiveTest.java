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
package org.jclouds.openstack.glance.v1_0.internal;

import java.util.Properties;

import org.jclouds.apis.BaseContextLiveTest;
import org.jclouds.openstack.glance.v1_0.GlanceApiMetadata;
import org.jclouds.openstack.glance.v1_0.GlanceAsyncClient;
import org.jclouds.openstack.glance.v1_0.GlanceClient;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties;
import org.jclouds.rest.RestContext;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.reflect.TypeToken;

/**
 * Tests behavior of {@code GlanceClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live")
public class BaseGlanceClientLiveTest extends BaseContextLiveTest<RestContext<GlanceClient, GlanceAsyncClient>> {

   public BaseGlanceClientLiveTest() {
      provider = "openstack-glance";
   }

   protected RestContext<GlanceClient, GlanceAsyncClient> glanceContext;

   @BeforeGroups(groups = { "integration", "live" })
   @Override
   public void setupContext() {
      super.setupContext();
      glanceContext = context;
   }

   @Override
   protected Properties setupProperties() {
      Properties props = super.setupProperties();
      setIfTestSystemPropertyPresent(props, KeystoneProperties.CREDENTIAL_TYPE);
      return props;
   }
   
   @AfterGroups(groups = "live")
   protected void tearDown() {
      if (glanceContext != null)
         glanceContext.close();
   }

   @Override
   protected TypeToken<RestContext<GlanceClient, GlanceAsyncClient>> contextType() {
      return GlanceApiMetadata.CONTEXT_TOKEN;
   }

}
