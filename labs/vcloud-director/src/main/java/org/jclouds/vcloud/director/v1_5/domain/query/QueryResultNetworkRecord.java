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

import static com.google.common.base.Objects.*;
import static com.google.common.base.Preconditions.*;

import java.net.URI;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;

import org.jclouds.vcloud.director.v1_5.domain.Link;
import org.jclouds.vcloud.director.v1_5.domain.query.QueryResultRecordType.Builder;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.Sets;

/**
 * Represents the results from a Network vCloud query as a record.
 * 
 * <pre>
 * &lt;complexType name="QueryResultNetworkRecord" /&gt;
 * </pre>
 * 
 * @author grkvlt@apache.org
 */
public class QueryResultNetworkRecord extends QueryResultRecordType<QueryResultNetworkRecord> {
   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return new Builder().fromQueryResultNetworkRecord(this);
   }

   public static class Builder extends QueryResultRecordType.Builder<QueryResultNetworkRecord> {

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
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @see QueryResultNetworkRecord#getIpScopeId()
       */
      public Builder ipScopeId(String ipScopeId) {
         this.ipScopeId = ipScopeId;
         return this;
      }

      /**
       * @see QueryResultNetworkRecord#getGateway()
       */
      public Builder gateway(String gateway) {
         this.gateway = gateway;
         return this;
      }

      /**
       * @see QueryResultNetworkRecord#getNetmask()
       */
      public Builder netmask(String netmask) {
         this.netmask = netmask;
         return this;
      }

      /**
       * @see QueryResultNetworkRecord#getDns1()
       */
      public Builder dns1(String dns1) {
         this.dns1 = dns1;
         return this;
      }

      /**
       * @see QueryResultNetworkRecord#getDns2()
       */
      public Builder dns2(String dns2) {
         this.dns2 = dns2;
         return this;
      }

      /**
       * @see QueryResultNetworkRecord#getDnsSuffix()
       */
      public Builder dnsSuffix(String dnsSuffix) {
         this.dnsSuffix = dnsSuffix;
         return this;
      }

      /**
       * @see QueryResultNetworkRecord#isBusy()
       */
      public Builder isBusy(Boolean isBusy) {
         this.isBusy = isBusy;
         return this;
      }

      /**
       * @see QueryResultNetworkRecord#isBusy()
       */
      public Builder busy() {
         this.isBusy = Boolean.TRUE;
         return this;
      }

      /**
       * @see QueryResultNetworkRecord#isBusy()
       */
      public Builder notBusy() {
         this.isBusy = Boolean.FALSE;
         return this;
      }

      @Override
      public QueryResultNetworkRecord build() {
         QueryResultNetworkRecord queryResultNetworkRecord = new QueryResultNetworkRecord(href);
         queryResultNetworkRecord.setName(name);
         queryResultNetworkRecord.setIpScopeId(ipScopeId);
         queryResultNetworkRecord.setGateway(gateway);
         queryResultNetworkRecord.setNetmask(netmask);
         queryResultNetworkRecord.setDns1(dns1);
         queryResultNetworkRecord.setDns2(dns2);
         queryResultNetworkRecord.setDnsSuffix(dnsSuffix);
         queryResultNetworkRecord.setIsBusy(isBusy);
         queryResultNetworkRecord.setId(id);
         queryResultNetworkRecord.setType(type);
         queryResultNetworkRecord.setLinks(links);
         return queryResultNetworkRecord;
      }

      /**
       * @see QueryResultRecordType#getHref()
       */
      @Override
      public Builder href(URI href) {
         this.href = href;
         return this;
      }

      /**
       * @see QueryResultRecordType#getId()
       */
      @Override
      public Builder id(String id) {
         this.id = id;
         return this;
      }

      /**
       * @see QueryResultRecordType#getType()
       */
      @Override
      public Builder type(String type) {
         this.type = type;
         return this;
      }

      /**
       * @see QueryResultRecordType#getLinks()
       */
      @Override
      public Builder links(Set<Link> links) {
         this.links = Sets.newLinkedHashSet(checkNotNull(links, "links"));
         return this;
      }

      /**
       * @see QueryResultRecordType#getLinks()
       */
      @Override
      public Builder link(Link link) {
         this.links.add(checkNotNull(link, "link"));
         return this;
      }

      @Override
      public Builder fromQueryResultRecordType(QueryResultRecordType<QueryResultNetworkRecord> in) {
         return Builder.class.cast(super.fromQueryResultRecordType(in));
      }

      public Builder fromQueryResultNetworkRecord(QueryResultNetworkRecord in) {
         return fromQueryResultRecordType(in)
               .name(in.getName()).ipScopeId(in.getIpScopeId()).gateway(in.getGateway())
               .netmask(in.getNetmask()).dns1(in.getDns1()).dns2(in.getDns2())
               .dnsSuffix(in.getDnsSuffix()).isBusy(in.isBusy());
      }
   }

   private QueryResultNetworkRecord() {
      // For JAXB and builder use
   }

   private QueryResultNetworkRecord(URI href) {
      super(href);
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

   public void setName(String value) {
      this.name = value;
   }

   /**
    * IP scope object of this network.
    */
   public String getIpScopeId() {
      return ipScopeId;
   }

   public void setIpScopeId(String value) {
      this.ipScopeId = value;
   }

   /**
    * Gateway for the network.
    */
   public String getGateway() {
      return gateway;
   }

   public void setGateway(String value) {
      this.gateway = value;
   }

   /**
    * Netmask for the network.
    */
   public String getNetmask() {
      return netmask;
   }

   public void setNetmask(String value) {
      this.netmask = value;
   }

   /**
    * Primary DNS for the network.
    */
   public String getDns1() {
      return dns1;
   }

   public void setDns1(String value) {
      this.dns1 = value;
   }

   /**
    * Secondary DNS for the network.
    */
   public String getDns2() {
      return dns2;
   }

   public void setDns2(String value) {
      this.dns2 = value;
   }

   /**
    * DNS suffix for the network.
    */
   public String getDnsSuffix() {
      return dnsSuffix;
   }

   public void setDnsSuffix(String value) {
      this.dnsSuffix = value;
   }

   /**
    * Shows whether it is busy.
    */
   public Boolean isBusy() {
      return isBusy;
   }

   public void setIsBusy(Boolean value) {
      this.isBusy = value;
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
