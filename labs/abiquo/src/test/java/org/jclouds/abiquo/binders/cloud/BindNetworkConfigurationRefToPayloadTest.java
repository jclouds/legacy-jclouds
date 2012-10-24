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

package org.jclouds.abiquo.binders.cloud;

import static org.jclouds.abiquo.domain.DomainUtils.withHeader;
import static org.jclouds.abiquo.util.Assert.assertPayloadEquals;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.NoSuchElementException;

import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;

import org.jclouds.abiquo.domain.CloudResources;
import org.jclouds.abiquo.domain.NetworkResources;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.xml.internal.JAXBParser;
import org.testng.annotations.Test;

import com.abiquo.model.transport.LinksDto;
import com.abiquo.server.core.cloud.VirtualMachineDto;
import com.abiquo.server.core.infrastructure.network.VLANNetworkDto;
import com.google.common.collect.ImmutableList;

/**
 * Unit tests for the {@link BindNetworkConfigurationRefToPayload} binder.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "unit", testName = "BindNetworkConfigurationRefToPayloadTest")
public class BindNetworkConfigurationRefToPayloadTest {
   @Test(expectedExceptions = NullPointerException.class)
   public void testInvalidNullRequest() throws SecurityException, NoSuchMethodException {
      BindNetworkConfigurationRefToPayload binder = new BindNetworkConfigurationRefToPayload(new JAXBParser("false"));
      binder.bindToRequest(null, new Object());
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testInvalidRequestType() throws SecurityException, NoSuchMethodException {
      BindNetworkConfigurationRefToPayload binder = new BindNetworkConfigurationRefToPayload(new JAXBParser("false"));

      binder.bindToRequest(HttpRequest.builder().method("m").endpoint("http://localhost").build(), new Object());
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testInvalidNullInput() throws SecurityException, NoSuchMethodException {
      VirtualMachineDto vm = CloudResources.virtualMachinePut();

      Method method = TestNetworkConfig.class.getMethod("withAll", VirtualMachineDto.class, VLANNetworkDto.class);
      GeneratedHttpRequest request = GeneratedHttpRequest.builder().declaring(TestNetworkConfig.class)
            .javaMethod(method).args(ImmutableList.<Object> of(vm, null)).method(HttpMethod.GET)
            .endpoint(URI.create("http://localhost")).build();

      BindNetworkConfigurationRefToPayload binder = new BindNetworkConfigurationRefToPayload(new JAXBParser("false"));
      binder.bindToRequest(request, null);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testInvalidTypeInput() throws SecurityException, NoSuchMethodException {
      VirtualMachineDto vm = CloudResources.virtualMachinePut();
      Object network = new Object();

      Method method = TestNetworkConfig.class.getMethod("withAll", VirtualMachineDto.class, VLANNetworkDto.class);
      GeneratedHttpRequest request = GeneratedHttpRequest.builder().declaring(TestNetworkConfig.class)
            .javaMethod(method).args(ImmutableList.<Object> of(vm, network)).method(HttpMethod.GET)
            .endpoint(URI.create("http://localhost")).build();

      BindNetworkConfigurationRefToPayload binder = new BindNetworkConfigurationRefToPayload(new JAXBParser("false"));
      binder.bindToRequest(request, network);
   }

   @Test(expectedExceptions = NoSuchElementException.class)
   public void testBindNetworkConfigurationRefWithoutVirtualMachine() throws SecurityException, NoSuchMethodException {
      VLANNetworkDto network = NetworkResources.privateNetworkPut();

      Method method = TestNetworkConfig.class.getMethod("withoutVirtualMachine", VLANNetworkDto.class);
      GeneratedHttpRequest request = GeneratedHttpRequest.builder().declaring(TestNetworkConfig.class)
            .javaMethod(method).args(ImmutableList.<Object> of(network)).method(HttpMethod.GET)
            .endpoint(URI.create("http://localhost")).build();

      BindNetworkConfigurationRefToPayload binder = new BindNetworkConfigurationRefToPayload(new JAXBParser("false"));
      binder.bindToRequest(request, network);
   }

   public void testBindNetworkConfigurationRef() throws SecurityException, NoSuchMethodException, IOException {
      VirtualMachineDto vm = CloudResources.virtualMachinePut();
      VLANNetworkDto network = NetworkResources.privateNetworkPut();

      Method method = TestNetworkConfig.class.getMethod("withAll", VirtualMachineDto.class, VLANNetworkDto.class);
      GeneratedHttpRequest request = GeneratedHttpRequest.builder().declaring(TestNetworkConfig.class)
            .javaMethod(method).args(ImmutableList.<Object> of(vm, network)).method(HttpMethod.GET)
            .endpoint(URI.create("http://localhost")).build();

      BindNetworkConfigurationRefToPayload binder = new BindNetworkConfigurationRefToPayload(new JAXBParser("false"));

      String configLink = vm.searchLink("configurations").getHref() + "/" + network.getId();

      GeneratedHttpRequest newRequest = binder.bindToRequest(request, network);
      assertPayloadEquals(newRequest.getPayload(), withHeader("<links><link href=\"" + configLink
            + "\" rel=\"network_configuration\"/></links>"), LinksDto.class);
   }

   static interface TestNetworkConfig {
      @GET
      void withoutVirtualMachine(VLANNetworkDto network);

      @GET
      void withAll(VirtualMachineDto virtualMachine, VLANNetworkDto network);
   }
}
