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

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.Vm;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.Lists;

/**
 * Represents vApp re-composition parameters.
 * 
 * <pre>
 * &lt;complexType name="RecomposeVAppParams">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}ComposeVAppParamsType">
 *       &lt;sequence>
 *         &lt;element name="CreateItem" type="{http://www.vmware.com/vcloud/v1.5}VmType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="DeleteItem" type="{http://www.vmware.com/vcloud/v1.5}ReferenceType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement(name = "RecomposeVAppParams")
@XmlType(name = "RecomposeVAppParamsType")
public class RecomposeVAppParams extends ComposeVAppParams {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return builder().fromRecomposeVAppParams(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public abstract static class Builder<B extends Builder<B>> extends ComposeVAppParams.Builder<B> {

      private List<Vm> createItem;
      private List<Reference> removeItem;

      /**
       * @see RecomposeVAppParams#getCreateItem()
       */
      public B createItem(List<Vm> createItem) {
         this.createItem = createItem;
         return self();
      }

      /**
       * @see RecomposeVAppParams#getDeleteItem()
       */
      public B removeItem(List<Reference> removeItem) {
         this.removeItem = removeItem;
         return self();
      }

      @Override
      public RecomposeVAppParams build() {
         return new RecomposeVAppParams(this);
      }

      public B fromRecomposeVAppParams(RecomposeVAppParams in) {
         return fromComposeVAppParams(in).createItem(in.getCreateItem()).removeItem(in.getDeleteItem());
      }
   }

   private RecomposeVAppParams() {
      // For JAXB and B use
   }

   private RecomposeVAppParams(Builder<?> builder) {
      super(builder);
      this.createItem = builder.createItem;
      this.removeItem = builder.removeItem;
   }

   @XmlElement(name = "CreateItem")
   protected List<Vm> createItem;
   @XmlElement(name = "DeleteItem")
   protected List<Reference> removeItem;

   /**
    * Gets the value of the createItem property.
    */
   public List<Vm> getCreateItem() {
      if (createItem == null) {
         createItem = Lists.newArrayList();
      }
      return this.createItem;
   }

   /**
    * Gets the value of the removeItem property.
    */
   public List<Reference> getDeleteItem() {
      if (removeItem == null) {
         removeItem = Lists.newArrayList();
      }
      return this.removeItem;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      RecomposeVAppParams that = RecomposeVAppParams.class.cast(o);
      return super.equals(that) &&
            equal(this.createItem, that.createItem) && equal(this.removeItem, that.removeItem);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), createItem, removeItem);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("createItem", createItem).add("removeItem", removeItem);
   }

}
