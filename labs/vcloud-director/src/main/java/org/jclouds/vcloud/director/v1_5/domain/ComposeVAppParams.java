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
public class ComposeVAppParams extends VAppCreationParamsType<ComposeVAppParams> {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromComposeVAppParams(this);
   }

   public static class Builder extends VAppCreationParamsType.Builder<ComposeVAppParams> {

      protected List<SourcedCompositionItemParam> sourcedItems = Lists.newArrayList();
      protected Boolean allEULAsAccepted;
      protected Boolean linkedClone;

      /**
       * @see ComposeVAppParams#getSourcedItems()
       */
      public Builder sourcedItems(List<SourcedCompositionItemParam> sourcedItems) {
         this.sourcedItems = Lists.newArrayList(checkNotNull(sourcedItems, "sourcedItems"));
         return this;
      }

      /**
       * @see ComposeVAppParams#getSourcedItem()
       */
      public Builder sourcedItem(SourcedCompositionItemParam sourcedItem) {
         this.sourcedItems.add(checkNotNull(sourcedItem, "sourcedItem"));
         return this;
      }

      /**
       * @see ComposeVAppParams#isAllEULAsAccepted()
       */
      public Builder allEULAsAccepted(Boolean allEULAsAccepted) {
         this.allEULAsAccepted = allEULAsAccepted;
         return this;
      }

      /**
       * @see ComposeVAppParams#isLinkedClone()
       */
      public Builder linkedClone(Boolean linkedClone) {
         this.linkedClone = linkedClone;
         return this;
      }

      @Override
      public ComposeVAppParams build() {
         return new ComposeVAppParams(description, name, vAppParent, instantiationParams, deploy, powerOn, sourcedItems, allEULAsAccepted, linkedClone);
      }

      /**
       * @see VAppCreationParamsType#getVAppParent()
       */
      @Override
      public Builder vAppParent(Reference vAppParent) {
         this.vAppParent = vAppParent;
         return this;
      }

      /**
       * @see VAppCreationParamsType#getInstantiationParams()
       */
      @Override
      public Builder instantiationParams(InstantiationParams instantiationParams) {
         this.instantiationParams = instantiationParams;
         return this;
      }

      /**
       * @see VAppCreationParamsType#isDeploy()
       */
      @Override
      public Builder deploy(Boolean deploy) {
         this.deploy = deploy;
         return this;
      }

      /**
       * @see VAppCreationParamsType#isDeploy()
       */
      @Override
      public Builder deploy() {
         this.deploy = Boolean.TRUE;
         return this;
      }

      /**
       * @see VAppCreationParamsType#isDeploy()
       */
      @Override
      public Builder notDeploy() {
         this.deploy = Boolean.FALSE;
         return this;
      }

      /**
       * @see VAppCreationParamsType#isPowerOn()
       */
      @Override
      public Builder powerOn(Boolean powerOn) {
         this.powerOn = powerOn;
         return this;
      }

      /**
       * @see VAppCreationParamsType#isPowerOn()
       */
      @Override
      public Builder powerOn() {
         this.powerOn = Boolean.TRUE;
         return this;
      }

      /**
       * @see VAppCreationParamsType#isPowerOn()
       */
      @Override
      public Builder notPowerOn() {
         this.powerOn = Boolean.FALSE;
         return this;
      }

      /**
       * @see ParamsType#getDescription()
       */
      @Override
      public Builder description(String description) {
         this.description = description;
         return this;
      }

      /**
       * @see ParamsType#getName()
       */
      @Override
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      @Override
      public Builder fromVAppCreationParamsType(VAppCreationParamsType<ComposeVAppParams> in) {
         return Builder.class.cast(super.fromVAppCreationParamsType(in));
      }

      public Builder fromComposeVAppParams(ComposeVAppParams in) {
         return fromVAppCreationParamsType(in).sourcedItems(in.getSourcedItems()).allEULAsAccepted(in.isAllEULAsAccepted()).linkedClone(in.isLinkedClone());
      }
   }

   public ComposeVAppParams(String description, String name, Reference vAppParent, InstantiationParams instantiationParams, Boolean deploy, Boolean powerOn,
                            List<SourcedCompositionItemParam> sourcedItems, Boolean allEULAsAccepted, Boolean linkedClone) {
      super(description, name, vAppParent, instantiationParams, deploy, powerOn);
      this.sourcedItems = ImmutableList.copyOf(sourcedItems);
      this.allEULAsAccepted = allEULAsAccepted;
      this.linkedClone = linkedClone;
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
