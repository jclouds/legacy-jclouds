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
package org.jclouds.rackspace.clouddns.v1.config;

import java.net.URI;
import java.util.Map;

import org.jclouds.json.config.GsonModule.DateAdapter;
import org.jclouds.json.config.GsonModule.Iso8601DateAdapter;
import org.jclouds.rackspace.clouddns.v1.CloudDNSApi;
import org.jclouds.rackspace.clouddns.v1.CloudDNSAsyncApi;
import org.jclouds.rackspace.clouddns.v1.features.LimitApi;
import org.jclouds.rackspace.clouddns.v1.features.LimitAsyncApi;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.config.RestClientModule;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.inject.TypeLiteral;

/**
 * Configures Rackspace Cloud DNS.
 * 
 * @author Everett Toews
 */
@ConfiguresRestClient
public class CloudDNSRestClientModule extends RestClientModule<CloudDNSApi, CloudDNSAsyncApi> {

   public static final Map<Class<?>, Class<?>> DELEGATE_MAP = ImmutableMap.<Class<?>, Class<?>> builder()
         .put(LimitApi.class, LimitAsyncApi.class)
         .build();

   public CloudDNSRestClientModule() {
      super(DELEGATE_MAP);
   }

   @Override
   protected void configure() {
      bind(new TypeLiteral<Supplier<URI>>() {}).annotatedWith(CloudDNS.class).to(new TypeLiteral<Supplier<URI>>() {});
      bind(DateAdapter.class).to(Iso8601DateAdapter.class);
      super.configure();
   }
}
