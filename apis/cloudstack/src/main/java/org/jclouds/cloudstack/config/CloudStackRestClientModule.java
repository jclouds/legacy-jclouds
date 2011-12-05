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
package org.jclouds.cloudstack.config;

import com.google.common.collect.ImmutableMap;
import com.google.inject.TypeLiteral;
import org.jclouds.cloudstack.CloudStackAsyncClient;
import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.CloudStackDomainAsyncClient;
import org.jclouds.cloudstack.CloudStackDomainClient;
import org.jclouds.cloudstack.CloudStackGlobalAsyncClient;
import org.jclouds.cloudstack.CloudStackGlobalClient;
import org.jclouds.cloudstack.features.AccountAsyncClient;
import org.jclouds.cloudstack.features.AccountClient;
import org.jclouds.cloudstack.features.AddressAsyncClient;
import org.jclouds.cloudstack.features.AddressClient;
import org.jclouds.cloudstack.features.AsyncJobAsyncClient;
import org.jclouds.cloudstack.features.AsyncJobClient;
import org.jclouds.cloudstack.features.ConfigurationAsyncClient;
import org.jclouds.cloudstack.features.ConfigurationClient;
import org.jclouds.cloudstack.features.DomainAccountAsyncClient;
import org.jclouds.cloudstack.features.DomainAccountClient;
import org.jclouds.cloudstack.features.DomainLimitAsyncClient;
import org.jclouds.cloudstack.features.DomainLimitClient;
import org.jclouds.cloudstack.features.EventAsyncClient;
import org.jclouds.cloudstack.features.EventClient;
import org.jclouds.cloudstack.features.FirewallAsyncClient;
import org.jclouds.cloudstack.features.FirewallClient;
import org.jclouds.cloudstack.features.GlobalAccountAsyncClient;
import org.jclouds.cloudstack.features.GlobalAccountClient;
import org.jclouds.cloudstack.features.GlobalAlertAsyncClient;
import org.jclouds.cloudstack.features.GlobalAlertClient;
import org.jclouds.cloudstack.features.GuestOSAsyncClient;
import org.jclouds.cloudstack.features.GuestOSClient;
import org.jclouds.cloudstack.features.HypervisorAsyncClient;
import org.jclouds.cloudstack.features.HypervisorClient;
import org.jclouds.cloudstack.features.ISOAsyncClient;
import org.jclouds.cloudstack.features.ISOClient;
import org.jclouds.cloudstack.features.LimitAsyncClient;
import org.jclouds.cloudstack.features.LimitClient;
import org.jclouds.cloudstack.features.LoadBalancerAsyncClient;
import org.jclouds.cloudstack.features.LoadBalancerClient;
import org.jclouds.cloudstack.features.NATAsyncClient;
import org.jclouds.cloudstack.features.NATClient;
import org.jclouds.cloudstack.features.NetworkAsyncClient;
import org.jclouds.cloudstack.features.NetworkClient;
import org.jclouds.cloudstack.features.OfferingAsyncClient;
import org.jclouds.cloudstack.features.OfferingClient;
import org.jclouds.cloudstack.features.SSHKeyPairAsyncClient;
import org.jclouds.cloudstack.features.SSHKeyPairClient;
import org.jclouds.cloudstack.features.SecurityGroupAsyncClient;
import org.jclouds.cloudstack.features.SecurityGroupClient;
import org.jclouds.cloudstack.features.SnapshotAsyncClient;
import org.jclouds.cloudstack.features.SnapshotClient;
import org.jclouds.cloudstack.features.TemplateAsyncClient;
import org.jclouds.cloudstack.features.TemplateClient;
import org.jclouds.cloudstack.features.VMGroupAsyncClient;
import org.jclouds.cloudstack.features.VMGroupClient;
import org.jclouds.cloudstack.features.VirtualMachineAsyncClient;
import org.jclouds.cloudstack.features.VirtualMachineClient;
import org.jclouds.cloudstack.features.VolumeAsyncClient;
import org.jclouds.cloudstack.features.VolumeClient;
import org.jclouds.cloudstack.features.ZoneAsyncClient;
import org.jclouds.cloudstack.features.ZoneClient;
import org.jclouds.cloudstack.handlers.CloudStackErrorHandler;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.RequiresHttp;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.json.config.GsonModule.DateAdapter;
import org.jclouds.json.config.GsonModule.Iso8601DateAdapter;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.config.BinderUtils;
import org.jclouds.rest.config.RestClientModule;
import org.jclouds.rest.internal.RestContextImpl;

