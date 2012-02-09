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
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;


/**
 * 
 *                 Represents a composition item.
 *             
 * 
 * <p>Java class for SourcedCompositionItemParam complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SourcedCompositionItemParam">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}VCloudExtensibleType">
 *       &lt;sequence>
 *         &lt;element name="Source" type="{http://www.vmware.com/vcloud/v1.5}ReferenceType"/>
 *         &lt;element name="VAppScopedLocalId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="InstantiationParams" type="{http://www.vmware.com/vcloud/v1.5}InstantiationParamsType" minOccurs="0"/>
 *         &lt;element name="NetworkAssignment" type="{http://www.vmware.com/vcloud/v1.5}NetworkAssignmentType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="sourceDelete" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SourcedCompositionItemParam", propOrder = {
    "source",
    "vAppScopedLocalId",
    "instantiationParams",
    "networkAssignment"
})
public class SourcedCompositionItemParam {
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromSourcedCompositionItemParam(this);
   }

   public static class Builder {
      
      private Reference source;
      private String vAppScopedLocalId;
      private InstantiationParams instantiationParams;
      private List<NetworkAssignment> networkAssignment;
      private Boolean sourceDelete;

      /**
       * @see SourcedCompositionItemParam#getSource()
       */
      public Builder source(Reference source) {
         this.source = source;
         return this;
      }

      /**
       * @see SourcedCompositionItemParam#getVAppScopedLocalId()
       */
      public Builder vAppScopedLocalId(String vAppScopedLocalId) {
         this.vAppScopedLocalId = vAppScopedLocalId;
         return this;
      }

      /**
       * @see SourcedCompositionItemParam#getInstantiationParams()
       */
      public Builder instantiationParams(InstantiationParams instantiationParams) {
         this.instantiationParams = instantiationParams;
         return this;
      }

      /**
       * @see SourcedCompositionItemParam#getNetworkAssignment()
       */
      public Builder networkAssignment(List<NetworkAssignment> networkAssignment) {
         this.networkAssignment = networkAssignment;
         return this;
      }

      /**
       * @see SourcedCompositionItemParam#getSourceDelete()
       */
      public Builder sourceDelete(Boolean sourceDelete) {
         this.sourceDelete = sourceDelete;
         return this;
      }


      public SourcedCompositionItemParam build() {
         SourcedCompositionItemParam sourcedCompositionItemParam = new SourcedCompositionItemParam(networkAssignment);
         sourcedCompositionItemParam.setSource(source);
         sourcedCompositionItemParam.setVAppScopedLocalId(vAppScopedLocalId);
         sourcedCompositionItemParam.setInstantiationParams(instantiationParams);
         sourcedCompositionItemParam.setSourceDelete(sourceDelete);
         return sourcedCompositionItemParam;
      }


      public Builder fromSourcedCompositionItemParam(SourcedCompositionItemParam in) {
         return source(in.getSource())
            .vAppScopedLocalId(in.getVAppScopedLocalId())
            .instantiationParams(in.getInstantiationParams())
            .networkAssignment(in.getNetworkAssignment())
            .sourceDelete(in.isSourceDelete());
      }
   }

   private SourcedCompositionItemParam() {
      // For JAXB and builder use
   }

   private SourcedCompositionItemParam(List<NetworkAssignment> networkAssignment) {
      this.networkAssignment = networkAssignment;
   }


    @XmlElement(name = "Source", required = true)
    protected Reference source;
    @XmlElement(name = "VAppScopedLocalId")
    protected String vAppScopedLocalId;
    @XmlElement(name = "InstantiationParams")
    protected InstantiationParams instantiationParams;
    @XmlElement(name = "NetworkAssignment")
    protected List<NetworkAssignment> networkAssignment;
    @XmlAttribute
    protected Boolean sourceDelete;

    /**
     * Gets the value of the source property.
     * 
     * @return
     *     possible object is
     *     {@link Reference }
     *     
     */
    public Reference getSource() {
        return source;
    }

    /**
     * Sets the value of the source property.
     * 
     * @param value
     *     allowed object is
     *     {@link Reference }
     *     
     */
    public void setSource(Reference value) {
        this.source = value;
    }

    /**
     * Gets the value of the vAppScopedLocalId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVAppScopedLocalId() {
        return vAppScopedLocalId;
    }

    /**
     * Sets the value of the vAppScopedLocalId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVAppScopedLocalId(String value) {
        this.vAppScopedLocalId = value;
    }

    /**
     * Gets the value of the instantiationParams property.
     * 
     * @return
     *     possible object is
     *     {@link InstantiationParams }
     *     
     */
    public InstantiationParams getInstantiationParams() {
        return instantiationParams;
    }

    /**
     * Sets the value of the instantiationParams property.
     * 
     * @param value
     *     allowed object is
     *     {@link InstantiationParams }
     *     
     */
    public void setInstantiationParams(InstantiationParams value) {
        this.instantiationParams = value;
    }

    /**
     * Gets the value of the networkAssignment property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the networkAssignment property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNetworkAssignment().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link NetworkAssignmentType }
     * 
     * 
     */
    public List<NetworkAssignment> getNetworkAssignment() {
        if (networkAssignment == null) {
            networkAssignment = new ArrayList<NetworkAssignment>();
        }
        return this.networkAssignment;
    }

    /**
     * Gets the value of the sourceDelete property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSourceDelete() {
        return sourceDelete;
    }

    /**
     * Sets the value of the sourceDelete property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSourceDelete(Boolean value) {
        this.sourceDelete = value;
    }

   @Override
   public boolean equals(Object o) {
      if (this == o)
          return true;
      if (o == null || getClass() != o.getClass())
         return false;
      SourcedCompositionItemParam that = SourcedCompositionItemParam.class.cast(o);
      return equal(source, that.source) && 
           equal(vAppScopedLocalId, that.vAppScopedLocalId) && 
           equal(instantiationParams, that.instantiationParams) && 
           equal(networkAssignment, that.networkAssignment) && 
           equal(sourceDelete, that.sourceDelete);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(source, 
           vAppScopedLocalId, 
           instantiationParams, 
           networkAssignment, 
           sourceDelete);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("")
            .add("source", source)
            .add("vAppScopedLocalId", vAppScopedLocalId)
            .add("instantiationParams", instantiationParams)
            .add("networkAssignment", networkAssignment)
            .add("sourceDelete", sourceDelete).toString();
   }

}
