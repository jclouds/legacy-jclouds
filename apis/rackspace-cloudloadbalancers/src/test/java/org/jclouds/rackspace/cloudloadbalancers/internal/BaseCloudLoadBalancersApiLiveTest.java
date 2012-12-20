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
package org.jclouds.rackspace.cloudloadbalancers.internal;

import java.util.Properties;
import java.util.logging.Logger;

import org.jclouds.apis.BaseContextLiveTest;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties;
import org.jclouds.rackspace.cloudloadbalancers.CloudLoadBalancersApi;
import org.jclouds.rackspace.cloudloadbalancers.CloudLoadBalancersApiMetadata;
import org.jclouds.rackspace.cloudloadbalancers.CloudLoadBalancersAsyncApi;
import org.jclouds.rest.RestContext;
import org.testng.annotations.BeforeGroups;

import com.google.common.reflect.TypeToken;

/**
 * 
 * @author Adrian Cole
 */
public class BaseCloudLoadBalancersApiLiveTest extends BaseContextLiveTest<RestContext<CloudLoadBalancersApi, CloudLoadBalancersAsyncApi>> {
   protected CloudLoadBalancersApi clbApi;

   public BaseCloudLoadBalancersApiLiveTest() {
      provider = "rackspace-cloudloadbalancers";
   }

   @BeforeGroups(groups = { "integration", "live" })
   @Override
   public void setupContext() {
      super.setupContext();

      clbApi = context.getApi();
      
      Logger.getAnonymousLogger().info("running against zones " + clbApi.getConfiguredZones());
   }

   @Override
   protected Properties setupProperties() {
      Properties props = super.setupProperties();
      setIfTestSystemPropertyPresent(props, KeystoneProperties.CREDENTIAL_TYPE);
      return props;
   }
   
   @Override
   protected TypeToken<RestContext<CloudLoadBalancersApi, CloudLoadBalancersAsyncApi>> contextType() {
      return CloudLoadBalancersApiMetadata.CONTEXT_TOKEN;
   }
}
