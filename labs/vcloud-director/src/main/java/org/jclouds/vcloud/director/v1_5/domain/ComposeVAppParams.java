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

import java.util.Collections;
import java.util.Set;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;


/**
 * Represents vApp composition parameters.
 * <p/>
 * <p/>
 * <p>Java class for ComposeVAppParams complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="ComposeVAppParams">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}VAppCreationParamsType">
 *       &lt;sequence>
 *         &lt;element name="SourcedItem" type="{http://www.vmware.com/vcloud/v1.5}SourcedCompositionItemParamType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.vmware.com/vcloud/v1.5}AllEULAsAccepted" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="linkedClone" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlType(name = "ComposeVAppParams", propOrder = {
      "sourcedItems",
      "allEULAsAccepted"
})
@XmlSeeAlso({
//    RecomposeVAppParamsType.class
})
public class ComposeVAppParams
      extends VAppCreationParamsType<ComposeVAppParams>

{
   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromComposeVAppParams(this);
   }

   public static class Builder extends VAppCreationParamsType.Builder<ComposeVAppParams> {

      private Set<SourcedCompositionItemParam> sourcedItems = Sets.newLinkedHashSet();
      private Boolean allEULAsAccepted;
      private Boolean linkedClone;

      /**
       * @see ComposeVAppParams#getSourcedItems()
       */
      public Builder sourcedItems(Set<SourcedCompositionItemParam> sourcedItems) {
         this.sourcedItems = checkNotNull(sourcedItems, "sourcedItems");
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
       * @see ComposeVAppParams#isLinkedClone() ()
       */
      public Builder linkedClone(Boolean linkedClone) {
         this.linkedClone = linkedClone;
         return this;
      }


      public ComposeVAppParams build() {
         return new ComposeVAppParams(description, name, vAppParent, instantiationParams, deploy, powerOn,
               sourcedItems, allEULAsAccepted, linkedClone);
      }


      @Override
      public Builder fromVAppCreationParamsType(VAppCreationParamsType<ComposeVAppParams> in) {
         return Builder.class.cast(super.fromVAppCreationParamsType(in));
      }

      public Builder fromComposeVAppParams(ComposeVAppParams in) {
         return fromVAppCreationParamsType(in)
               .sourcedItems(in.getSourcedItems())
               .allEULAsAccepted(in.isAllEULAsAccepted())
               .linkedClone(in.isLinkedClone());
      }
   }

   public ComposeVAppParams(String description, String name, Reference vAppParent, InstantiationParams instantiationParams,
                            Boolean deploy, Boolean powerOn, Set<SourcedCompositionItemParam> sourcedItems, Boolean allEULAsAccepted, Boolean linkedClone) {
      super(description, name, vAppParent, instantiationParams, deploy, powerOn);
      this.sourcedItems = ImmutableSet.copyOf(sourcedItems);
      this.allEULAsAccepted = allEULAsAccepted;
      this.linkedClone = linkedClone;
   }

   private ComposeVAppParams() {
      // For JAXB and builder use
   }


   @XmlElement(name = "SourcedItem")
   protected Set<SourcedCompositionItemParam> sourcedItems = Sets.newLinkedHashSet();
   @XmlElement(name = "AllEULAsAccepted")
   protected Boolean allEULAsAccepted;
   @XmlAttribute
   protected Boolean linkedClone;

   /**
    * Gets the value of the sourcedItem property.
    */
   public Set<SourcedCompositionItemParam> getSourcedItems() {
      return Collections.unmodifiableSet(this.sourcedItems);
   }

   /**
    * Used to confirm acceptance of all EULAs in a
    * vApp template. Instantiation fails if this
    * element is missing, empty, or set to false
    * and one or more EulaSection elements are
    * present.
    *
    * @return possible object is
    *         {@link Boolean }
    */
   public Boolean isAllEULAsAccepted() {
      return allEULAsAccepted;
   }

   /**
    * Gets the value of the linkedClone property.
    *
    * @return possible object is
    *         {@link Boolean }
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
      return equal(sourcedItems, that.sourcedItems) &&
            equal(allEULAsAccepted, that.allEULAsAccepted) &&
            equal(linkedClone, that.linkedClone);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(sourcedItems,
            allEULAsAccepted,
            linkedClone);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("")
            .add("sourcedItem", sourcedItems)
            .add("allEULAsAccepted", allEULAsAccepted)
            .add("linkedClone", linkedClone).toString();
   }

}
