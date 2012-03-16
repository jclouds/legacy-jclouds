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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Represents the results from a Datastore vCloud query as a record.
 * 
 * <pre>
 * &lt;complexType name="QueryResultDatastoreRecordType" /&gt;
 * </pre>
 * 
 * @author grkvlt@apache.org
 */
@XmlRootElement(name = "DatastoreRecord")
@XmlType(name = "QueryResultDatastoreRecordType")
public class QueryResultDatastoreRecord extends QueryResultRecordType {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return builder().fromQueryResultDatastoreRecord(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }

   public static class Builder<B extends Builder<B>> extends QueryResultRecordType.Builder<B> {

      private String name;
      private String datastoreType;
      private Boolean isEnabled;
      private Boolean isDeleted;
      private Long storageUsedMB;
      private Long storageMB;
      private Long provisionedStorageMB;
      private Long requestedStorageMB;
      private String vc;
      private String vcName;
      private String moref;
      private Integer numberOfProviderVdcs;

      /**
       * @see QueryResultDatastoreRecord#getName()
       */
      public B name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see QueryResultDatastoreRecord#getDatastoreType()
       */
      public B datastoreType(String datastoreType) {
         this.datastoreType = datastoreType;
         return self();
      }

      /**
       * @see QueryResultDatastoreRecord#isEnabled()
       */
      public B isEnabled(Boolean isEnabled) {
         this.isEnabled = isEnabled;
         return self();
      }

      /**
       * @see QueryResultDatastoreRecord#isEnabled()
       */
      public B enabled() {
         this.isEnabled = Boolean.TRUE;
         return self();
      }

      /**
       * @see QueryResultDatastoreRecord#isEnabled()
       */
      public B disabled() {
         this.isEnabled = Boolean.FALSE;
         return self();
      }

      /**
       * @see QueryResultDatastoreRecord#isDeleted()
       */
      public B isDeleted(Boolean isDeleted) {
         this.isDeleted = isDeleted;
         return self();
      }

      /**
       * @see QueryResultDatastoreRecord#isDeleted()
       */
      public B deleted() {
         this.isDeleted = Boolean.TRUE;
         return self();
      }

      /**
       * @see QueryResultDatastoreRecord#isDeleted()
       */
      public B notDeleted() {
         this.isDeleted = Boolean.FALSE;
         return self();
      }

      /**
       * @see QueryResultDatastoreRecord#getStorageUsedMB()
       */
      public B storageUsedMB(Long storageUsedMB) {
         this.storageUsedMB = storageUsedMB;
         return self();
      }

      /**
       * @see QueryResultDatastoreRecord#getStorageMB()
       */
      public B storageMB(Long storageMB) {
         this.storageMB = storageMB;
         return self();
      }

      /**
       * @see QueryResultDatastoreRecord#getProvisionedStorageMB()
       */
      public B provisionedStorageMB(Long provisionedStorageMB) {
         this.provisionedStorageMB = provisionedStorageMB;
         return self();
      }

      /**
       * @see QueryResultDatastoreRecord#getRequestedStorageMB()
       */
      public B requestedStorageMB(Long requestedStorageMB) {
         this.requestedStorageMB = requestedStorageMB;
         return self();
      }

      /**
       * @see QueryResultDatastoreRecord#getVc()
       */
      public B vc(String vc) {
         this.vc = vc;
         return self();
      }

      /**
       * @see QueryResultDatastoreRecord#getVcName()
       */
      public B vcName(String vcName) {
         this.vcName = vcName;
         return self();
      }

      /**
       * @see QueryResultDatastoreRecord#getMoref()
       */
      public B moref(String moref) {
         this.moref = moref;
         return self();
      }

      /**
       * @see QueryResultDatastoreRecord#getNumberOfProviderVdcs()
       */
      public B numberOfProviderVdcs(Integer numberOfProviderVdcs) {
         this.numberOfProviderVdcs = numberOfProviderVdcs;
         return self();
      }

      @Override
      public QueryResultDatastoreRecord build() {
         return new QueryResultDatastoreRecord(this);
      }

