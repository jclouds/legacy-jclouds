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
package org.jclouds.openstack.cinder.v1.internal;

import java.util.Properties;

import org.jclouds.apis.BaseContextLiveTest;
import org.jclouds.openstack.cinder.v1.CinderApi;
import org.jclouds.openstack.cinder.v1.CinderApiMetadata;
import org.jclouds.openstack.cinder.v1.CinderAsyncApi;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties;
import org.jclouds.rest.RestContext;
import org.testng.annotations.BeforeGroups;

import com.google.common.reflect.TypeToken;

/**
 * Tests behavior of CinderApi
 * 
 * @author Everett Toews
 */
public class BaseCinderApiLiveTest extends BaseContextLiveTest<RestContext<CinderApi, CinderAsyncApi>> {

   public BaseCinderApiLiveTest() {
      provider = "openstack-cinder";
   }

   protected RestContext<CinderApi, CinderAsyncApi> cinder;

   @BeforeGroups(groups = { "integration", "live" })
   @Override
   public void setupContext() {
      super.setupContext();
      cinder = context;
   }

   @Override
   protected Properties setupProperties() {
      Properties props = super.setupProperties();
      setIfTestSystemPropertyPresent(props, KeystoneProperties.CREDENTIAL_TYPE);
      return props;
   }
   
   @Override
   protected TypeToken<RestContext<CinderApi, CinderAsyncApi>> contextType() {
      return CinderApiMetadata.CONTEXT_TOKEN;
   }

}