/*
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
package org.jclouds.vcloud.director.v1_5.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Collections;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

import org.jclouds.vcloud.director.v1_5.domain.ovf.SectionType;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * Represents a list of network cards existing in a VM.
 * 
 * <pre>
 * &lt;complexType name="NetworkConnectionSection" /&gt;
 * </pre>
 */
@XmlRootElement(name = "NetworkConnectionSection")
public class NetworkConnectionSection extends SectionType {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return builder().fromNetworkConnectionSection(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public static class Builder<B extends Builder<B>> extends SectionType.Builder<B> {

      private Integer primaryNetworkConnectionIndex;
      private Set<NetworkConnection> networkConnections = Sets.newLinkedHashSet();
      private Set<Link> links = Sets.newLinkedHashSet();
      private URI href;
      private String type;

      /**
       * @see NetworkConnectionSection#getPrimaryNetworkConnectionIndex()
       */
      public B primaryNetworkConnectionIndex(Integer primaryNetworkConnectionIndex) {
         this.primaryNetworkConnectionIndex = primaryNetworkConnectionIndex;
         return self();
      }

      /**
       * @see NetworkConnectionSection#getNetworkConnections()
       */
      public B networkConnections(Set<NetworkConnection> networkConnections) {
         this.networkConnections = checkNotNull(networkConnections, "networkConnection");
         return self();
      }

      /**
       * @see NetworkConnectionSection#getLinks()
       */
      public B links(Set<Link> links) {
         this.links = checkNotNull(links, "links");
         return self();
      }

      /**
       * @see NetworkConnectionSection#getHref()
       */
      public B href(URI href) {
         this.href = href;
         return self();
      }

      /**
       * @see NetworkConnectionSection#getType()
       */
      public B type(String type) {
         this.type = type;
         return self();
      }

      @Override
      public NetworkConnectionSection build() {
         return new NetworkConnectionSection(this);

      }

      public B fromNetworkConnectionSection(NetworkConnectionSection in) {
         return fromSectionType(in).primaryNetworkConnectionIndex(in.getPrimaryNetworkConnectionIndex()).networkConnections(in.getNetworkConnections()).links(in.getLinks()).href(in.getHref()).type(
               in.getType());
      }
   }

   private NetworkConnectionSection(Builder<?> builder) {
      super(builder);
      this.primaryNetworkConnectionIndex = builder.primaryNetworkConnectionIndex;
      this.networkConnections = ImmutableSet.copyOf(builder.networkConnections);
      this.links = ImmutableSet.copyOf(builder.links);
      this.href = builder.href;
      this.type = builder.type;
   }

   private NetworkConnectionSection() {
      // For JAXB
   }

   @XmlElement(name = "PrimaryNetworkConnectionIndex")
   protected Integer primaryNetworkConnectionIndex;
   @XmlElement(name = "NetworkConnection")
   protected Set<NetworkConnection> networkConnections = Sets.newLinkedHashSet();
   @XmlElement(name = "Link")
   protected Set<Link> links = Sets.newLinkedHashSet();
   @XmlAttribute
   @XmlSchemaType(name = "anyURI")
   protected URI href;
   @XmlAttribute
   protected String type;

   /**
    * Gets the value of the primaryNetworkConnectionIndex property.
    * 
    * @return possible object is {@link Integer }
    */
   public Integer getPrimaryNetworkConnectionIndex() {
      return primaryNetworkConnectionIndex;
   }

   /**
    * Gets the value of the networkConnection property.
    * <p/>
    * Objects of the following type(s) are allowed in the list {@link NetworkConnection }
    */
   public Set<NetworkConnection> getNetworkConnections() {
      return Collections.unmodifiableSet(this.networkConnections);
   }

   /**
    * Gets the value of the link property.
    * <p/>
    * Objects of the following type(s) are allowed in the list {@link Link }
    */
   public Set<Link> getLinks() {
      return Collections.unmodifiableSet(this.links);
   }

   /**
    * @return the value of the href property.
    */
   public URI getHref() {
      return href;
   }

   /**
    * Gets the value of the type property.
    * 
    * @return possible object is {@link String }
    */
   public String getType() {
      return type;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      NetworkConnectionSection that = NetworkConnectionSection.class.cast(o);
      return super.equals(that) &&
            equal(primaryNetworkConnectionIndex, that.primaryNetworkConnectionIndex) &&
            equal(networkConnections, that.networkConnections) && equal(links, that.links) &&
            equal(href, that.href) && equal(type, that.type);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), primaryNetworkConnectionIndex, networkConnections, links, href, type);
   }

   @Override
   public Objects.ToStringHelper string() {
      return super.string().add("primaryNetworkConnectionIndex", primaryNetworkConnectionIndex).add("networkConnection", networkConnections).add("links", links).add("href", href).add("type", type);
   }
}
