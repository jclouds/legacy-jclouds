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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;


/**
 * 
 *                 Represents vApp composition parameters.
 *             
 * 
 * <p>Java class for ComposeVAppParams complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
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
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ComposeVAppParams", propOrder = {
    "sourcedItem",
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
      
      private List<SourcedCompositionItemParam> sourcedItem;
      private Boolean allEULAsAccepted;
      private Boolean linkedClone;

      /**
       * @see ComposeVAppParams#getSourcedItem()
       */
      public Builder sourcedItem(List<SourcedCompositionItemParam> sourcedItem) {
         this.sourcedItem = sourcedItem;
         return this;
      }

      /**
       * @see ComposeVAppParams#getAllEULAsAccepted()
       */
      public Builder allEULAsAccepted(Boolean allEULAsAccepted) {
         this.allEULAsAccepted = allEULAsAccepted;
         return this;
      }

      /**
       * @see ComposeVAppParams#getLinkedClone()
       */
      public Builder linkedClone(Boolean linkedClone) {
         this.linkedClone = linkedClone;
         return this;
      }


      public ComposeVAppParams build() {
         ComposeVAppParams composeVAppParams = new ComposeVAppParams(sourcedItem);
         composeVAppParams.setAllEULAsAccepted(allEULAsAccepted);
         composeVAppParams.setLinkedClone(linkedClone);
         return composeVAppParams;
      }


      @Override
      public Builder fromVAppCreationParamsType(VAppCreationParamsType<ComposeVAppParams> in) {
          return Builder.class.cast(super.fromVAppCreationParamsType(in));
      }
      public Builder fromComposeVAppParams(ComposeVAppParams in) {
         return fromVAppCreationParamsType(in)
            .sourcedItem(in.getSourcedItem())
            .allEULAsAccepted(in.isAllEULAsAccepted())
            .linkedClone(in.isLinkedClone());
      }
   }

   private ComposeVAppParams() {
      // For JAXB and builder use
   }

   private ComposeVAppParams(List<SourcedCompositionItemParam> sourcedItem) {
      this.sourcedItem = sourcedItem;
   }


    @XmlElement(name = "SourcedItem")
    protected List<SourcedCompositionItemParam> sourcedItem;
    @XmlElement(name = "AllEULAsAccepted")
    protected Boolean allEULAsAccepted;
    @XmlAttribute
    protected Boolean linkedClone;

    /**
     * Gets the value of the sourcedItem property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the sourcedItem property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSourcedItem().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SourcedCompositionItemParamType }
     * 
     * 
     */
    public List<SourcedCompositionItemParam> getSourcedItem() {
        if (sourcedItem == null) {
            sourcedItem = new ArrayList<SourcedCompositionItemParam>();
        }
        return this.sourcedItem;
    }

    /**
     * 
     *                                 Used to confirm acceptance of all EULAs in a
     *                                 vApp template. Instantiation fails if this
     *                                 element is missing, empty, or set to false
     *                                 and one or more EulaSection elements are
     *                                 present.
     *                             
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAllEULAsAccepted() {
        return allEULAsAccepted;
    }

    /**
     * Sets the value of the allEULAsAccepted property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAllEULAsAccepted(Boolean value) {
        this.allEULAsAccepted = value;
    }

    /**
     * Gets the value of the linkedClone property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isLinkedClone() {
        return linkedClone;
    }

    /**
     * Sets the value of the linkedClone property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setLinkedClone(Boolean value) {
        this.linkedClone = value;
    }

   @Override
   public boolean equals(Object o) {
      if (this == o)
          return true;
      if (o == null || getClass() != o.getClass())
         return false;
      ComposeVAppParams that = ComposeVAppParams.class.cast(o);
      return equal(sourcedItem, that.sourcedItem) && 
           equal(allEULAsAccepted, that.allEULAsAccepted) && 
           equal(linkedClone, that.linkedClone);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(sourcedItem, 
           allEULAsAccepted, 
           linkedClone);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("")
            .add("sourcedItem", sourcedItem)
            .add("allEULAsAccepted", allEULAsAccepted)
            .add("linkedClone", linkedClone).toString();
   }

}
