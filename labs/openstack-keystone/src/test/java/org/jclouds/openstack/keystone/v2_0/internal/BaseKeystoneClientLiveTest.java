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
package org.jclouds.openstack.keystone.v2_0.internal;

import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.SERVICE_TYPE;

import java.util.Properties;

import org.jclouds.apis.BaseContextLiveTest;
import org.jclouds.openstack.keystone.v2_0.KeystoneApiMetadata;
import org.jclouds.openstack.keystone.v2_0.KeystoneAsyncClient;
import org.jclouds.openstack.keystone.v2_0.KeystoneClient;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties;
import org.jclouds.openstack.services.ServiceType;
import org.jclouds.rest.RestContext;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.reflect.TypeToken;

/**
 * Tests behavior of {@code KeystoneClient}
 * 
 * @author Adam Lowe
 */
@Test(groups = "live")
public class BaseKeystoneClientLiveTest extends BaseContextLiveTest<RestContext<KeystoneClient, KeystoneAsyncClient>> {

   public BaseKeystoneClientLiveTest() {
      provider = "openstack-keystone";
   }

   protected RestContext<KeystoneClient, KeystoneAsyncClient> keystoneContext;

   @BeforeGroups(groups = { "integration", "live" })
   @Override
   public void setupContext() {
      super.setupContext();
      keystoneContext = context;
   }

   @Override
   protected Properties setupProperties() {
      Properties props = super.setupProperties();
      setIfTestSystemPropertyPresent(props, KeystoneProperties.CREDENTIAL_TYPE);
      return props;
   }

   @AfterGroups(groups = "live")
   protected void tearDown() {
      if (keystoneContext != null)
         keystoneContext.close();
   }

   @Override
   protected TypeToken<RestContext<KeystoneClient, KeystoneAsyncClient>> contextType() {
      return KeystoneApiMetadata.CONTEXT_TOKEN;
   }

}
