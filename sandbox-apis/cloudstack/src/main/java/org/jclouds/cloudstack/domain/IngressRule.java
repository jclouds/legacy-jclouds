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

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Adrian Cole
 */
public class IngressRule {
   private String account;
   @SerializedName("cidr")
   private String CIDR;
   @SerializedName("endport")
   private int endPort;
   @SerializedName("icmpcode")
   private int ICMPCode;
   @SerializedName("icmptype")
   private int ICMPType;
   private String protocol;
   @SerializedName("ruleid")
   private long id;
   @SerializedName("securitygroupname")
   private String securityGroupName;
   @SerializedName("startport")
   private int startPort;

   // for serialization
   IngressRule() {

   }

   public IngressRule(String account, String cIDR, int endPort, int iCMPCode, int iCMPType, String protocol, long id,
         String securityGroupName, int startPort) {
      this.account = account;
      this.CIDR = cIDR;
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

}
