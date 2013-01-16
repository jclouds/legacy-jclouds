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
package org.jclouds.fujitsu.fgcp.compute;

import java.security.KeyStore;
import java.util.Calendar;
import java.util.Map;

import javax.annotation.Resource;
import javax.net.ssl.SSLContext;

import org.jclouds.date.TimeStamp;
import org.jclouds.fujitsu.fgcp.FGCPApi;
import org.jclouds.fujitsu.fgcp.FGCPAsyncApi;
import org.jclouds.fujitsu.fgcp.handlers.FGCPRetryIfNotProxyAuthenticationFailureHandler;
import org.jclouds.fujitsu.fgcp.location.SystemAndNetworkSegmentToLocationSupplier;
import org.jclouds.fujitsu.fgcp.services.AdditionalDiskApi;
import org.jclouds.fujitsu.fgcp.services.AdditionalDiskAsyncApi;
import org.jclouds.fujitsu.fgcp.services.BuiltinServerApi;
import org.jclouds.fujitsu.fgcp.services.BuiltinServerAsyncApi;
import org.jclouds.fujitsu.fgcp.services.DiskImageApi;
import org.jclouds.fujitsu.fgcp.services.DiskImageAsyncApi;
import org.jclouds.fujitsu.fgcp.services.FirewallApi;
import org.jclouds.fujitsu.fgcp.services.FirewallAsyncApi;
import org.jclouds.fujitsu.fgcp.services.LoadBalancerApi;
import org.jclouds.fujitsu.fgcp.services.LoadBalancerAsyncApi;
import org.jclouds.fujitsu.fgcp.services.PublicIPAddressApi;
import org.jclouds.fujitsu.fgcp.services.PublicIPAddressAsyncApi;
import org.jclouds.fujitsu.fgcp.services.SystemTemplateApi;
import org.jclouds.fujitsu.fgcp.services.SystemTemplateAsyncApi;
import org.jclouds.fujitsu.fgcp.services.VirtualDCApi;
import org.jclouds.fujitsu.fgcp.services.VirtualDCAsyncApi;
import org.jclouds.fujitsu.fgcp.services.VirtualServerApi;
import org.jclouds.fujitsu.fgcp.services.VirtualServerAsyncApi;
import org.jclouds.fujitsu.fgcp.services.VirtualSystemApi;
import org.jclouds.fujitsu.fgcp.services.VirtualSystemAsyncApi;
import org.jclouds.fujitsu.fgcp.suppliers.KeyStoreSupplier;
import org.jclouds.fujitsu.fgcp.suppliers.SSLContextWithKeysSupplier;
import org.jclouds.fujitsu.fgcp.xml.FGCPJAXBParser;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.location.suppliers.ImplicitLocationSupplier;
import org.jclouds.location.suppliers.LocationsSupplier;
import org.jclouds.location.suppliers.implicit.FirstNetwork;
import org.jclouds.logging.Logger;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.config.RestClientModule;
import org.jclouds.xml.XMLParser;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;

/**
 * Configures the FGCP connection. This module is added in FGCPContextBuilder.
 * 
 * @author Dies Koper
 */
@ConfiguresRestClient
public class FGCPRestClientModule extends
      RestClientModule<FGCPApi, FGCPAsyncApi> {

   @Resource
   Logger logger = Logger.NULL;

   public static final Map<Class<?>, Class<?>> DELEGATE_MAP = ImmutableMap
         .<Class<?>, Class<?>> builder()
         .put(VirtualDCApi.class, VirtualDCAsyncApi.class)
         .put(VirtualSystemApi.class, VirtualSystemAsyncApi.class)
         .put(VirtualServerApi.class, VirtualServerAsyncApi.class)
         .put(AdditionalDiskApi.class, AdditionalDiskAsyncApi.class)
         .put(SystemTemplateApi.class, SystemTemplateAsyncApi.class)
         .put(DiskImageApi.class, DiskImageAsyncApi.class)
         .put(BuiltinServerApi.class, BuiltinServerAsyncApi.class)
         .put(FirewallApi.class, FirewallAsyncApi.class)
         .put(LoadBalancerApi.class, LoadBalancerAsyncApi.class)
         .put(PublicIPAddressApi.class, PublicIPAddressAsyncApi.class)
         .build();

   public FGCPRestClientModule() {
      super(DELEGATE_MAP);
   }

   @Override
   protected void installLocations() {
      super.installLocations();
      bind(ImplicitLocationSupplier.class).to(FirstNetwork.class).in(Scopes.SINGLETON);
      bind(LocationsSupplier.class).to(SystemAndNetworkSegmentToLocationSupplier.class).in(Scopes.SINGLETON);
   }

   @Override
   protected void bindRetryHandlers() {
      bind(HttpRetryHandler.class).annotatedWith(ClientError.class).to(
            FGCPRetryIfNotProxyAuthenticationFailureHandler.class);
   }

   @Override
   protected void configure() {
      super.configure();
      bind(XMLParser.class).to(FGCPJAXBParser.class);
      bind(new TypeLiteral<Supplier<SSLContext>>() {
      }).to(new TypeLiteral<SSLContextWithKeysSupplier>() {
      });
      bind(new TypeLiteral<Supplier<KeyStore>>() {
      }).to(new TypeLiteral<KeyStoreSupplier>() {
      });
   }

   @Provides
   @TimeStamp
   protected Calendar provideCalendar() {
      return Calendar.getInstance();
   }
}
