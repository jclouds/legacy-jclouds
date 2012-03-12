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
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Date;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

import org.jclouds.vcloud.director.v1_5.domain.Link;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.Sets;

/**
 * Represents the results from a Catalog vCloud query as a record.
 * <p/>
 * <pre>
 * &lt;complexType name="QueryResultCatalogRecord" /&gt;
 * </pre>
 *
 * @author grkvlt@apache.org
 */
@XmlRootElement(name = "CatalogRecord")
public class QueryResultCatalogRecord extends QueryResultRecordType<QueryResultCatalogRecord> {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return new Builder().fromQueryResultCatalogRecord(this);
   }

   public static class Builder extends QueryResultRecordType.Builder<QueryResultCatalogRecord> {

      private String name;
      private Boolean isPublished;
      private Boolean isShared;
      private Date creationDate;
      private String orgName;
      private String ownerName;
      private Integer numberOfVAppTemplates;
      private Integer numberOfMedia;
      private URI owner;

      /**
       * @see QueryResultCatalogRecord#getName()
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @see QueryResultCatalogRecord#isPublished()
       */
      public Builder isPublished(Boolean isPublished) {
         this.isPublished = isPublished;
         return this;
      }

      /**
       * @see QueryResultCatalogRecord#isPublished()
       */
      public Builder published() {
         this.isPublished = Boolean.TRUE;
         return this;
      }

      /**
       * @see QueryResultCatalogRecord#isPublished()
       */
      public Builder notPublished() {
         this.isPublished = Boolean.FALSE;
         return this;
      }

      /**
       * @see QueryResultCatalogRecord#isShared()
       */
      public Builder isShared(Boolean isShared) {
         this.isShared = isShared;
         return this;
      }

      /**
       * @see QueryResultCatalogRecord#isShared()
       */
      public Builder shared() {
         this.isShared = Boolean.TRUE;
         return this;
      }

      /**
       * @see QueryResultCatalogRecord#isShared()
       */
      public Builder notShared() {
         this.isShared = Boolean.FALSE;
         return this;
      }

      /**
       * @see QueryResultCatalogRecord#getCreationDate()
       */
      public Builder creationDate(Date creationDate) {
         this.creationDate = creationDate;
         return this;
      }

      /**
       * @see QueryResultCatalogRecord#getOrgName()
       */
      public Builder orgName(String orgName) {
         this.orgName = orgName;
         return this;
      }

      /**
       * @see QueryResultCatalogRecord#getOwnerName()
       */
      public Builder ownerName(String ownerName) {
         this.ownerName = ownerName;
         return this;
      }

      /**
       * @see QueryResultCatalogRecord#getNumberOfVAppTemplates()
       */
      public Builder numberOfVAppTemplates(Integer numberOfVAppTemplates) {
         this.numberOfVAppTemplates = numberOfVAppTemplates;
         return this;
      }

      /**
       * @see QueryResultCatalogRecord#getNumberOfMedia()
       */
      public Builder numberOfMedia(Integer numberOfMedia) {
         this.numberOfMedia = numberOfMedia;
         return this;
      }

      /**
       * @see QueryResultCatalogRecord#getOwner()
       */
      public Builder owner(URI owner) {
         this.owner = owner;
         return this;
      }

      @Override
      public QueryResultCatalogRecord build() {
         return new QueryResultCatalogRecord(links, href, id, type,name, isPublished, isShared, creationDate, orgName,
               ownerName, numberOfVAppTemplates, numberOfMedia, owner);
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
      public Builder fromQueryResultRecordType(QueryResultRecordType<QueryResultCatalogRecord> in) {
         return Builder.class.cast(super.fromQueryResultRecordType(in));
      }

      public Builder fromQueryResultCatalogRecord(QueryResultCatalogRecord in) {
         return fromQueryResultRecordType(in).name(in.getName()).isPublished(in.isPublished()).isShared(in.isShared()).creationDate(in.getCreationDate()).orgName(in.getOrgName()).ownerName(
               in.getOwnerName()).numberOfVAppTemplates(in.getNumberOfVAppTemplates()).numberOfMedia(in.getNumberOfMedia()).owner(in.getOwner());
      }
   }

   private QueryResultCatalogRecord(Set<Link> links, URI href, String id, String type, String name,
                                   Boolean published, Boolean shared, Date creationDate, String orgName, 
                                   String ownerName, Integer numberOfVAppTemplates, Integer numberOfMedia, 
                                   URI owner) {
      super(links, href, id, type);
      this.name = name;
      isPublished = published;
      isShared = shared;
      this.creationDate = creationDate;
      this.orgName = orgName;
      this.ownerName = ownerName;
      this.numberOfVAppTemplates = numberOfVAppTemplates;
      this.numberOfMedia = numberOfMedia;
      this.owner = owner;
   }

   private QueryResultCatalogRecord() {
      // for JAXB
   }


   @XmlAttribute
   protected String name;
   @XmlAttribute
   protected Boolean isPublished;
   @XmlAttribute
   protected Boolean isShared;
   @XmlAttribute
   @XmlSchemaType(name = "dateTime")
   protected Date creationDate;
   @XmlAttribute
   protected String orgName;
   @XmlAttribute
   protected String ownerName;
   @XmlAttribute
   protected Integer numberOfVAppTemplates;
   @XmlAttribute
   protected Integer numberOfMedia;
   @XmlAttribute
   protected URI owner;

   /**
    * Gets the value of the name property.
    */
   public String getName() {
      return name;
   }


   /**
    * Gets the value of the isPublished property.
    */
   public Boolean isPublished() {
      return isPublished;
   }

   /**
    * Gets the value of the isShared property.
    */
   public Boolean isShared() {
      return isShared;
   }

   /**
    * Gets the value of the creationDate property.
    */
   public Date getCreationDate() {
      return creationDate;
   }

   /**
    * Gets the value of the orgName property.
    */
   public String getOrgName() {
      return orgName;
   }

   /**
    * Gets the value of the ownerName property.
    */
   public String getOwnerName() {
      return ownerName;
   }

   /**
    * Gets the value of the numberOfVAppTemplates property.
    */
   public Integer getNumberOfVAppTemplates() {
      return numberOfVAppTemplates;
   }

   /**
    * Gets the value of the numberOfMedia property.
    */
   public Integer getNumberOfMedia() {
      return numberOfMedia;
   }

   /**
    * Gets the value of the owner property.
    */
   public URI getOwner() {
      return owner;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      QueryResultCatalogRecord that = QueryResultCatalogRecord.class.cast(o);
      return super.equals(that) &&
            equal(this.name, that.name) && equal(this.isPublished, that.isPublished) &&
            equal(this.isShared, that.isShared) && equal(this.creationDate, that.creationDate) &&
            equal(this.orgName, that.orgName) && equal(this.ownerName, that.ownerName) &&
            equal(this.numberOfVAppTemplates, that.numberOfVAppTemplates) &&
            equal(this.numberOfMedia, that.numberOfMedia) && equal(this.owner, that.owner);
   }

   @Override
   public int hashCode() {
      return super.hashCode() + Objects.hashCode(name, isPublished, isShared, creationDate,
            orgName, ownerName, numberOfVAppTemplates, numberOfMedia, owner);
   }

   @Override
   public ToStringHelper string() {
      return super.string()
            .add("name", name).add("isPublished", isPublished).add("isShared", isShared)
            .add("creationDate", creationDate).add("orgName", orgName).add("ownerName", ownerName)
            .add("numberOfVAppTemplates", numberOfVAppTemplates).add("numberOfMedia", numberOfMedia)
            .add("owner", owner);
   }

}
