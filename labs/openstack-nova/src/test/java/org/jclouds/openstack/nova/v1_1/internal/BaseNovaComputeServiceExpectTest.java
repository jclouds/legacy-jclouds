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

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.openstack.keystone.v2_0.internal.KeystoneFixture;
import org.jclouds.rest.BaseRestClientExpectTest;
import java.util.Properties;

/**
 * Base class for writing KeyStone Expect tests with the ComputeService abstraction
 *
 * @author Matt Stephenson
 */
public class BaseNovaComputeServiceExpectTest extends BaseRestClientExpectTest<ComputeService>
{
   protected HttpRequest keystoneAuthWithUsernameAndPassword;
   protected HttpRequest keystoneAuthWithAccessKeyAndSecretKey;
   protected String authToken;
   protected HttpResponse responseWithKeystoneAccess;

   public BaseNovaComputeServiceExpectTest()
   {
      provider = "openstack-nova";
      keystoneAuthWithUsernameAndPassword = KeystoneFixture.INSTANCE.initialAuthWithUsernameAndPassword(identity,
         credential);
      keystoneAuthWithAccessKeyAndSecretKey = KeystoneFixture.INSTANCE.initialAuthWithAccessKeyAndSecretKey(identity,
         credential);
      authToken = KeystoneFixture.INSTANCE.getAuthToken();
      responseWithKeystoneAccess = KeystoneFixture.INSTANCE.responseWithAccess();
      // now, createContext arg will need tenant prefix
      identity = KeystoneFixture.INSTANCE.getTenantName() + ":" + identity;
   }


   @Override
   public ComputeService createClient(Function<HttpRequest, HttpResponse> fn, Module module, Properties props)
   {
      return new ComputeServiceContextFactory(setupRestProperties()).createContext(provider, identity, credential,
         ImmutableSet.<Module>of(new ExpectModule(fn), new NullLoggingModule(), module), props).getComputeService();
   }
}
