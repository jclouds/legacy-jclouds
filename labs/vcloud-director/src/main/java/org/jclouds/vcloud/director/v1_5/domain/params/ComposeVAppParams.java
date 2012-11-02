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
package org.jclouds.vcloud.director.v1_5.domain.params;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * Represents vApp composition parameters.
 * 
 * <pre>
 * &lt;complexType name="ComposeVAppParams" /&gt;
 * </pre>
 * 
 * @author grkvlt@apache.org
 */
@XmlRootElement(name = "ComposeVAppParams")
@XmlType(name = "ComposeVAppParamsType")
public class ComposeVAppParams extends VAppCreationParams {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return builder().fromComposeVAppParams(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public abstract static class Builder<B extends Builder<B>> extends VAppCreationParams.Builder<B> {

      private List<SourcedCompositionItemParam> sourcedItems = Lists.newArrayList();
      private Boolean allEULAsAccepted;
      private Boolean linkedClone;

      /**
       * @see ComposeVAppParams#getSourcedItems()
       */
      public B sourcedItems(List<SourcedCompositionItemParam> sourcedItems) {
         this.sourcedItems = Lists.newArrayList(checkNotNull(sourcedItems, "sourcedItems"));
         return self();
      }

      /**
       * @see ComposeVAppParams#getSourcedItem()
       */
      public B sourcedItem(SourcedCompositionItemParam sourcedItem) {
         this.sourcedItems.add(checkNotNull(sourcedItem, "sourcedItem"));
         return self();
      }

      /**
       * @see ComposeVAppParams#isAllEULAsAccepted()
       */
      public B allEULAsAccepted(Boolean allEULAsAccepted) {
         this.allEULAsAccepted = allEULAsAccepted;
         return self();
      }

      /**
       * @see ComposeVAppParams#isLinkedClone()
       */
      public B linkedClone(Boolean linkedClone) {
         this.linkedClone = linkedClone;
         return self();
      }

      @Override
      public ComposeVAppParams build() {
         return new ComposeVAppParams(this);
      }

      public B fromComposeVAppParams(ComposeVAppParams in) {
         return fromVAppCreationParamsType(in).sourcedItems(in.getSourcedItems()).allEULAsAccepted(in.isAllEULAsAccepted()).linkedClone(in.isLinkedClone());
      }
   }

   public ComposeVAppParams(Builder<?> builder) {
      super(builder);
      this.sourcedItems = ImmutableList.copyOf(builder.sourcedItems);
      this.allEULAsAccepted = builder.allEULAsAccepted;
      this.linkedClone = builder.linkedClone;
   }

   protected ComposeVAppParams() {
      // for JAXB
   }

   @XmlElement(name = "SourcedItem")
   protected List<SourcedCompositionItemParam> sourcedItems = Lists.newArrayList();
   @XmlElement(name = "AllEULAsAccepted")
   protected Boolean allEULAsAccepted;
   @XmlAttribute
   protected Boolean linkedClone;

   /**
    * Gets the value of the sourcedItems property.
    */
   public List<SourcedCompositionItemParam> getSourcedItems() {
      return ImmutableList.copyOf(sourcedItems);
   }

   /**
    * Used to confirm acceptance of all EULAs in a vApp template.
    *
    * Instantiation fails if this element is missing, empty, or set to
    * false and one or more EulaSection elements are present.
    */
   public Boolean isAllEULAsAccepted() {
      return allEULAsAccepted;
   }

   /**
    * Gets the value of the linkedClone property.
    */
   public Boolean isLinkedClone() {
      return linkedClone;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      ComposeVAppParams that = ComposeVAppParams.class.cast(o);
      return super.equals(that) &&
            equal(this.sourcedItems, that.sourcedItems) && equal(this.allEULAsAccepted, that.allEULAsAccepted) && equal(this.linkedClone, that.linkedClone);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), sourcedItems, allEULAsAccepted, linkedClone);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("sourcedItems", sourcedItems).add("allEULAsAccepted", allEULAsAccepted).add("linkedClone", linkedClone);
   }

}
