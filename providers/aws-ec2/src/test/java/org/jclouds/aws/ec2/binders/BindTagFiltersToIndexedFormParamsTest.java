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
package org.jclouds.aws.ec2.binders;

import static org.testng.Assert.*;

import java.io.File;
import java.net.URI;

import javax.ws.rs.HttpMethod;

import org.jclouds.aws.ec2.util.TagFilters;
import org.jclouds.aws.ec2.util.TagFilters.FilterName;
import org.jclouds.aws.ec2.util.TagFilters.ResourceType;
import org.jclouds.http.HttpRequest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code BindTagFiltersToIndexedFormParams}
 * 
 * @author grkvlt@apache.org
 */
@Test(groups = "unit")
public class BindTagFiltersToIndexedFormParamsTest {
   Injector injector = Guice.createInjector();
   BindTagFiltersToIndexedFormParams binder = injector.getInstance(BindTagFiltersToIndexedFormParams.class);

   public void testResourceTypeWithValues() {
      HttpRequest request = HttpRequest.builder().method("POST").endpoint(URI.create("http://localhost")).build();
      request = binder.bindToRequest(request, ImmutableSetMultimap.<FilterName, ResourceType>builder().put(FilterName.RESOURCE_TYPE, ResourceType.VPN_GATEWAY).put(FilterName.RESOURCE_TYPE, ResourceType.INTERNET_GATEWAY).build());
      assertEquals(request.getPayload().getRawContent(), "Filter.1.Name=resource-type&Filter.1.Value.1=vpn-gateway&Filter.1.Value.2=internet-gateway");
   }

   public void testMultipleKeys() {
      HttpRequest request = HttpRequest.builder().method("POST").endpoint(URI.create("http://localhost")).build();
      request = binder.bindToRequest(request, ImmutableSetMultimap.<FilterName, String>builder().put(FilterName.KEY, "one").put(FilterName.KEY, "two").build());
      assertEquals(request.getPayload().getRawContent(), "Filter.1.Name=key&Filter.1.Value.1=one&Filter.1.Value.2=two");
   }

   public void testkeyWithValue() {
      HttpRequest request = HttpRequest.builder().method("POST").endpoint(URI.create("http://localhost")).build();
      request = binder.bindToRequest(request, ImmutableSetMultimap.<FilterName, String>builder().put(FilterName.KEY, "one").put(FilterName.VALUE, "alpha").build());
      assertEquals(request.getPayload().getRawContent(), "Filter.1.Name=key&Filter.1.Value.1=one&Filter.2.Name=value&Filter.2.Value.1=alpha");
   }

   public void testAnyKey() {
      HttpRequest request = HttpRequest.builder().method("POST").endpoint(URI.create("http://localhost")).build();
      request = binder.bindToRequest(request, ImmutableSetMultimap.<FilterName, String>builder().put(FilterName.KEY, "*").build());
      assertEquals(request.getPayload().getRawContent(), "Filter.1.Name=key&Filter.1.Value.1=%2A");
   }

   public void testResourceTypeWithValuesBuilder() {
      HttpRequest request = HttpRequest.builder().method("POST").endpoint(URI.create("http://localhost")).build();
      request = binder.bindToRequest(request, TagFilters.filters().vpnGateway().internetGateway().build());
      assertEquals(request.getPayload().getRawContent(), "Filter.1.Name=resource-type&Filter.1.Value.1=vpn-gateway&Filter.1.Value.2=internet-gateway");
   }

   public void testMultipleKeysBuilder() {
      HttpRequest request = HttpRequest.builder().method("POST").endpoint(URI.create("http://localhost")).build();
      request = binder.bindToRequest(request, TagFilters.filters().key("one").key("two").build());
      assertEquals(request.getPayload().getRawContent(), "Filter.1.Name=key&Filter.1.Value.1=one&Filter.1.Value.2=two");
   }

   public void testkeyWithValueBuilder() {
      HttpRequest request = HttpRequest.builder().method("POST").endpoint(URI.create("http://localhost")).build();
      request = binder.bindToRequest(request, TagFilters.filters().keyValuePair("one", "alpha").build());
      assertEquals(request.getPayload().getRawContent(), "Filter.1.Name=key&Filter.1.Value.1=one&Filter.2.Name=value&Filter.2.Value.1=alpha");
   }

   public void testAnyKeyBuilder() {
      HttpRequest request = HttpRequest.builder().method("POST").endpoint(URI.create("http://localhost")).build();
      request = binder.bindToRequest(request, TagFilters.filters().anyKey().build());
      assertEquals(request.getPayload().getRawContent(), "Filter.1.Name=key&Filter.1.Value.1=%2A");
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testMustBeArray() {
      HttpRequest request = new HttpRequest(HttpMethod.POST, URI.create("http://localhost"));
      binder.bindToRequest(request, new File("foo"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testNullIsBad() {
      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://momma")).build();
      binder.bindToRequest(request, null);
   }
}