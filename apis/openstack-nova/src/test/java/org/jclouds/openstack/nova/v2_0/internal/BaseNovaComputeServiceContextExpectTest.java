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
package org.jclouds.openstack.nova.v2_0.internal;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.nova.v2_0.NovaApiMetadata;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMultimap;
import com.google.inject.Module;

/**
 * Base class for writing KeyStone Expect tests with the ComputeService abstraction
 * 
 * @author Matt Stephenson
 */
public abstract class BaseNovaComputeServiceContextExpectTest<T> extends BaseNovaExpectTest<T> implements
         Function<ComputeServiceContext, T> {
   
   protected final HttpRequest listImagesDetail = HttpRequest.builder().method("GET").endpoint(
            URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/images/detail")).headers(
            ImmutableMultimap.<String, String> builder().put("Accept", "application/json").put("X-Auth-Token",
                     authToken).build()).build();

   protected final HttpResponse listImagesDetailResponse = HttpResponse.builder().statusCode(200).payload(
            payloadFromResource("/image_list_detail.json")).build();

   protected final HttpRequest listFlavorsDetail = HttpRequest.builder().method("GET").endpoint(
            URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/flavors/detail")).headers(
            ImmutableMultimap.<String, String> builder().put("Accept", "application/json").put("X-Auth-Token",
                     authToken).build()).build();

   protected final HttpResponse listFlavorsDetailResponse = HttpResponse.builder().statusCode(200).payload(
            payloadFromResource("/flavor_list_detail.json")).build();

   protected final HttpRequest listServers = HttpRequest.builder().method("GET").endpoint(
            URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers/detail")).headers(
            ImmutableMultimap.<String, String> builder().put("Accept", "application/json").put("X-Auth-Token",
                     authToken).build()).build();

   protected final HttpResponse listServersResponse = HttpResponse.builder().statusCode(200).payload(
            payloadFromResource("/server_list_details.json")).build();

   protected final HttpRequest listFloatingIps = HttpRequest.builder().method("GET").endpoint(
            URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-floating-ips")).headers(
            ImmutableMultimap.<String, String> builder().put("Accept", "application/json").put("X-Auth-Token",
                     authToken).build()).build();

   protected final HttpResponse listFloatingIpsResponse = HttpResponse.builder().statusCode(200).payload(
            payloadFromResource("/floatingip_list.json")).build();

   @Override
   public T createClient(Function<HttpRequest, HttpResponse> fn, Module module, Properties props) {
      return apply(createComputeServiceContext(fn, module, props));
   }

   private ComputeServiceContext createComputeServiceContext(Function<HttpRequest, HttpResponse> fn, Module module,
         Properties props) {
      return createInjector(fn, module, props).getInstance(ComputeServiceContext.class);
   }
   
   @Override
   protected ApiMetadata createApiMetadata() {
      return new NovaApiMetadata();
   }

}
