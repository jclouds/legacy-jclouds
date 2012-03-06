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
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;


/**
 * Represents a composition item.
 * <p/>
 * <p/>
 * <p>Java class for SourcedCompositionItemParam complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
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
 */
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
      private Set<NetworkAssignment> networkAssignments = Sets.newLinkedHashSet();
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
       * @see SourcedCompositionItemParam#getNetworkAssignments()
       */
      public Builder networkAssignments(Set<NetworkAssignment> networkAssignments) {
         this.networkAssignments = checkNotNull(networkAssignments, "networkAssignments");
         return this;
      }

      /**
       * @see SourcedCompositionItemParam#isSourceDelete()
       */
      public Builder sourceDelete(Boolean sourceDelete) {
         this.sourceDelete = sourceDelete;
         return this;
      }

      public SourcedCompositionItemParam build() {
         return new SourcedCompositionItemParam(source, vAppScopedLocalId, instantiationParams, networkAssignments, sourceDelete);
      }

      public Builder fromSourcedCompositionItemParam(SourcedCompositionItemParam in) {
         return source(in.getSource())
               .vAppScopedLocalId(in.getVAppScopedLocalId())
               .instantiationParams(in.getInstantiationParams())
               .networkAssignments(in.getNetworkAssignments())
               .sourceDelete(in.isSourceDelete());
      }
   }

   public SourcedCompositionItemParam(Reference source, String vAppScopedLocalId, InstantiationParams instantiationParams,
                                      Set<NetworkAssignment> networkAssignments, Boolean sourceDelete) {
      this.source = source;
      this.vAppScopedLocalId = vAppScopedLocalId;
      this.instantiationParams = instantiationParams;
      this.networkAssignments = ImmutableSet.copyOf(networkAssignments);
      this.sourceDelete = sourceDelete;
   }

   private SourcedCompositionItemParam() {
      // for JAXB
   }

   @XmlElement(name = "Source", required = true)
   protected Reference source;
   @XmlElement(name = "VAppScopedLocalId")
   protected String vAppScopedLocalId;
   @XmlElement(name = "InstantiationParams")
   protected InstantiationParams instantiationParams;
   @XmlElement(name = "NetworkAssignment")
   protected Set<NetworkAssignment> networkAssignments = Sets.newLinkedHashSet();
   @XmlAttribute
   protected Boolean sourceDelete;

   /**
    * Gets the value of the source property.
    *
    * @return possible object is
    *         {@link Reference }
    */
   public Reference getSource() {
      return source;
   }


   /**
    * Gets the value of the vAppScopedLocalId property.
    *
    * @return possible object is
    *         {@link String }
    */
   public String getVAppScopedLocalId() {
      return vAppScopedLocalId;
   }

   /**
    * Gets the value of the instantiationParams property.
    *
    * @return possible object is
    *         {@link InstantiationParams }
    */
   public InstantiationParams getInstantiationParams() {
      return instantiationParams;
   }

   /**
    * Gets the value of the networkAssignment property.
    */
   public Set<NetworkAssignment> getNetworkAssignments() {
      return Collections.unmodifiableSet(this.networkAssignments);
   }

   /**
    * Gets the value of the sourceDelete property.
    *
    * @return possible object is
    *         {@link Boolean }
    */
   public Boolean isSourceDelete() {
      return sourceDelete;
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
            equal(networkAssignments, that.networkAssignments) &&
            equal(sourceDelete, that.sourceDelete);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(source,
            vAppScopedLocalId,
            instantiationParams,
            networkAssignments,
            sourceDelete);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("")
            .add("source", source)
            .add("vAppScopedLocalId", vAppScopedLocalId)
            .add("instantiationParams", instantiationParams)
            .add("networkAssignments", networkAssignments)
            .add("sourceDelete", sourceDelete).toString();
   }

}