import java.util.Map;

/**
 * Configures the cloudstack connection.
 * 
 * @author Adrian Cole
 */
@RequiresHttp
@ConfiguresRestClient
public class CloudStackRestClientModule extends RestClientModule<CloudStackClient, CloudStackAsyncClient> {

   public static final Map<Class<?>, Class<?>> DELEGATE_MAP = ImmutableMap.<Class<?>, Class<?>> builder()//
         .put(ZoneClient.class, ZoneAsyncClient.class)//
         .put(TemplateClient.class, TemplateAsyncClient.class)//
         .put(OfferingClient.class, OfferingAsyncClient.class)//
         .put(NetworkClient.class, NetworkAsyncClient.class)//
         .put(VirtualMachineClient.class, VirtualMachineAsyncClient.class)//
         .put(SecurityGroupClient.class, SecurityGroupAsyncClient.class)//
         .put(AsyncJobClient.class, AsyncJobAsyncClient.class)//
         .put(AddressClient.class, AddressAsyncClient.class)//
         .put(NATClient.class, NATAsyncClient.class)//
         .put(FirewallClient.class, FirewallAsyncClient.class)//
         .put(LoadBalancerClient.class, LoadBalancerAsyncClient.class)//
         .put(GuestOSClient.class, GuestOSAsyncClient.class)//
         .put(HypervisorClient.class, HypervisorAsyncClient.class)//
         .put(ConfigurationClient.class, ConfigurationAsyncClient.class)//
         .put(AccountClient.class, AccountAsyncClient.class)//
         .put(DomainAccountClient.class, DomainAccountAsyncClient.class)//
         .put(GlobalAccountClient.class, GlobalAccountAsyncClient.class)//
         .put(EventClient.class, EventAsyncClient.class)//
         .put(LimitClient.class, LimitAsyncClient.class)//
         .put(DomainLimitClient.class, DomainLimitAsyncClient.class)//
         .put(SSHKeyPairClient.class, SSHKeyPairAsyncClient.class)//
         .put(VMGroupClient.class, VMGroupAsyncClient.class)//
         .put(ISOClient.class, ISOAsyncClient.class)//
         .put(VolumeClient.class, VolumeAsyncClient.class)//
         .put(SnapshotClient.class, SnapshotAsyncClient.class)//
         .put(GlobalAlertClient.class, GlobalAlertAsyncClient.class)//
         .build();

   @Override
   protected void bindAsyncClient() {
      // bind the user client (default)
      super.bindAsyncClient();
      // bind the domain admin client
      BinderUtils.bindAsyncClient(binder(), CloudStackDomainAsyncClient.class);
      // bind the global admin client
      BinderUtils.bindAsyncClient(binder(), CloudStackGlobalAsyncClient.class);
   }

   @Override
   protected void bindClient() {
      // bind the user client (default)
      super.bindClient();
      // bind the domain admin client
      BinderUtils.bindClient(binder(), CloudStackDomainClient.class, CloudStackDomainAsyncClient.class, DELEGATE_MAP);
      // bind the domain admin client
      BinderUtils.bindClient(binder(), CloudStackGlobalClient.class, CloudStackGlobalAsyncClient.class, DELEGATE_MAP);
   }

   public CloudStackRestClientModule() {
      super(CloudStackClient.class, CloudStackAsyncClient.class, DELEGATE_MAP);
   }

   @Override
   protected void configure() {
      bind(DateAdapter.class).to(Iso8601DateAdapter.class);
      bind(new TypeLiteral<RestContext<CloudStackDomainClient, CloudStackDomainAsyncClient>>() {
      }).to(new TypeLiteral<RestContextImpl<CloudStackDomainClient, CloudStackDomainAsyncClient>>() {
      });
      bind(new TypeLiteral<RestContext<CloudStackGlobalClient, CloudStackGlobalAsyncClient>>() {
      }).to(new TypeLiteral<RestContextImpl<CloudStackGlobalClient, CloudStackGlobalAsyncClient>>() {
      });
      install(new CloudStackParserModule());
      super.configure();
   }

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(CloudStackErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(CloudStackErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(CloudStackErrorHandler.class);
   }

}
