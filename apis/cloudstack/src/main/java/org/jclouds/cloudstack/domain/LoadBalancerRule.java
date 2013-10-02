/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.cloudstack.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.CaseFormat;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;

/**
 * Class LoadBalancerRule
 *
 * @author Adrian Cole
 */
public class LoadBalancerRule {

   /**
    */
   public static enum State {
      ADD, ACTIVE, UNRECOGNIZED;

      @Override
      public String toString() {
         return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name());
      }

      public static State fromValue(String state) {
         try {
            return valueOf(CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, checkNotNull(state, "state")));
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }

   }

   public static enum Algorithm {
      SOURCE, ROUNDROBIN, LEASTCONN, UNRECOGNIZED;

      @Override
      public String toString() {
         return name().toLowerCase();
      }

      public static Algorithm fromValue(String algorithm) {
         try {
            return Algorithm.valueOf(checkNotNull(algorithm, "algorithm").toUpperCase());
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }

   }

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromLoadBalancerRule(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String id;
      protected String account;
      protected LoadBalancerRule.Algorithm algorithm;
      protected String description;
      protected String domain;
      protected String domainId;
      protected String name;
      protected int privatePort;
      protected String publicIP;
      protected String publicIPId;
      protected int publicPort;
      protected LoadBalancerRule.State state;
      protected Set<String> CIDRs = ImmutableSet.of();
      protected String zoneId;

      /**
       * @see LoadBalancerRule#getId()
       */
      public T id(String id) {
         this.id = id;
         return self();
      }

      /**
       * @see LoadBalancerRule#getAccount()
       */
      public T account(String account) {
         this.account = account;
         return self();
      }

      /**
       * @see LoadBalancerRule#getAlgorithm()
       */
      public T algorithm(LoadBalancerRule.Algorithm algorithm) {
         this.algorithm = algorithm;
         return self();
      }

      /**
       * @see LoadBalancerRule#getDescription()
       */
      public T description(String description) {
         this.description = description;
         return self();
      }

      /**
       * @see LoadBalancerRule#getDomain()
       */
      public T domain(String domain) {
         this.domain = domain;
         return self();
      }

      /**
       * @see LoadBalancerRule#getDomainId()
       */
      public T domainId(String domainId) {
         this.domainId = domainId;
         return self();
      }

      /**
       * @see LoadBalancerRule#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see LoadBalancerRule#getPrivatePort()
       */
      public T privatePort(int privatePort) {
         this.privatePort = privatePort;
         return self();
      }

      /**
       * @see LoadBalancerRule#getPublicIP()
       */
      public T publicIP(String publicIP) {
         this.publicIP = publicIP;
         return self();
      }

      /**
       * @see LoadBalancerRule#getPublicIPId()
       */
      public T publicIPId(String publicIPId) {
         this.publicIPId = publicIPId;
         return self();
      }

      /**
       * @see LoadBalancerRule#getPublicPort()
       */
      public T publicPort(int publicPort) {
         this.publicPort = publicPort;
         return self();
      }

      /**
       * @see LoadBalancerRule#getState()
       */
      public T state(LoadBalancerRule.State state) {
         this.state = state;
         return self();
      }

      /**
       * @see LoadBalancerRule#getCIDRs()
       */
      public T CIDRs(Set<String> CIDRs) {
         this.CIDRs = ImmutableSet.copyOf(checkNotNull(CIDRs, "CIDRs"));
         return self();
      }

      public T CIDRs(String... in) {
         return CIDRs(ImmutableSet.copyOf(in));
      }

      /**
       * @see LoadBalancerRule#getZoneId()
       */
      public T zoneId(String zoneId) {
         this.zoneId = zoneId;
         return self();
      }

      public LoadBalancerRule build() {
         return new LoadBalancerRule(id, account, algorithm, description, domain, domainId, name, privatePort, publicIP, publicIPId, publicPort, state, CIDRs, zoneId);
      }

      public T fromLoadBalancerRule(LoadBalancerRule in) {
         return this
               .id(in.getId())
               .account(in.getAccount())
               .algorithm(in.getAlgorithm())
               .description(in.getDescription())
               .domain(in.getDomain())
               .domainId(in.getDomainId())
               .name(in.getName())
               .privatePort(in.getPrivatePort())
               .publicIP(in.getPublicIP())
               .publicIPId(in.getPublicIPId())
               .publicPort(in.getPublicPort())
               .state(in.getState())
               .CIDRs(in.getCIDRs())
               .zoneId(in.getZoneId());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String id;
   private final String account;
   private final LoadBalancerRule.Algorithm algorithm;
   private final String description;
   private final String domain;
   private final String domainId;
   private final String name;
   private final int privatePort;
   private final String publicIP;
   private final String publicIPId;
   private final int publicPort;
   private final LoadBalancerRule.State state;
   private final Set<String> CIDRs;
   private final String zoneId;


   @ConstructorProperties({
         "id", "account", "algorithm", "description", "domain", "domainid", "name", "privateport", "publicip",
         "publicipid", "publicport", "state", "cidrlist", "zoneId"
   })
   private LoadBalancerRule(String id, @Nullable String account, @Nullable Algorithm algorithm,
                            @Nullable String description, @Nullable String domain, @Nullable String domainId,
                            @Nullable String name, int privatePort, @Nullable String publicIP,
                            @Nullable String publicIPId, int publicPort, @Nullable State state,
                            @Nullable String CIDRs, @Nullable String zoneId) {
      this(id, account, algorithm, description, domain, domainId, name, privatePort, publicIP, publicIPId, publicPort, state,
            splitStringOnCommas(CIDRs), zoneId);
   }

   private static Set<String> splitStringOnCommas(String in) {
      return in == null ? ImmutableSet.<String>of() : ImmutableSet.copyOf(in.split(","));
   }

   protected LoadBalancerRule(String id, @Nullable String account, @Nullable LoadBalancerRule.Algorithm algorithm,
                              @Nullable String description, @Nullable String domain, @Nullable String domainId, @Nullable String name,
                              int privatePort, @Nullable String publicIP, @Nullable String publicIPId, int publicPort,
                              @Nullable LoadBalancerRule.State state, @Nullable Iterable<String> CIDRs, @Nullable String zoneId) {
      this.id = checkNotNull(id, "id");
      this.account = account;
      this.algorithm = algorithm;
      this.description = description;
      this.domain = domain;
      this.domainId = domainId;
      this.name = name;
      this.privatePort = privatePort;
      this.publicIP = publicIP;
      this.publicIPId = publicIPId;
      this.publicPort = publicPort;
      this.state = state;
      this.CIDRs = CIDRs == null ? ImmutableSet.<String>of() : ImmutableSet.copyOf(CIDRs);
      this.zoneId = zoneId;
   }

   /**
    * @return the load balancer rule ID
    */
   public String getId() {
      return this.id;
   }

   /**
    * @return the account of the load balancer rule
    */
   @Nullable
   public String getAccount() {
      return this.account;
   }

   /**
    * @return the load balancer algorithm (source, roundrobin, leastconn)
    */
   @Nullable
   public LoadBalancerRule.Algorithm getAlgorithm() {
      return this.algorithm;
   }

   /**
    * @return the description of the load balancer
    */
   @Nullable
   public String getDescription() {
      return this.description;
   }

   /**
    * @return the domain of the load balancer rule
    */
   @Nullable
   public String getDomain() {
      return this.domain;
   }

   /**
    * @return the domain ID of the load balancer rule
    */
   @Nullable
   public String getDomainId() {
      return this.domainId;
   }

   /**
    * @return the name of the load balancer
    */
   @Nullable
   public String getName() {
      return this.name;
   }

   /**
    * @return the private port
    */
   public int getPrivatePort() {
      return this.privatePort;
   }

   /**
    * @return the public ip address
    */
   @Nullable
   public String getPublicIP() {
      return this.publicIP;
   }

   /**
    * @return the public ip address id
    */
   @Nullable
   public String getPublicIPId() {
      return this.publicIPId;
   }

   /**
    * @return the public port
    */
   public int getPublicPort() {
      return this.publicPort;
   }

   /**
    * @return the state of the rule
    */
   @Nullable
   public LoadBalancerRule.State getState() {
      return this.state;
   }

   /**
    * @return the cidr list to forward traffic from
    */
   public Set<String> getCIDRs() {
      return this.CIDRs;
   }

   /**
    * @return the id of the zone the rule beStrings to
    */
   @Nullable
   public String getZoneId() {
      return this.zoneId;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, account, algorithm, description, domain, domainId, name, privatePort, publicIP, publicIPId, publicPort, state, CIDRs, zoneId);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      LoadBalancerRule that = LoadBalancerRule.class.cast(obj);
      return Objects.equal(this.id, that.id)
            && Objects.equal(this.account, that.account)
            && Objects.equal(this.algorithm, that.algorithm)
            && Objects.equal(this.description, that.description)
            && Objects.equal(this.domain, that.domain)
            && Objects.equal(this.domainId, that.domainId)
            && Objects.equal(this.name, that.name)
            && Objects.equal(this.privatePort, that.privatePort)
            && Objects.equal(this.publicIP, that.publicIP)
            && Objects.equal(this.publicIPId, that.publicIPId)
            && Objects.equal(this.publicPort, that.publicPort)
            && Objects.equal(this.state, that.state)
            && Objects.equal(this.CIDRs, that.CIDRs)
            && Objects.equal(this.zoneId, that.zoneId);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("id", id).add("account", account).add("algorithm", algorithm).add("description", description).add("domain", domain).add("domainId", domainId).add("name", name).add("privatePort", privatePort).add("publicIP", publicIP).add("publicIPId", publicIPId).add("publicPort", publicPort).add("state", state).add("CIDRs", CIDRs).add("zoneId", zoneId);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
