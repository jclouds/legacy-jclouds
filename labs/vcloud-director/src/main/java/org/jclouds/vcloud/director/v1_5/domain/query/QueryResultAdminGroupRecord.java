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
 * Represents the results from a AdminGroup vCloud query as a record.
 * 
 * @author Aled Sage
 */
@XmlRootElement(name = "GroupRecord")
@XmlType(name = "QueryResultAdminGroupRecordType")
public class QueryResultAdminGroupRecord extends QueryResultRecordType {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return builder().fromQueryResultAdminGroupRecord(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }

   public static class Builder<B extends Builder<B>> extends QueryResultRecordType.Builder<B> {

      private String name;
      private String org;
      private String roleName;
      private Boolean isReadOnly;

      /**
       * @see QueryResultAdminGroupRecord#getName()
       */
      public B name(String val) {
         this.name = val;
         return self();
      }

      /**
       * @see QueryResultAdminGroupRecord#get()
       */
      public B org(String val) {
         this.org = val;
         return self();
      }

      /**
       * @see QueryResultAdminGroupRecord#getRoleName()
       */
      public B roleName(String val) {
         this.roleName = val;
         return self();
      }

      /**
       * @see QueryResultAdminGroupRecord#isReadOnly()
       */
      public B isReadOnly(Boolean val) {
         this.isReadOnly = val;
         return self();
      }

      @Override
      public QueryResultAdminGroupRecord build() {
         return new QueryResultAdminGroupRecord(this);
      }

      public B fromQueryResultAdminGroupRecord(QueryResultAdminGroupRecord in) {
         return fromQueryResultRecordType(in)
                  .name(in.getName())
                  .org(in.get())
                  .roleName(in.getRoleName())
                  .isReadOnly(in.isReadOnly());
      }

   }

   @XmlAttribute
   private String name;
   @XmlAttribute
   private String org;
   @XmlAttribute
   private String roleName;
   @XmlAttribute
   private Boolean isReadOnly;

   protected QueryResultAdminGroupRecord(Builder<?> builder) {
      super(builder);
      this.name = builder.name;
      this.org = builder.org;
      this.roleName = builder.roleName;
      this.isReadOnly = builder.isReadOnly;
   }

   protected QueryResultAdminGroupRecord() {
      // for JAXB
   }

   /**
    * name
    */
   public String getName() {
      return name;
   }

   /**
    * Organization reference or id
    */
   public String get() {
      return org;
   }

   /**
    * Role name
    */
   public String getRoleName() {
      return roleName;
   }

   /**
    * Shows whether it is read only
    */
   public Boolean isReadOnly() {
      return isReadOnly;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      QueryResultAdminGroupRecord that = QueryResultAdminGroupRecord.class.cast(o);

      return super.equals(that) && equal(name, that.name) && equal(org, that.org) && equal(roleName, that.roleName) && 
               equal(isReadOnly, that.isReadOnly);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), name, org, roleName, isReadOnly);
   }
   @Override
   public ToStringHelper string() {
      return super.string().add("name", name).add("org", org).add("roleName", roleName).add("isReadOnly", isReadOnly);
   }
}
