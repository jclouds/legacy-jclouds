/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.cloudstack.domain;

import static com.google.common.base.Preconditions.checkArgument;

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
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((CIDR == null) ? 0 : CIDR.hashCode());
      result = prime * result + ICMPCode;
      result = prime * result + ICMPType;
      result = prime * result + ((account == null) ? 0 : account.hashCode());
      result = prime * result + endPort;
      result = prime * result + (int) (id ^ (id >>> 32));
      result = prime * result + ((protocol == null) ? 0 : protocol.hashCode());
      result = prime * result + ((securityGroupName == null) ? 0 : securityGroupName.hashCode());
      result = prime * result + startPort;
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
      IngressRule other = (IngressRule) obj;
      if (CIDR == null) {
         if (other.CIDR != null)
            return false;
      } else if (!CIDR.equals(other.CIDR))
         return false;
      if (ICMPCode != other.ICMPCode)
         return false;
      if (ICMPType != other.ICMPType)
         return false;
      if (account == null) {
         if (other.account != null)
            return false;
      } else if (!account.equals(other.account))
         return false;
      if (endPort != other.endPort)
         return false;
      if (id != other.id)
         return false;
      if (protocol == null) {
         if (other.protocol != null)
            return false;
      } else if (!protocol.equals(other.protocol))
         return false;
      if (securityGroupName == null) {
         if (other.securityGroupName != null)
            return false;
      } else if (!securityGroupName.equals(other.securityGroupName))
         return false;
      if (startPort != other.startPort)
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[id=" + id + ", securityGroupName=" + securityGroupName + ", account=" + account + ", startPort="
               + startPort + ", endPort=" + endPort + ", protocol=" + protocol + ", CIDR=" + CIDR + ", ICMPCode="
               + ICMPCode + ", ICMPType=" + ICMPType + "]";
   }

   @Override
   public int compareTo(IngressRule arg0) {
      return new Long(id).compareTo(arg0.getId());
   }
}
