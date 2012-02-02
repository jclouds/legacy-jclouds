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
package org.jclouds.openstack.nova.v1_1.internal;

import org.jclouds.http.RequiresHttp;
import org.jclouds.openstack.keystone.v2_0.internal.BaseKeystoneRestClientExpectTest;
import org.jclouds.openstack.nova.v1_1.NovaClient;
import org.jclouds.openstack.nova.v1_1.config.NovaRestClientModule;
import org.jclouds.rest.ConfiguresRestClient;

import com.google.inject.Module;

/**
 * Base class for writing KeyStone Rest Client Expect tests
 * 
 * @author Adrian Cole
 */
public class BaseNovaRestClientExpectTest extends BaseKeystoneRestClientExpectTest<NovaClient> {

   public BaseNovaRestClientExpectTest() {
      provider = "openstack-nova";
   }

   @Override
   protected Module createModule() {
      return new TestNovaRestClientModule();
   }

   @ConfiguresRestClient
   @RequiresHttp
   protected static class TestNovaRestClientModule extends NovaRestClientModule {
      private TestNovaRestClientModule() {
         super(new TestKeystoneAuthenticationModule());
      }
   }

}
