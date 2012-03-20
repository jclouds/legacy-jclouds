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
 * Represents the results from a StrandedUser vCloud query as a record.
 * 
 * @author Aled Sage
 */
@XmlRootElement(name = "StrandedUserRecord")
@XmlType(name = "QueryResultStrandedUserRecordType")
public class QueryResultStrandedUserRecord extends QueryResultRecordType {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return builder().fromQueryResultStrandedUserRecord(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }

   public static class Builder<B extends Builder<B>> extends QueryResultRecordType.Builder<B> {

      private String name;
      private String fullName;
      private Boolean isInSync;
      private Integer numberOfDeployedVMs;
      private Integer numberOfStoredVMs;

      /**
       * @see QueryResultStrandedUserRecord#getName()
       */
      public B name(String val) {
         this.name = val;
         return self();
      }

      /**
       * @see QueryResultStrandedUserRecord#getFullName()
       */
      public B fullName(String val) {
         this.fullName = val;
         return self();
      }

      /**
       * @see QueryResultStrandedUserRecord#isInSync()
       */
      public B isInSync(Boolean val) {
         this.isInSync = val;
         return self();
      }

      /**
       * @see QueryResultStrandedUserRecord#getNumberOfDeployedVMs()
       */
      public B numberOfDeployedVMs(Integer val) {
         this.numberOfDeployedVMs = val;
         return self();
      }

      /**
       * @see QueryResultStrandedUserRecord#getNumberOfStoredVMs()
       */
      public B numberOfStoredVMs(Integer val) {
         this.numberOfStoredVMs = val;
         return self();
      }

      @Override
      public QueryResultStrandedUserRecord build() {
         return new QueryResultStrandedUserRecord(this);
      }

      public B fromQueryResultStrandedUserRecord(QueryResultStrandedUserRecord in) {
         return fromQueryResultRecordType(in)
                  .name(in.getName())
                  .fullName(in.getFullName())
                  .isInSync(in.isInSync())
                  .numberOfDeployedVMs(in.getNumberOfDeployedVMs())
                  .numberOfStoredVMs(in.getNumberOfStoredVMs());
      }

   }

   @XmlAttribute
   private String name;
   @XmlAttribute
   private String fullName;
   @XmlAttribute
   private Boolean isInSync;
   @XmlAttribute
   private Integer numberOfDeployedVMs;
   @XmlAttribute
   private Integer numberOfStoredVMs;

   protected QueryResultStrandedUserRecord(Builder<?> builder) {
      super(builder);
      this.name = builder.name;
      this.fullName = builder.fullName;
      this.isInSync = builder.isInSync;
      this.numberOfDeployedVMs = builder.numberOfDeployedVMs;
      this.numberOfStoredVMs = builder.numberOfStoredVMs;
   }

   protected QueryResultStrandedUserRecord() {
      // for JAXB
   }

   /**
    * name
    */
   public String getName() {
      return name;
   }

   /**
    * Full name
    */
   public String getFullName() {
      return fullName;
   }

   /**
    * Shows whether it is in sync
    */
   public Boolean isInSync() {
      return isInSync;
   }

   /**
    * Number of deployed VMs
    */
   public Integer getNumberOfDeployedVMs() {
      return numberOfDeployedVMs;
   }

   /**
    * Number of stored VMs
    */
   public Integer getNumberOfStoredVMs() {
      return numberOfStoredVMs;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      QueryResultStrandedUserRecord that = QueryResultStrandedUserRecord.class.cast(o);

      return super.equals(that) && equal(name, that.name) && equal(fullName, that.fullName) 
                && equal(isInSync, that.isInSync) && equal(numberOfDeployedVMs, that.numberOfDeployedVMs) 
                && equal(numberOfStoredVMs, that.numberOfStoredVMs);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), name, fullName, isInSync, numberOfDeployedVMs, numberOfStoredVMs);
   }
   @Override
   public ToStringHelper string() {
      return super.string().add("name", name).add("fullName", fullName).add("isInSync", isInSync)
               .add("numberOfDeployedVMs", numberOfDeployedVMs).add("numberOfStoredVMs", numberOfStoredVMs);
   }
}
