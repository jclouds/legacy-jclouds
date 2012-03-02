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

package org.jclouds.vcloud.director.v1_5.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Set;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.vcloud.director.v1_5.domain.ovf.SectionType;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;


/**
 * Represents a list of network cards existing in a VM.
 * <p/>
 * <p/>
 * <p>Java class for NetworkConnectionSection complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="NetworkConnectionSection">
 *   &lt;complexContent>
 *     &lt;extension base="{http://schemas.dmtf.org/ovf/envelope/1}Section_Type">
 *       &lt;sequence>
 *         &lt;element name="PrimaryNetworkConnectionIndex" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="NetworkConnection" type="{http://www.vmware.com/vcloud/v1.5}NetworkConnectionType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Link" type="{http://www.vmware.com/vcloud/v1.5}LinkType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="href" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;anyAttribute processContents='lax'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement(name = "NetworkConnectionSection")
@XmlType(propOrder = {
      "primaryNetworkConnectionIndex",
      "networkConnections",
      "links"
})
public class NetworkConnectionSection extends SectionType<NetworkConnectionSection> {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromNetworkConnectionSection(this);
   }

   public static class Builder extends SectionType.Builder<NetworkConnectionSection> {

      private Integer primaryNetworkConnectionIndex;
      private Set<NetworkConnection> networkConnection = Sets.newLinkedHashSet();
      private Set<Link> links = Sets.newLinkedHashSet();
      private URI href;
      private String type;

      /**
       * @see NetworkConnectionSection#getPrimaryNetworkConnectionIndex()
       */
      public Builder primaryNetworkConnectionIndex(Integer primaryNetworkConnectionIndex) {
         this.primaryNetworkConnectionIndex = primaryNetworkConnectionIndex;
         return this;
      }

      /**
       * @see NetworkConnectionSection#getNetworkConnections()
       */
      public Builder networkConnection(Set<NetworkConnection> networkConnection) {
         this.networkConnection = checkNotNull(networkConnection, "networkConnection");
         return this;
      }

      /**
       * @see NetworkConnectionSection#getLinks()
       */
      public Builder links(Set<Link> links) {
         this.links = checkNotNull(links, "links");
         return this;
      }

      /**
       * @see NetworkConnectionSection#getHref()
       */
      public Builder href(URI href) {
         this.href = href;
         return this;
      }

      /**
       * @see NetworkConnectionSection#getType()
       */
      public Builder type(String type) {
         this.type = type;
         return this;
      }


      public NetworkConnectionSection build() {
         return new NetworkConnectionSection(info, required, primaryNetworkConnectionIndex, networkConnection, links, href, type);

      }

      public Builder fromNetworkConnectionSection(NetworkConnectionSection in) {
         return fromSection(in)
               .primaryNetworkConnectionIndex(in.getPrimaryNetworkConnectionIndex())
               .networkConnection(in.getNetworkConnections())
               .links(in.getLinks())
               .href(in.getHref())
               .type(in.getType());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromSection(SectionType<NetworkConnectionSection> in) {
         return Builder.class.cast(super.fromSection(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder info(String info) {
         return Builder.class.cast(super.info(info));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder required(Boolean required) {
         return Builder.class.cast(super.required(required));
      }

   }

   private NetworkConnectionSection(@Nullable String info, @Nullable Boolean required, Integer primaryNetworkConnectionIndex,
                                    Set<NetworkConnection> networkConnections, Set<Link> links, URI href, String type) {
      super(info, required);
      this.primaryNetworkConnectionIndex = primaryNetworkConnectionIndex;
      this.networkConnections = ImmutableSet.copyOf(networkConnections);
      this.links = ImmutableSet.copyOf(links);
      this.href = href;
      this.type = type;
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
    * @return possible object is
    *         {@link Integer }
    */
   public Integer getPrimaryNetworkConnectionIndex() {
      return primaryNetworkConnectionIndex;
   }

   /**
    * Gets the value of the networkConnection property.
    * <p/>
    * Objects of the following type(s) are allowed in the list
    * {@link NetworkConnection }
    */
   public Set<NetworkConnection> getNetworkConnections() {
      return this.networkConnections;
   }

   /**
    * Gets the value of the link property.
    * <p/>
    * Objects of the following type(s) are allowed in the list
    * {@link Link }
    */
   public Set<Link> getLinks() {
      return this.links;
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
    * @return possible object is
    *         {@link String }
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
            equal(networkConnections, that.networkConnections) &&
            equal(links, that.links) &&
            equal(href, that.href) &&
            equal(type, that.type);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(primaryNetworkConnectionIndex,
            networkConnections,
            links,
            href,
            type);
   }

   @Override
   public Objects.ToStringHelper string() {
      return super.string()
            .add("primaryNetworkConnectionIndex", primaryNetworkConnectionIndex)
            .add("networkConnection", networkConnections)
            .add("links", links)
            .add("href", href)
            .add("type", type);
   }

}
