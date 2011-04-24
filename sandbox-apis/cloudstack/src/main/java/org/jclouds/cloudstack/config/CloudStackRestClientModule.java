/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.cloudstack.config;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

import javax.inject.Singleton;

import org.jclouds.cloudstack.CloudStackAsyncClient;
import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.domain.Account;
import org.jclouds.cloudstack.domain.User;
import org.jclouds.cloudstack.domain.Account.State;
import org.jclouds.cloudstack.features.AccountAsyncClient;
import org.jclouds.cloudstack.features.AccountClient;
import org.jclouds.cloudstack.features.AddressAsyncClient;
import org.jclouds.cloudstack.features.AddressClient;
import org.jclouds.cloudstack.features.AsyncJobAsyncClient;
import org.jclouds.cloudstack.features.AsyncJobClient;
import org.jclouds.cloudstack.features.ConfigurationAsyncClient;
import org.jclouds.cloudstack.features.ConfigurationClient;
import org.jclouds.cloudstack.features.FirewallAsyncClient;
import org.jclouds.cloudstack.features.FirewallClient;
import org.jclouds.cloudstack.features.GuestOSAsyncClient;
import org.jclouds.cloudstack.features.GuestOSClient;
import org.jclouds.cloudstack.features.HypervisorAsyncClient;
import org.jclouds.cloudstack.features.HypervisorClient;
import org.jclouds.cloudstack.features.LoadBalancerAsyncClient;
import org.jclouds.cloudstack.features.LoadBalancerClient;
import org.jclouds.cloudstack.features.NATAsyncClient;
import org.jclouds.cloudstack.features.NATClient;
import org.jclouds.cloudstack.features.NetworkAsyncClient;
import org.jclouds.cloudstack.features.NetworkClient;
import org.jclouds.cloudstack.features.OfferingAsyncClient;
import org.jclouds.cloudstack.features.OfferingClient;
import org.jclouds.cloudstack.features.SecurityGroupAsyncClient;
import org.jclouds.cloudstack.features.SecurityGroupClient;
import org.jclouds.cloudstack.features.TemplateAsyncClient;
import org.jclouds.cloudstack.features.TemplateClient;
import org.jclouds.cloudstack.features.VirtualMachineAsyncClient;
import org.jclouds.cloudstack.features.VirtualMachineClient;
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
import org.jclouds.net.IPSocket;
import org.jclouds.predicates.SocketOpen;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.config.RestClientModule;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;
import com.google.inject.TypeLiteral;

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
            .build();

   public CloudStackRestClientModule() {
      super(CloudStackClient.class, CloudStackAsyncClient.class, DELEGATE_MAP);
   }

   @Singleton
   public static class BreakGenericSetAdapter implements JsonSerializer<Account>, JsonDeserializer<Account> {

      public JsonElement serialize(Account src, Type typeOfSrc, JsonSerializationContext context) {
         return context.serialize(src);
      }

      public Account deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
               throws JsonParseException {
         return apply(context.<AccountInternal> deserialize(json, AccountInternal.class));
      }

      public Account apply(AccountInternal in) {
         return Account.builder().id(in.id).type(in.type).domain(in.domain).domainId(in.domainId).IPsAvailable(
                  nullIfUnlimited(in.IPsAvailable)).IPLimit(nullIfUnlimited(in.IPLimit)).IPs(in.IPs).cleanupRequired(
                  in.cleanupRequired).name(in.name).receivedBytes(in.receivedBytes).sentBytes(in.sentBytes)
                  .snapshotsAvailable(nullIfUnlimited(in.snapshotsAvailable)).snapshotLimit(
                           nullIfUnlimited(in.snapshotLimit)).snapshots(in.snapshots).state(in.state)
                  .templatesAvailable(nullIfUnlimited(in.templatesAvailable)).templateLimit(
                           nullIfUnlimited(in.templateLimit)).templates(in.templates).VMsAvailable(
                           nullIfUnlimited(in.VMsAvailable)).VMLimit(nullIfUnlimited(in.VMLimit)).VMsRunning(
                           in.VMsRunning).VMsStopped(in.VMsStopped).VMs(in.VMs).volumesAvailable(
                           nullIfUnlimited(in.volumesAvailable)).volumeLimit(nullIfUnlimited(in.volumeLimit)).volumes(
                           in.volumes).users(in.users).build();
      }

      static final class AccountInternal {
         private long id;
         @SerializedName("accounttype")
         private Account.Type type;
         private String domain;
         @SerializedName("domainid")
         private long domainId;
         @SerializedName("ipavailable")
         private String IPsAvailable;
         @SerializedName("iplimit")
         private String IPLimit;
         @SerializedName("iptotal")
         private long IPs;
         @SerializedName("iscleanuprequired")
         private boolean cleanupRequired;
         private String name;
         @SerializedName("receivedbytes")
         private long receivedBytes;
         @SerializedName("sentBytes")
         private long sentBytes;
         @SerializedName("snapshotavailable")
         private String snapshotsAvailable;
         @SerializedName("snapshotlimit")
         private String snapshotLimit;
         @SerializedName("snapshottotal")
         private long snapshots;
         @SerializedName("state")
         private State state;
         @SerializedName("templateavailable")
         private String templatesAvailable;
         @SerializedName("templatelimit")
         private String templateLimit;
         @SerializedName("templatetotal")
         private long templates;
         @SerializedName("vmavailable")
         private String VMsAvailable;
         @SerializedName("vmlimit")
         private String VMLimit;
         @SerializedName("vmrunning")
         private long VMsRunning;
         @SerializedName("vmstopped")
         private long VMsStopped;
         @SerializedName("vmtotal")
         private long VMs;
         @SerializedName("volumeavailable")
         private String volumesAvailable;
         @SerializedName("volumelimit")
         private String volumeLimit;
         @SerializedName("volumetotal")
         private long volumes;
         @SerializedName("user")
         private Set<User> users;
      }

      private static Long nullIfUnlimited(String in) {
         return in == null || "Unlimited".equals(in) ? null : new Long(in);
      }
   }

   @Override
   protected void configure() {
      bind(DateAdapter.class).to(Iso8601DateAdapter.class);
      bind(SocketOpen.class).toInstance(new SocketOpen() {

         @Override
         public boolean apply(IPSocket arg0) {
            return true;
         }

      });
      bind(new TypeLiteral<Map<Type, Object>>() {
      }).toInstance(ImmutableMap.<Type, Object> of(Account.class, new BreakGenericSetAdapter()));
      super.configure();
   }

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(CloudStackErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(CloudStackErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(CloudStackErrorHandler.class);
   }

}
