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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 *                 Represents vApp re-composition parameters.
 *             
 * 
 * <p>Java class for RecomposeVAppParams complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
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
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RecomposeVAppParams", propOrder = {
    "createItem",
    "deleteItem"
})
public class RecomposeVAppParams
    extends ComposeVAppParamsType<RecomposeVAppParams>

{
   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromRecomposeVAppParams(this);
   }

   public static class Builder extends ComposeVAppParamsType.Builder<RecomposeVAppParams> {
      
      private List<Vm> createItem;
      private List<Reference> deleteItem;

      /**
       * @see RecomposeVAppParams#getCreateItem()
       */
      public Builder createItem(List<Vm> createItem) {
         this.createItem = createItem;
         return this;
      }

      /**
       * @see RecomposeVAppParams#getDeleteItem()
       */
      public Builder deleteItem(List<Reference> deleteItem) {
         this.deleteItem = deleteItem;
         return this;
      }


      public RecomposeVAppParams build() {
         RecomposeVAppParams recomposeVAppParams = new RecomposeVAppParams(createItem, deleteItem);
         return recomposeVAppParams;
      }


      @Override
      public Builder fromComposeVAppParamsType(ComposeVAppParamsType<RecomposeVAppParams> in) {
          return Builder.class.cast(super.fromComposeVAppParamsType(in));
      }
      public Builder fromRecomposeVAppParams(RecomposeVAppParams in) {
         return fromComposeVAppParamsType(in)
            .createItem(in.getCreateItem())
            .deleteItem(in.getDeleteItem());
      }
   }

   private RecomposeVAppParams() {
      // For JAXB and builder use
   }

   private RecomposeVAppParams(List<Vm> createItem, List<Reference> deleteItem) {
      this.createItem = createItem;
      this.deleteItem = deleteItem;
   }


    @XmlElement(name = "CreateItem")
    protected List<Vm> createItem;
    @XmlElement(name = "DeleteItem")
    protected List<Reference> deleteItem;

    /**
     * Gets the value of the createItem property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the createItem property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCreateItem().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link VmType }
     * 
     * 
     */
    public List<Vm> getCreateItem() {
        if (createItem == null) {
            createItem = new ArrayList<Vm>();
        }
        return this.createItem;
    }

    /**
     * Gets the value of the deleteItem property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the deleteItem property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDeleteItem().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ReferenceType }
     * 
     * 
     */
    public List<Reference> getDeleteItem() {
        if (deleteItem == null) {
            deleteItem = new ArrayList<Reference>();
        }
        return this.deleteItem;
    }

   @Override
   public boolean equals(Object o) {
      if (this == o)
          return true;
      if (o == null || getClass() != o.getClass())
         return false;
      RecomposeVAppParams that = RecomposeVAppParams.class.cast(o);
      return equal(createItem, that.createItem) && 
           equal(deleteItem, that.deleteItem);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(createItem, 
           deleteItem);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("")
            .add("createItem", createItem)
            .add("deleteItem", deleteItem).toString();
   }

}
