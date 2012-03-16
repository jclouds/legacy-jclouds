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
package org.jclouds.vcloud.director.v1_5.domain.query;

import static com.google.common.base.Objects.equal;

import java.net.URI;
import java.util.Date;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Represents the results from a Catalog vCloud query as a record.
 *
 * <pre>
 * &lt;complexType name="QueryResultCatalogRecordType" /&gt;
 * </pre>
 *
 * @author grkvlt@apache.org
 */
@XmlRootElement(name = "CatalogRecord")
@XmlType(name = "QueryResultCatalogRecordType")
public class QueryResultCatalogRecord extends QueryResultRecordType {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return builder().fromQueryResultCatalogRecord(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public static class Builder<B extends Builder<B>> extends QueryResultRecordType.Builder<B> {

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
      public B name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see QueryResultCatalogRecord#isPublished()
       */
      public B isPublished(Boolean isPublished) {
         this.isPublished = isPublished;
         return self();
      }

      /**
       * @see QueryResultCatalogRecord#isPublished()
       */
      public B published() {
         this.isPublished = Boolean.TRUE;
         return self();
      }

      /**
       * @see QueryResultCatalogRecord#isPublished()
       */
      public B notPublished() {
         this.isPublished = Boolean.FALSE;
         return self();
      }

      /**
       * @see QueryResultCatalogRecord#isShared()
       */
      public B isShared(Boolean isShared) {
         this.isShared = isShared;
         return self();
      }

      /**
       * @see QueryResultCatalogRecord#isShared()
       */
      public B shared() {
         this.isShared = Boolean.TRUE;
         return self();
      }

      /**
       * @see QueryResultCatalogRecord#isShared()
       */
      public B notShared() {
         this.isShared = Boolean.FALSE;
         return self();
      }

      /**
       * @see QueryResultCatalogRecord#getCreationDate()
       */
      public B creationDate(Date creationDate) {
         this.creationDate = creationDate;
         return self();
      }

      /**
       * @see QueryResultCatalogRecord#getOrgName()
       */
      public B orgName(String orgName) {
         this.orgName = orgName;
         return self();
      }

      /**
       * @see QueryResultCatalogRecord#getOwnerName()
       */
      public B ownerName(String ownerName) {
         this.ownerName = ownerName;
         return self();
      }

      /**
       * @see QueryResultCatalogRecord#getNumberOfVAppTemplates()
       */
      public B numberOfVAppTemplates(Integer numberOfVAppTemplates) {
         this.numberOfVAppTemplates = numberOfVAppTemplates;
         return self();
      }

      /**
       * @see QueryResultCatalogRecord#getNumberOfMedia()
       */
      public B numberOfMedia(Integer numberOfMedia) {
         this.numberOfMedia = numberOfMedia;
         return self();
      }

      /**
       * @see QueryResultCatalogRecord#getOwner()
       */
      public B owner(URI owner) {
         this.owner = owner;
         return self();
      }

      @Override
      public QueryResultCatalogRecord build() {
         return new QueryResultCatalogRecord(this);
      }

      public B fromQueryResultCatalogRecord(QueryResultCatalogRecord in) {
         return fromQueryResultRecordType(in).name(in.getName()).isPublished(in.isPublished()).isShared(in.isShared()).creationDate(in.getCreationDate()).orgName(in.getOrgName()).ownerName(
               in.getOwnerName()).numberOfVAppTemplates(in.getNumberOfVAppTemplates()).numberOfMedia(in.getNumberOfMedia()).owner(in.getOwner());
      }
   }

   private QueryResultCatalogRecord(Builder<?> builder) {
      super(builder);
      this.name = builder.name;
      this.isPublished = builder.isPublished;
      this.isShared = builder.isShared;
      this.creationDate = builder.creationDate;
      this.orgName = builder.orgName;
      this.ownerName = builder.ownerName;
      this.numberOfVAppTemplates = builder.numberOfVAppTemplates;
      this.numberOfMedia = builder.numberOfMedia;
      this.owner = builder.owner;
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
      return Objects.hashCode(super.hashCode(), name, isPublished, isShared, creationDate,
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
