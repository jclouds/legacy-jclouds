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
package org.jclouds.cloudstack.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import com.google.common.base.CaseFormat;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.gson.annotations.SerializedName;

/**
 * @author Adrian Cole
 */
public class LoadBalancerRule implements Comparable<LoadBalancerRule> {
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

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String id;
      private String account;
      private Algorithm algorithm;
      private String description;
      private String domain;
      private String domainId;
      private String name;
      private int privatePort;
      private String publicIP;
      private String publicIPId;
      private int publicPort;
      private State state;
      private Set<String> CIDRs = ImmutableSet.of();
      private String zoneId;

      public Builder id(String id) {
         this.id = id;
         return this;
      }

      public Builder account(String account) {
         this.account = account;
         return this;
      }

      public Builder algorithm(Algorithm algorithm) {
         this.algorithm = algorithm;
         return this;
      }

      public Builder description(String description) {
         this.description = description;
         return this;
      }

      public Builder domain(String domain) {
         this.domain = domain;
         return this;
      }

      public Builder domainId(String domainId) {
         this.domainId = domainId;
         return this;
      }

      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public Builder privatePort(int privatePort) {
         this.privatePort = privatePort;
         return this;
      }

      public Builder publicIP(String publicIP) {
         this.publicIP = publicIP;
         return this;
      }

      public Builder publicIPId(String publicIPId) {
         this.publicIPId = publicIPId;
         return this;
      }

      public Builder publicPort(int publicPort) {
         this.publicPort = publicPort;
         return this;
      }

      public Builder state(State state) {
         this.state = state;
         return this;
      }

      public Builder CIDRs(Set<String> CIDRs) {
         this.CIDRs = CIDRs;
         return this;
      }

      public Builder zoneId(String zoneId) {
         this.zoneId = zoneId;
         return this;
      }

      public LoadBalancerRule build() {
         return new LoadBalancerRule(id, account, algorithm, description, domain, domainId, name, privatePort,
               publicIP, publicIPId, publicPort, state, zoneId, CIDRs);
      }
   }

   private String id;
   private String account;
   private Algorithm algorithm;
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
   private State state;
   @SerializedName("cidrlist")
   private Set<String> CIDRs;
   @SerializedName("zoneId")
   private String zoneId;

   // for deserializer
   LoadBalancerRule() {

   }

   public LoadBalancerRule(String id, String account, Algorithm algorithm, String description, String domain,
                           String domainId, String name, int privatePort, String publicIP, String publicIPId, int publicPort, State state,
                           String zoneId, Set<String> CIDRs) {
      this.id = id;
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
      this.zoneId = zoneId;
      this.CIDRs = ImmutableSet.copyOf(CIDRs);

   }

   /**
    * @return the load balancer rule ID
    */
   public String getId() {
      return id;
   }

   /**
    * @return the account of the load balancer rule
    */
   public String getAccount() {
      return account;
   }

   /**
    * @return the load balancer algorithm (source, roundrobin, leastconn)
    */
   public Algorithm getAlgorithm() {
      return algorithm;
   }

   /**
    * @return the description of the load balancer
    */
   public String getDescription() {
      return description;
   }

   /**
    * @return the domain of the load balancer rule
    */
   public String getDomain() {
      return domain;
   }

   /**
    * @return the domain ID of the load balancer rule
    */
   public String getDomainId() {
      return domainId;
   }

   /**
    * @return the name of the load balancer
    */
   public String getName() {
      return name;
   }

   /**
    * @return the private port
    */
   public int getPrivatePort() {
      return privatePort;
   }

   /**
    * @return the public ip address
    */
   public String getPublicIP() {
      return publicIP;
   }

   /**
    * @return the public ip address id
    */
   public String getPublicIPId() {
      return publicIPId;
   }

   /**
    * @return the public port
    */
   public int getPublicPort() {
      return publicPort;
   }

   /**
    * @return the state of the rule
    */
   public State getState() {
      return state;
   }

   /**
    * @return the cidr list to forward traffic from
    */
   public Set<String> getCIDRs() {
      return CIDRs;
   }

   /**
    * @return the id of the zone the rule beStrings to
    */
   public String getZoneId() {
      return zoneId;
   }

   @Override
   public int compareTo(LoadBalancerRule arg0) {
      return id.compareTo(arg0.getId());
   }


   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      LoadBalancerRule that = (LoadBalancerRule) o;

      if (!Objects.equal(account, that.account)) return false;
      if (!Objects.equal(algorithm, that.algorithm)) return false;
      if (!Objects.equal(description, that.description)) return false;
      if (!Objects.equal(domain, that.domain)) return false;
      if (!Objects.equal(domainId, that.domainId)) return false;
      if (!Objects.equal(id, that.id)) return false;
      if (!Objects.equal(name, that.name)) return false;
      if (!Objects.equal(privatePort, that.privatePort)) return false;
      if (!Objects.equal(publicIP, that.publicIP)) return false;
      if (!Objects.equal(publicIPId, that.publicIPId)) return false;
      if (!Objects.equal(publicPort, that.publicPort)) return false;
      if (!Objects.equal(zoneId, that.zoneId)) return false;
      if (!Objects.equal(state, that.state)) return false;

      return true;
   }

   @Override
   public int hashCode() {
       return Objects.hashCode(account, algorithm, description, domain, domainId, id, name, privatePort, publicIP, publicIPId, publicPort, zoneId, state);
   }

   @Override
   public String toString() {
      return "LoadBalancerRule{" +
            "id=" + id +
            ", account='" + account + '\'' +
            ", algorithm=" + algorithm +
            ", description='" + description + '\'' +
            ", domain='" + domain + '\'' +
            ", domainId=" + domainId +
            ", name='" + name + '\'' +
            ", privatePort=" + privatePort +
            ", publicIP='" + publicIP + '\'' +
            ", publicIPId=" + publicIPId +
            ", publicPort=" + publicPort +
            ", state=" + state +
            ", CIDRs=" + CIDRs +
            ", zoneId=" + zoneId +
            '}';
   }

}
