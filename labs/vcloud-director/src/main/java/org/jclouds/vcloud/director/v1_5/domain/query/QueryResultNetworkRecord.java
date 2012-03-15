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

package org.jclouds.vcloud.director.v1_5.domain.query;

import static com.google.common.base.Objects.equal;

import javax.xml.bind.annotation.XmlAttribute;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Represents the results from a Network vCloud query as a record.
 *
 * <pre>
 * &lt;complexType name="QueryResultNetworkRecord" /&gt;
 * </pre>
 *
 * @author grkvlt@apache.org
 */
public class QueryResultNetworkRecord extends QueryResultRecordType {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return builder().fromQueryResultNetworkRecord(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public static class Builder<B extends Builder<B>> extends QueryResultRecordType.Builder<B> {

      private String name;
      private String ipScopeId;
      private String gateway;
      private String netmask;
      private String dns1;
      private String dns2;
      private String dnsSuffix;
      private Boolean isBusy;

      /**
       * @see QueryResultNetworkRecord#getName()
       */
      public B name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see QueryResultNetworkRecord#getIpScopeId()
       */
      public B ipScopeId(String ipScopeId) {
         this.ipScopeId = ipScopeId;
         return self();
      }

      /**
       * @see QueryResultNetworkRecord#getGateway()
       */
      public B gateway(String gateway) {
         this.gateway = gateway;
         return self();
      }

      /**
       * @see QueryResultNetworkRecord#getNetmask()
       */
      public B netmask(String netmask) {
         this.netmask = netmask;
         return self();
      }

      /**
       * @see QueryResultNetworkRecord#getDns1()
       */
      public B dns1(String dns1) {
         this.dns1 = dns1;
         return self();
      }

      /**
       * @see QueryResultNetworkRecord#getDns2()
       */
      public B dns2(String dns2) {
         this.dns2 = dns2;
         return self();
      }

      /**
       * @see QueryResultNetworkRecord#getDnsSuffix()
       */
      public B dnsSuffix(String dnsSuffix) {
         this.dnsSuffix = dnsSuffix;
         return self();
      }

      /**
       * @see QueryResultNetworkRecord#isBusy()
       */
      public B isBusy(Boolean isBusy) {
         this.isBusy = isBusy;
         return self();
      }

      /**
       * @see QueryResultNetworkRecord#isBusy()
       */
      public B busy() {
         this.isBusy = Boolean.TRUE;
         return self();
      }

      /**
       * @see QueryResultNetworkRecord#isBusy()
       */
      public B notBusy() {
         this.isBusy = Boolean.FALSE;
         return self();
      }

      @Override
      public QueryResultNetworkRecord build() {
         return new QueryResultNetworkRecord(this);
      }

      public B fromQueryResultNetworkRecord(QueryResultNetworkRecord in) {
         return fromQueryResultRecordType(in)
               .name(in.getName()).ipScopeId(in.getIpScopeId()).gateway(in.getGateway())
               .netmask(in.getNetmask()).dns1(in.getDns1()).dns2(in.getDns2())
               .dnsSuffix(in.getDnsSuffix()).isBusy(in.isBusy());
      }
   }

   public QueryResultNetworkRecord(Builder<?> builder) {
      super(builder);
      this.name = builder.name;
      this.ipScopeId = builder.ipScopeId;
      this.gateway = builder.gateway;
      this.netmask = builder.netmask;
      this.dns1 = builder.dns1;
      this.dns2 = builder.dns2;
      this.dnsSuffix = builder.dnsSuffix;
      isBusy = builder.isBusy;
   }

   private QueryResultNetworkRecord() {
      // Qfor JAXB
   }

   @XmlAttribute
   protected String name;
   @XmlAttribute
   protected String ipScopeId;
   @XmlAttribute
   protected String gateway;
   @XmlAttribute
   protected String netmask;
   @XmlAttribute
   protected String dns1;
   @XmlAttribute
   protected String dns2;
   @XmlAttribute
   protected String dnsSuffix;
   @XmlAttribute
   protected Boolean isBusy;

   /**
    * name.
    */
   public String getName() {
      return name;
   }

   /**
    * IP scope object of this network.
    */
   public String getIpScopeId() {
      return ipScopeId;
   }


   /**
    * Gateway for the network.
    */
   public String getGateway() {
      return gateway;
   }

   /**
    * Netmask for the network.
    */
   public String getNetmask() {
      return netmask;
   }

   /**
    * Primary DNS for the network.
    */
   public String getDns1() {
      return dns1;
   }

   /**
    * Secondary DNS for the network.
    */
   public String getDns2() {
      return dns2;
   }

   /**
    * DNS suffix for the network.
    */
   public String getDnsSuffix() {
      return dnsSuffix;
   }

   /**
    * Shows whether it is busy.
    */
   public Boolean isBusy() {
      return isBusy;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      QueryResultNetworkRecord that = QueryResultNetworkRecord.class.cast(o);
      return super.equals(that) && equal(name, that.name) && equal(ipScopeId, that.ipScopeId) && equal(gateway, that.gateway) && equal(netmask, that.netmask) && equal(dns1, that.dns1)
            && equal(dns2, that.dns2) && equal(dnsSuffix, that.dnsSuffix) && equal(isBusy, that.isBusy);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(name, ipScopeId, gateway, netmask, dns1, dns2, dnsSuffix, isBusy);
   }

   @Override
   public ToStringHelper string() {
      return string().add("name", name).add("ipScopeId", ipScopeId).add("gateway", gateway).add("netmask", netmask).add("dns1", dns1).add("dns2", dns2).add("dnsSuffix", dnsSuffix).add("isBusy",
            isBusy);
   }

}
