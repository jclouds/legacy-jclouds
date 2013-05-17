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

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * @author Adrian Cole
 */
public class IngressRule implements Comparable<IngressRule> {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromIngressRule(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String account;
      protected String CIDR;
      protected int endPort;
      protected int ICMPCode;
      protected int ICMPType;
      protected String protocol;
      protected String id;
      protected String securityGroupName;
      protected int startPort;

      /**
       * @see IngressRule#getAccount()
       */
      public T account(String account) {
         this.account = account;
         return self();
      }

      /**
       * @see IngressRule#getCIDR()
       */
      public T CIDR(String CIDR) {
         this.CIDR = CIDR;
         return self();
      }

      /**
       * @see IngressRule#getEndPort()
       */
      public T endPort(int endPort) {
         this.endPort = endPort;
         return self();
      }

      /**
       * @see IngressRule#getICMPCode()
       */
      public T ICMPCode(int ICMPCode) {
         this.ICMPCode = ICMPCode;
         return self();
      }

      /**
       * @see IngressRule#getICMPType()
       */
      public T ICMPType(int ICMPType) {
         this.ICMPType = ICMPType;
         return self();
      }

      /**
       * @see IngressRule#getProtocol()
       */
      public T protocol(String protocol) {
         this.protocol = protocol;
         return self();
      }

      /**
       * @see IngressRule#getId()
       */
      public T id(String id) {
         this.id = id;
         return self();
      }

      /**
       * @see IngressRule#getSecurityGroupName()
       */
      public T securityGroupName(String securityGroupName) {
         this.securityGroupName = securityGroupName;
         return self();
      }

      /**
       * @see IngressRule#getStartPort()
       */
      public T startPort(int startPort) {
         this.startPort = startPort;
         return self();
      }

      public IngressRule build() {
         return new IngressRule(account, CIDR, endPort, ICMPCode, ICMPType, protocol, id, securityGroupName, startPort);
      }

      public T fromIngressRule(IngressRule in) {
         return this
               .account(in.getAccount())
               .CIDR(in.getCIDR())
               .endPort(in.getEndPort())
               .ICMPCode(in.getICMPCode())
               .ICMPType(in.getICMPType())
               .protocol(in.getProtocol())
               .id(in.getId())
               .securityGroupName(in.getSecurityGroupName())
               .startPort(in.getStartPort());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String account;
   private final String CIDR;
   private final int endPort;
   private final int ICMPCode;
   private final int ICMPType;
   private final String protocol;
   private final String id;
   private final String securityGroupName;
   private final int startPort;

   @ConstructorProperties({
         "account", "cidr", "endport", "icmpcode", "icmptype", "protocol", "ruleid", "securitygroupname", "startport"
   })
   protected IngressRule(@Nullable String account, @Nullable String CIDR, int endPort, int ICMPCode, int ICMPType,
                         @Nullable String protocol, String id, @Nullable String securityGroupName, int startPort) {
      this.account = account;
      this.CIDR = CIDR;
      this.endPort = endPort;
      this.ICMPCode = ICMPCode;
      this.ICMPType = ICMPType;
      this.protocol = protocol;
      this.id = checkNotNull(id, "id");
      this.securityGroupName = securityGroupName;
      this.startPort = startPort;
   }

   /**
    * @return account owning the ingress rule
    */
   @Nullable
   public String getAccount() {
      return this.account;
   }

   /**
    * @return the CIDR notation for the base IP address of the ingress rule
    */
   @Nullable
   public String getCIDR() {
      return this.CIDR;
   }

   /**
    * @return the ending IP of the ingress rule
    */
   public int getEndPort() {
      return this.endPort;
   }

   /**
    * @return the code for the ICMP message response
    */
   public int getICMPCode() {
      return this.ICMPCode;
   }

   /**
    * @return the type of the ICMP message response
    */
   public int getICMPType() {
      return this.ICMPType;
   }

   /**
    * @return the protocol of the ingress rule
    */
   @Nullable
   public String getProtocol() {
      return this.protocol;
   }

   /**
    * @return the id of the ingress rule
    */
   public String getId() {
      return this.id;
   }

   /**
    * @return security group name
    */
   @Nullable
   public String getSecurityGroupName() {
      return this.securityGroupName;
   }

   /**
    * @return the starting IP of the ingress rule
    */
   public int getStartPort() {
      return this.startPort;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(account, CIDR, endPort, ICMPCode, ICMPType, protocol, id, securityGroupName, startPort);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      IngressRule that = IngressRule.class.cast(obj);
      return Objects.equal(this.account, that.account)
            && Objects.equal(this.CIDR, that.CIDR)
            && Objects.equal(this.endPort, that.endPort)
            && Objects.equal(this.ICMPCode, that.ICMPCode)
            && Objects.equal(this.ICMPType, that.ICMPType)
            && Objects.equal(this.protocol, that.protocol)
            && Objects.equal(this.id, that.id)
            && Objects.equal(this.securityGroupName, that.securityGroupName)
            && Objects.equal(this.startPort, that.startPort);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("account", account).add("CIDR", CIDR).add("endPort", endPort).add("ICMPCode", ICMPCode)
            .add("ICMPType", ICMPType).add("protocol", protocol).add("id", id).add("securityGroupName", securityGroupName).add("startPort", startPort);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   @Override
   public int compareTo(IngressRule o) {
      return id.compareTo(o.getId());
   }
}