      public B fromQueryResultDatastoreRecord(QueryResultDatastoreRecord in) {
         return fromQueryResultRecordType(in)
               .name(in.getName())
               .datastoreType(in.getDatastoreType())
               .isEnabled(in.isEnabled())
               .isDeleted(in.isDeleted())
               .storageUsedMB(in.getStorageUsedMB())
               .storageMB(in.getStorageMB())
               .provisionedStorageMB(in.getProvisionedStorageMB())
               .requestedStorageMB(in.getRequestedStorageMB())
               .vc(in.getVc())
               .vcName(in.getVcName())
               .moref(in.getMoref())
               .numberOfProviderVdcs(in.getNumberOfProviderVdcs());
      }
   }

   private QueryResultDatastoreRecord(Builder<?> builder) {
      super(builder);
      this.name = builder.name;
      this.datastoreType = builder.datastoreType;
      this.isEnabled = builder.isEnabled;
      this.isDeleted = builder.isDeleted;
      this.storageUsedMB = builder.storageUsedMB;
      this.storageMB = builder.storageMB;
      this.provisionedStorageMB = builder.provisionedStorageMB;
      this.requestedStorageMB = builder.requestedStorageMB;
      this.vc = builder.vc;
      this.vcName = builder.vcName;
      this.moref = builder.moref;
      this.numberOfProviderVdcs = builder.numberOfProviderVdcs;
   }

   private QueryResultDatastoreRecord() {
      // for JAXB
   }

   @XmlAttribute
   protected String name;
   @XmlAttribute
   protected String datastoreType;
   @XmlAttribute
   protected Boolean isEnabled;
   @XmlAttribute
   protected Boolean isDeleted;
   @XmlAttribute
   protected Long storageUsedMB;
   @XmlAttribute
   protected Long storageMB;
   @XmlAttribute
   protected Long provisionedStorageMB;
   @XmlAttribute
   protected Long requestedStorageMB;
   @XmlAttribute
   protected String vc;
   @XmlAttribute
   protected String vcName;
   @XmlAttribute
   protected String moref;
   @XmlAttribute
   protected Integer numberOfProviderVdcs;

   /**
    * Gets the value of the name property.
    * 
    * @return possible object is {@link String }
    */
   public String getName() {
      return name;
   }

   /**
    * Sets the value of the name property.
    * 
    * @param value
    *           allowed object is {@link String }
    */
   public void setName(String value) {
      this.name = value;
   }

   /**
    * Gets the value of the datastoreType property.
    * 
    * @return possible object is {@link String }
    */
   public String getDatastoreType() {
      return datastoreType;
   }

   /**
    * Sets the value of the datastoreType property.
    * 
    * @param value
    *           allowed object is {@link String }
    */
   public void setDatastoreType(String value) {
      this.datastoreType = value;
   }

   /**
    * Gets the value of the isEnabled property.
    * 
    * @return possible object is {@link Boolean }
    */
   public Boolean isEnabled() {
      return isEnabled;
   }

   /**
    * Sets the value of the isEnabled property.
    * 
    * @param value
    *           allowed object is {@link Boolean }
    */
   public void setIsEnabled(Boolean value) {
      this.isEnabled = value;
   }

   /**
    * Gets the value of the isDeleted property.
    * 
    * @return possible object is {@link Boolean }
    */
   public Boolean isDeleted() {
      return isDeleted;
   }

   /**
    * Sets the value of the isDeleted property.
    * 
    * @param value
    *           allowed object is {@link Boolean }
    */
   public void setIsDeleted(Boolean value) {
      this.isDeleted = value;
   }

   /**
    * Gets the value of the storageUsedMB property.
    * 
    * @return possible object is {@link Long }
    */
   public Long getStorageUsedMB() {
      return storageUsedMB;
   }

   /**
    * Sets the value of the storageUsedMB property.
    * 
    * @param value
    *           allowed object is {@link Long }
    */
   public void setStorageUsedMB(Long value) {
      this.storageUsedMB = value;
   }

