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
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.inject.Singleton;

import org.jclouds.cloudstack.domain.Account;
import org.jclouds.cloudstack.domain.FirewallRule;
import org.jclouds.cloudstack.domain.LoadBalancerRule;
import org.jclouds.cloudstack.domain.PortForwardingRule;
import org.jclouds.cloudstack.domain.User;
import org.jclouds.cloudstack.domain.Account.State;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
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
 * @author Adrian Cole, Andrei Savu
 */
public class CloudStackParserModule extends AbstractModule {

   @Singleton
   public static class PortForwardingRuleAdapter implements JsonSerializer<PortForwardingRule>, JsonDeserializer<PortForwardingRule> {

      public JsonElement serialize(PortForwardingRule src, Type typeOfSrc, JsonSerializationContext context) {
         return context.serialize(src);
      }

      public PortForwardingRule deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
         throws JsonParseException {
         return apply(context.<PortForwardingRuleInternal>deserialize(json, PortForwardingRuleInternal.class));
      }

      public PortForwardingRule apply(PortForwardingRuleInternal in) {
         Set<String> cidrSet;
         if (in.CIDRs != null) {
            String[] elements = in.CIDRs.split(",");
            cidrSet = Sets.newTreeSet(Arrays.asList(elements));
         } else {
            cidrSet = Collections.emptySet();
         }
         return PortForwardingRule.builder().id(in.id).IPAddress(in.IPAddress).IPAddressId(in.IPAddressId)
            .privatePort(in.privatePort).protocol(in.protocol).publicPort(in.publicPort).state(in.state)
            .virtualMachineDisplayName(in.virtualMachineDisplayName).virtualMachineId(in.virtualMachineId)
            .virtualMachineName(in.virtualMachineName).CIDRs(cidrSet).privateEndPort(in.privateEndPort)
            .publicEndPort(in.publicEndPort).build();
      }

      static final class PortForwardingRuleInternal {
         private String id;
         @SerializedName("ipaddress")
         private String IPAddress;
         @SerializedName("ipaddressid")
         private String IPAddressId;
         @SerializedName("privateport")
         private int privatePort;
         private PortForwardingRule.Protocol protocol;
         @SerializedName("publicport")
         public int publicPort;
         private PortForwardingRule.State state;
         @SerializedName("virtualmachinedisplayname")
         private String virtualMachineDisplayName;
         @SerializedName("virtualmachineid")
         public String virtualMachineId;
         @SerializedName("virtualmachinename")
         private String virtualMachineName;
         @SerializedName("cidrlist")
         private String CIDRs;
         @SerializedName("privateendport")
         private int privateEndPort;
         @SerializedName("publicendport")
         private int publicEndPort;
      }
   }

   @Singleton
   public static class FirewallRuleAdapter implements JsonSerializer<FirewallRule>, JsonDeserializer<FirewallRule> {

      public JsonElement serialize(FirewallRule src, Type typeOfSrc, JsonSerializationContext context) {
         return context.serialize(src);
      }

      public FirewallRule deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
         throws JsonParseException {
         return apply(context.<FirewallRuleInternal>deserialize(json, FirewallRuleInternal.class));
      }

      public FirewallRule apply(FirewallRuleInternal in) {
         Set<String> cidrSet;
         if (in.CIDRs != null) {
            String[] elements = in.CIDRs.split(",");
            cidrSet = Sets.newTreeSet(Arrays.asList(elements));
         } else {
            cidrSet = Collections.emptySet();
         }
         return FirewallRule.builder().id(in.id).CIDRs(cidrSet).startPort(in.startPort).endPort(in.endPort)
            .icmpCode(in.icmpCode).icmpType(in.icmpType).ipAddress(in.ipAddress).ipAddressId(in.ipAddressId)
            .protocol(in.protocol).state(in.state).build();
      }

      static final class FirewallRuleInternal {
         private String id;
         @SerializedName("cidrlist")
         private String CIDRs;
         @SerializedName("startport")
         private int startPort;
         @SerializedName("endport")
         private int endPort;
         @SerializedName("icmpcode")
         private String icmpCode;
         @SerializedName("icmptype")
         private String icmpType;
         @SerializedName("ipaddress")
         private String ipAddress;
         @SerializedName("ipaddressid")
         private String ipAddressId;
         private FirewallRule.Protocol protocol;
         private FirewallRule.State state;
      }
   }

   @Singleton
   public static class LoadBalancerRuleAdapter implements JsonSerializer<LoadBalancerRule>, JsonDeserializer<LoadBalancerRule> {

      public JsonElement serialize(LoadBalancerRule src, Type typeOfSrc, JsonSerializationContext context) {
         return context.serialize(src);
      }

      public LoadBalancerRule deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
         throws JsonParseException {
         return apply(context.<LoadBalancerRuleInternal>deserialize(json, LoadBalancerRuleInternal.class));
      }

      public LoadBalancerRule apply(LoadBalancerRuleInternal in) {
         Set<String> cidrSet = Sets.newHashSet(in.CIDRs.split(","));
         return LoadBalancerRule.builder().id(in.id).account(in.account).algorithm(in.algorithm)
            .description(in.description).domain(in.domain).domainId(in.domainId).name(in.name)
            .privatePort(in.privatePort).publicIP(in.publicIP).publicIPId(in.publicIPId)
            .publicPort(in.publicPort).state(in.state).CIDRs(cidrSet).zoneId(in.zoneId).build();
      }

      static final class LoadBalancerRuleInternal {
         private String id;
         private String account;
         private LoadBalancerRule.Algorithm algorithm;
         private String description;
         private String domain;
         @SerializedName("domainid")
         private String domainId;
         private String name;
         @SerializedName("privateport")
         private int privatePort;
         @SerializedName("publicip")
         private String publicIP;
         @SerializedName("publicipid")
         private String publicIPId;
         @SerializedName("publicport")
         private int publicPort;
         private LoadBalancerRule.State state;
         @SerializedName("cidrlist")
         private String CIDRs;
         @SerializedName("zoneId")
         private String zoneId;
      }
   }

   @Singleton
   public static class BreakGenericSetAdapter implements JsonSerializer<Account>, JsonDeserializer<Account> {

      public JsonElement serialize(Account src, Type typeOfSrc, JsonSerializationContext context) {
         return context.serialize(src);
      }

      public Account deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
         throws JsonParseException {
         return apply(context.<AccountInternal>deserialize(json, AccountInternal.class));
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
         private String id;
         @SerializedName("accounttype")
         private Account.Type type;
         private String domain;
         @SerializedName("domainid")
         private String domainId;
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
      }).toInstance(ImmutableMap.<Type, Object>of(
         Account.class, new BreakGenericSetAdapter(),
         LoadBalancerRule.class, new LoadBalancerRuleAdapter(),
         PortForwardingRule.class, new PortForwardingRuleAdapter(),
         FirewallRule.class, new FirewallRuleAdapter()
      ));
   }

}
