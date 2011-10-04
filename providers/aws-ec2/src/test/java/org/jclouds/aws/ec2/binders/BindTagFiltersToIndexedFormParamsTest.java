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

import org.jclouds.aws.ec2.domain.TagFilter;
import org.jclouds.aws.ec2.domain.TagFilter.FilterName;
import org.jclouds.http.HttpRequest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
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
      request = binder.bindToRequest(request, ImmutableList.builder().add(new TagFilter(FilterName.RESOURCE_TYPE,
              ImmutableList.<String>builder().add(TagFilter.ResourceType.VPN_GATEWAY.value()).add(TagFilter.ResourceType.INTERNET_GATEWAY.value()).build())).build());
      assertEquals(request.getPayload().getRawContent(), "Filter.1.Name=resource-type&Filter.1.Value.1=vpn-gateway&Filter.1.Value.2=internet-gateway");
   }

   public void testKeyWithValues() {
      HttpRequest request = HttpRequest.builder().method("POST").endpoint(URI.create("http://localhost")).build();
      request = binder.bindToRequest(request, ImmutableList.builder().add(new TagFilter(FilterName.KEY,
              ImmutableList.<String>builder().add("one").add("two").build())).build());
      assertEquals(request.getPayload().getRawContent(), "Filter.1.Name=key&Filter.1.Value.1=one&Filter.1.Value.2=two");
   }

   public void testKeyWithoutValues() {
      HttpRequest request = HttpRequest.builder().method("POST").endpoint(URI.create("http://localhost")).build();
      request = binder.bindToRequest(request, ImmutableList.builder().add(new TagFilter(FilterName.KEY, ImmutableList.<String>of())).build());
      assertEquals(request.getPayload().getRawContent(), "Filter.1.Name=key");
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