   /**
    * Gets the value of the storageMB property.
    * 
    * @return possible object is {@link Long }
    */
   public Long getStorageMB() {
      return storageMB;
   }

   /**
    * Sets the value of the storageMB property.
    * 
    * @param value
    *           allowed object is {@link Long }
    */
   public void setStorageMB(Long value) {
      this.storageMB = value;
   }

   /**
    * Gets the value of the provisionedStorageMB property.
    * 
    * @return possible object is {@link Long }
    */
   public Long getProvisionedStorageMB() {
      return provisionedStorageMB;
   }

   /**
    * Sets the value of the provisionedStorageMB property.
    * 
    * @param value
    *           allowed object is {@link Long }
    */
   public void setProvisionedStorageMB(Long value) {
      this.provisionedStorageMB = value;
   }

   /**
    * Gets the value of the requestedStorageMB property.
    * 
    * @return possible object is {@link Long }
    */
   public Long getRequestedStorageMB() {
      return requestedStorageMB;
   }

   /**
    * Sets the value of the requestedStorageMB property.
    * 
    * @param value
    *           allowed object is {@link Long }
    */
   public void setRequestedStorageMB(Long value) {
      this.requestedStorageMB = value;
   }

   /**
    * Gets the value of the vc property.
    * 
    * @return possible object is {@link String }
    */
   public String getVc() {
      return vc;
   }

   /**
    * Sets the value of the vc property.
    * 
    * @param value
    *           allowed object is {@link String }
    */
   public void setVc(String value) {
      this.vc = value;
   }

   /**
    * Gets the value of the vcName property.
    * 
    * @return possible object is {@link String }
    */
   public String getVcName() {
      return vcName;
   }

   /**
    * Sets the value of the vcName property.
    * 
    * @param value
    *           allowed object is {@link String }
    */
   public void setVcName(String value) {
      this.vcName = value;
   }

   /**
    * Gets the value of the moref property.
    * 
    * @return possible object is {@link String }
    */
   public String getMoref() {
      return moref;
   }

   /**
    * Sets the value of the moref property.
    * 
    * @param value
    *           allowed object is {@link String }
    */
   public void setMoref(String value) {
      this.moref = value;
   }

   /**
    * Gets the value of the numberOfProviderVdcs property.
    * 
    * @return possible object is {@link Integer }
    */
   public Integer getNumberOfProviderVdcs() {
      return numberOfProviderVdcs;
   }

   /**
    * Sets the value of the numberOfProviderVdcs property.
    * 
    * @param value
    *           allowed object is {@link Integer }
    */
   public void setNumberOfProviderVdcs(Integer value) {
      this.numberOfProviderVdcs = value;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      QueryResultDatastoreRecord that = QueryResultDatastoreRecord.class.cast(o);
      return super.equals(that) && equal(name, that.name) && equal(datastoreType, that.datastoreType) && equal(isEnabled, that.isEnabled) && equal(isDeleted, that.isDeleted)
            && equal(storageUsedMB, that.storageUsedMB) && equal(storageMB, that.storageMB) && equal(provisionedStorageMB, that.provisionedStorageMB)
            && equal(requestedStorageMB, that.requestedStorageMB) && equal(vc, that.vc) && equal(vcName, that.vcName) && equal(moref, that.moref)
            && equal(numberOfProviderVdcs, that.numberOfProviderVdcs);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), name, datastoreType, isEnabled, isDeleted, storageUsedMB, storageMB, provisionedStorageMB, requestedStorageMB, vc, vcName, moref, numberOfProviderVdcs);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("name", name).add("datastoreType", datastoreType).add("isEnabled", isEnabled).add("isDeleted", isDeleted).add("storageUsedMB", storageUsedMB).add("storageMB",
            storageMB).add("provisionedStorageMB", provisionedStorageMB).add("requestedStorageMB", requestedStorageMB).add("vc", vc).add("vcName", vcName).add("moref", moref).add(
            "numberOfProviderVdcs", numberOfProviderVdcs);
   }

}
