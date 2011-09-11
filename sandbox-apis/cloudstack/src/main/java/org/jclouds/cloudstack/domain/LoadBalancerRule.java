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

import com.google.common.base.CaseFormat;
import com.google.gson.annotations.SerializedName;

/**
 * 
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
      private long id;
      private String account;
      private Algorithm algorithm;
      private String description;
      private String domain;
      private long domainId;
      private String name;
      private int privatePort;
      private String publicIP;
      private long publicIPId;
      private int publicPort;
      private State state;

      public Builder id(long id) {
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

      public Builder domainId(long domainId) {
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

      public Builder publicIPId(long publicIPId) {
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

      public LoadBalancerRule build() {
         return new LoadBalancerRule(id, account, algorithm, description, domain, domainId, name, privatePort,
                  publicIP, publicIPId, publicPort, state);
      }
   }

   private long id;
   private String account;
   private Algorithm algorithm;
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
   private State state;

   // for deserializer
   LoadBalancerRule() {

   }

   public LoadBalancerRule(long id, String account, Algorithm algorithm, String description, String domain,
            long domainId, String name, int privatePort, String publicIP, long publicIPId, int publicPort, State state) {
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
   }

   /**
    * @return the load balancer rule ID
    */
   public long getId() {
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
   public long getDomainId() {
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
   public long getPublicIPId() {
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

   @Override
   public int compareTo(LoadBalancerRule arg0) {
      return new Long(id).compareTo(arg0.getId());
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((account == null) ? 0 : account.hashCode());
      result = prime * result + ((algorithm == null) ? 0 : algorithm.hashCode());
      result = prime * result + ((description == null) ? 0 : description.hashCode());
      result = prime * result + ((domain == null) ? 0 : domain.hashCode());
      result = prime * result + (int) (domainId ^ (domainId >>> 32));
      result = prime * result + (int) (id ^ (id >>> 32));
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + privatePort;
      result = prime * result + ((publicIP == null) ? 0 : publicIP.hashCode());
      result = prime * result + (int) (publicIPId ^ (publicIPId >>> 32));
      result = prime * result + publicPort;
      result = prime * result + ((state == null) ? 0 : state.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      LoadBalancerRule other = (LoadBalancerRule) obj;
      if (account == null) {
         if (other.account != null)
            return false;
      } else if (!account.equals(other.account))
         return false;
      if (algorithm == null) {
         if (other.algorithm != null)
            return false;
      } else if (!algorithm.equals(other.algorithm))
         return false;
      if (description == null) {
         if (other.description != null)
            return false;
      } else if (!description.equals(other.description))
         return false;
      if (domain == null) {
         if (other.domain != null)
            return false;
      } else if (!domain.equals(other.domain))
         return false;
      if (domainId != other.domainId)
         return false;
      if (id != other.id)
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (privatePort != other.privatePort)
         return false;
      if (publicIP == null) {
         if (other.publicIP != null)
            return false;
      } else if (!publicIP.equals(other.publicIP))
         return false;
      if (publicIPId != other.publicIPId)
         return false;
      if (publicPort != other.publicPort)
         return false;
      if (state == null) {
         if (other.state != null)
            return false;
      } else if (!state.equals(other.state))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[account=" + account + ", algorithm=" + algorithm + ", description=" + description + ", domain=" + domain
               + ", domainId=" + domainId + ", id=" + id + ", name=" + name + ", privatePort=" + privatePort
               + ", publicIP=" + publicIP + ", publicIPId=" + publicIPId + ", publicPort=" + publicPort + ", state="
               + state + "]";
   }

}
