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

package org.jclouds.abiquo.binders.infrastructure;

import static org.testng.Assert.assertEquals;

import java.lang.reflect.Method;
import java.net.URI;

import javax.ws.rs.HttpMethod;

import org.jclouds.abiquo.features.InfrastructureAsyncApi;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.infrastructure.DatacenterDto;
import com.google.common.collect.ImmutableList;

/**
 * Unit tests for the {@link BindSupportedDevicesLinkToPath} class.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "unit", testName = "BindSupportedDevicesLinkToPathTest")
public class BindSupportedDevicesLinkToPathTest {
   @Test(expectedExceptions = NullPointerException.class)
   public void testGetNewEnpointNullInput() {
      BindSupportedDevicesLinkToPath binder = new BindSupportedDevicesLinkToPath();
      binder.getNewEndpoint(null, null);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testGetNewEnpointInvalidInput() {
      BindSupportedDevicesLinkToPath binder = new BindSupportedDevicesLinkToPath();
      binder.getNewEndpoint(null, new Object());
   }

   public void testGetNewEnpoint() throws Exception {
      DatacenterDto datacenter = new DatacenterDto();
      datacenter.addLink(new RESTLink("devices", "http://foo/bar"));

      BindSupportedDevicesLinkToPath binder = new BindSupportedDevicesLinkToPath();

      Method withEndpointLink = InfrastructureAsyncApi.class.getMethod("listSupportedStorageDevices",
            DatacenterDto.class);

      GeneratedHttpRequest request = GeneratedHttpRequest.builder().declaring(InfrastructureAsyncApi.class)
            .javaMethod(withEndpointLink).args(ImmutableList.<Object> of(datacenter)).method(HttpMethod.GET)
            .endpoint(URI.create("http://foo/bar")).build();

      assertEquals(binder.getNewEndpoint(request, datacenter), "http://foo/bar/action/supported");
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testGetNewEnpointWithoutLink() throws Exception {
      DatacenterDto datacenter = new DatacenterDto();

      BindSupportedDevicesLinkToPath binder = new BindSupportedDevicesLinkToPath();

      Method withEndpointLink = InfrastructureAsyncApi.class.getMethod("listSupportedStorageDevices",
            DatacenterDto.class);

      GeneratedHttpRequest request = GeneratedHttpRequest.builder().declaring(InfrastructureAsyncApi.class)
            .javaMethod(withEndpointLink).args(ImmutableList.<Object> of(datacenter)).method(HttpMethod.GET)
            .endpoint(URI.create("http://foo/bar")).build();

      assertEquals(binder.getNewEndpoint(request, datacenter), "http://foo/bar/action/supported");
   }
}
