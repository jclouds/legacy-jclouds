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

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Singleton;

import com.google.common.collect.Sets;
import org.jclouds.cloudstack.domain.Account;
import org.jclouds.cloudstack.domain.Account.State;
import org.jclouds.cloudstack.domain.LoadBalancerRule;
import org.jclouds.cloudstack.domain.User;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

/**
 * Configures the cloudstack parsers.
 * 
 * @author Adrian Cole
 */
public class CloudStackParserModule extends AbstractModule {

   @Singleton
   public static class LoadBalancerRuleAdapter implements JsonSerializer<LoadBalancerRule>, JsonDeserializer<LoadBalancerRule> {

      public JsonElement serialize(LoadBalancerRule src, Type typeOfSrc, JsonSerializationContext context) {
         return context.serialize(src);
      }

      public LoadBalancerRule deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
         return apply(context.<LoadBalancerRuleInternal> deserialize(json, LoadBalancerRuleInternal.class));
      }

      public LoadBalancerRule apply(LoadBalancerRuleInternal in) {
         Set<String> cidrSet = Sets.newHashSet(in.CIDRs.split(","));
         return LoadBalancerRule.builder().id(in.id).account(in.account).algorithm(in.algorithm)
            .description(in.description).domain(in.domain).domainId(in.domainId).name(in.name)
            .privatePort(in.privatePort).publicIP(in.publicIP).publicIPId(in.publicIPId)
            .publicPort(in.publicPort).state(in.state).CIDRs(cidrSet).zoneId(in.zoneId).build();
      }

      static final class LoadBalancerRuleInternal {
         private long id;
         private String account;
         private LoadBalancerRule.Algorithm algorithm;
         private String description;
         private String domain;
         @SerializedName("domainid")
         private long domainId;
         private String name;
         @SerializedName("privateport")
         private int privatePort;
         @SerializedName("publicip")
         private String publicIP;
         @SerializedName("publicipid")
         private long publicIPId;
         @SerializedName("publicport")
         private int publicPort;
         private LoadBalancerRule.State state;
         @SerializedName("cidrlist")
         private String CIDRs;
         @SerializedName("zoneId")
         private long zoneId;
      }
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
         return Account.builder().id(in.id).type(in.type).domain(in.domain).domainId(in.domainId)
               .IPsAvailable(nullIfUnlimited(in.IPsAvailable)).IPLimit(nullIfUnlimited(in.IPLimit)).IPs(in.IPs)
               .cleanupRequired(in.cleanupRequired).name(in.name).receivedBytes(in.receivedBytes)
               .sentBytes(in.sentBytes).snapshotsAvailable(nullIfUnlimited(in.snapshotsAvailable))
               .snapshotLimit(nullIfUnlimited(in.snapshotLimit)).snapshots(in.snapshots).state(in.state)
               .templatesAvailable(nullIfUnlimited(in.templatesAvailable))
               .templateLimit(nullIfUnlimited(in.templateLimit)).templates(in.templates)
               .VMsAvailable(nullIfUnlimited(in.VMsAvailable)).VMLimit(nullIfUnlimited(in.VMLimit))
               .VMsRunning(in.VMsRunning).VMsStopped(in.VMsStopped).VMs(in.VMs)
               .volumesAvailable(nullIfUnlimited(in.volumesAvailable)).volumeLimit(nullIfUnlimited(in.volumeLimit))
               .volumes(in.volumes).users(in.users).build();
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
         @SerializedName("sentbytes")
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
      bind(new TypeLiteral<Map<Type, Object>>() {
      }).toInstance(ImmutableMap.<Type, Object> of(Account.class, new BreakGenericSetAdapter(), LoadBalancerRule.class, new LoadBalancerRuleAdapter()));
   }

}
