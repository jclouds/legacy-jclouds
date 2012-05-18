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

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.Objects;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Adrian Cole
 */
public class IngressRule implements Comparable<IngressRule> {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String account;
      private String CIDR;
      private int endPort = -1;
      private int ICMPCode = -1;
      private int ICMPType = -1;
      private String protocol;
      private long id = -1;
      private String securityGroupName;
      private int startPort = -1;

      public Builder account(String account) {
         this.account = account;
         return this;
      }

      public Builder CIDR(String CIDR) {
         this.CIDR = CIDR;
         return this;
      }

      public Builder endPort(int endPort) {
         this.endPort = endPort;
         return this;
      }

      public Builder ICMPCode(int ICMPCode) {
         this.ICMPCode = ICMPCode;
         return this;
      }

      public Builder ICMPType(int ICMPType) {
         this.ICMPType = ICMPType;
         return this;
      }

      public Builder protocol(String protocol) {
         this.protocol = protocol;
         return this;
      }

      public Builder id(long id) {
         this.id = id;
         return this;
      }

      public Builder securityGroupName(String securityGroupName) {
         this.securityGroupName = securityGroupName;
         return this;
      }

      public Builder startPort(int startPort) {
         this.startPort = startPort;
         return this;
      }

      public IngressRule build() {
         return new IngressRule(account, CIDR, endPort, ICMPCode, ICMPType, protocol, id, securityGroupName, startPort);
      }
   }

   private String account;
   @SerializedName("cidr")
   private String CIDR;
   @SerializedName("endport")
   private int endPort = -1;
   @SerializedName("icmpcode")
   private int ICMPCode = -1;
   @SerializedName("icmptype")
   private int ICMPType = -1;
   private String protocol;
   @SerializedName("ruleid")
   private long id = -1;
   @SerializedName("securitygroupname")
   private String securityGroupName;
   @SerializedName("startport")
   private int startPort = -1;

   // for serialization
   IngressRule() {

   }

   public IngressRule(String account, String CIDR, int endPort, int iCMPCode, int iCMPType, String protocol, long id,
         String securityGroupName, int startPort) {
      if (account == null)
         checkArgument(securityGroupName == null && CIDR != null,
               "if you do not specify an account and security group, you must specify a CIDR range");
      if (CIDR == null)
         checkArgument(account != null && securityGroupName != null,
               "if you do not specify an account and security group, you must specify a CIDR range");
      this.account = account;
      this.CIDR = CIDR;
      this.endPort = endPort;
      this.ICMPCode = iCMPCode;
      this.ICMPType = iCMPType;
      this.protocol = protocol;
      this.id = id;
      this.securityGroupName = securityGroupName;
      this.startPort = startPort;
   }

   /**
    * @return account owning the ingress rule
    */
   public String getAccount() {
      return account;
   }

   /**
    * @return the CIDR notation for the base IP address of the ingress rule
    */
   public String getCIDR() {
      return CIDR;
   }

   /**
    * @return the ending IP of the ingress rule
    */
   public int getEndPort() {
      return endPort;
   }

   /**
    * @return the code for the ICMP message response
    */
   public int getICMPCode() {
      return ICMPCode;
   }

   /**
    * @return the type of the ICMP message response
    */
   public int getICMPType() {
      return ICMPType;
   }

   /**
    * @return the protocol of the ingress rule
    */
   public String getProtocol() {
      return protocol;
   }

   /**
    * @return the id of the ingress rule
    */
   public long getId() {
      return id;
   }

   /**
    * @return security group name
    */
   public String getSecurityGroupName() {
      return securityGroupName;
   }

   /**
    * @return the starting IP of the ingress rule
    */
   public int getStartPort() {
      return startPort;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      IngressRule that = (IngressRule) o;

      if (!Objects.equal(CIDR, that.CIDR)) return false;
      if (!Objects.equal(ICMPCode, that.ICMPCode)) return false;
      if (!Objects.equal(ICMPType, that.ICMPType)) return false;
      if (!Objects.equal(account, that.account)) return false;
      if (!Objects.equal(endPort, that.endPort)) return false;
      if (!Objects.equal(id, that.id)) return false;
      if (!Objects.equal(protocol, that.protocol)) return false;
      if (!Objects.equal(securityGroupName, that.securityGroupName)) return false;
      if (!Objects.equal(startPort, that.startPort)) return false;

      return true;
   }

   @Override
   public int hashCode() {
       return Objects.hashCode(CIDR, ICMPCode, ICMPType, account, endPort, id, protocol, securityGroupName, startPort);
   }

   @Override
   public String toString() {
      return "IngressRule{" +
            "account='" + account + '\'' +
            ", CIDR='" + CIDR + '\'' +
            ", endPort=" + endPort +
            ", ICMPCode=" + ICMPCode +
            ", ICMPType=" + ICMPType +
            ", protocol='" + protocol + '\'' +
            ", id=" + id +
            ", securityGroupName='" + securityGroupName + '\'' +
            ", startPort=" + startPort +
            '}';
   }

   @Override
   public int compareTo(IngressRule arg0) {
      return new Long(id).compareTo(arg0.getId());
   }
}
