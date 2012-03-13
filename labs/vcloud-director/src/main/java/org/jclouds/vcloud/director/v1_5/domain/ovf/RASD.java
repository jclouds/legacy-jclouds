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
package org.jclouds.vcloud.director.v1_5.domain.ovf;

import static org.jclouds.vcloud.director.v1_5.VCloudDirectorConstants.VCLOUD_OVF_NS;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.jclouds.vcloud.director.v1_5.domain.cim.CIMResourceAllocationSettingDataType;

/**
 * Wrapper for CIM_ResourceAllocationSettingData_Type
 * 
 * <pre>
 * &lt;complexType name="RASD_Type" /&gt;
 * </pre>
 * 
 * @author grkvlt@apache.org
 */
@XmlType(name = "RASD_Type")
public class RASD extends CIMResourceAllocationSettingDataType {

   // TODO Add toString, hashCode and equals

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return builder().fromRASD(this);
   }

   public static class Builder<B extends Builder<B>> extends CIMResourceAllocationSettingDataType.Builder<B> {

      private Boolean required;
      private String configuration;
      private String bound;

      public B required(Boolean val) {
         this.required = val;
         return self();
      }

      public B configuration(String val) {
         this.configuration = val;
         return self();
      }

      public B bound(String val) {
         this.bound = val;
         return self();
      }

      public B fromRASD(RASD val) {
         return fromCIMResourceAllocationSettingDataType(val).required(val.isRequired()).configuration(val.getConfiguration()).bound(val.getBound());
      }

      @Override
      public RASD build() {
         return new RASD(this);
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }

   @XmlAttribute(namespace = VCLOUD_OVF_NS)
   private Boolean required;
   @XmlAttribute(namespace = VCLOUD_OVF_NS)
   private String configuration;
   @XmlAttribute(namespace = VCLOUD_OVF_NS)
   private String bound;

   protected RASD() {
      // For JAXB
   }

   protected RASD(Builder<?> builder) {
      super(builder);
      this.required = builder.required;
      this.configuration = builder.configuration;
      this.bound = builder.bound;
   }

   /**
    * Gets the value of the required property.
    * 
    * @return possible object is {@link Boolean }
    */
   public boolean isRequired() {
      if (required == null) {
         return true;
      } else {
         return required;
      }
   }

   /**
    * Gets the value of the configuration property.
    * 
    * @return possible object is {@link String }
    */
   public String getConfiguration() {
      return configuration;
   }

   /**
    * Gets the value of the bound property.
    * 
    * @return possible object is {@link String }
    */
   public String getBound() {
      return bound;
   }
}
