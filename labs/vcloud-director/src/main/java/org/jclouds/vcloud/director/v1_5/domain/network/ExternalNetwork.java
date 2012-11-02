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
package org.jclouds.vcloud.director.v1_5.domain.network;

import static com.google.common.base.Objects.equal;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
/**
 * Admin representation of external network.
 * 
 * <pre>
 * &lt;complexType name="ExternalNetwork">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}NetworkType">
 *       &lt;sequence>
 *         &lt;element name="ProviderInfo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement(name = "ExternalNetwork")
@XmlType(propOrder = {
    "providerInfo"
})
public class ExternalNetwork extends Network {
   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromExternalNetwork(this);
   }
   
   public abstract static class Builder<T extends Builder<T>> extends Network.Builder<T> {
      private String providerInfo;

      /**
       * @see ExternalNetwork#getProviderInfo()
       */
      public T providerInfo(String providerInfo) {
         this.providerInfo = providerInfo;
         return self();
      }

      @Override
      public ExternalNetwork build() {
         return new ExternalNetwork(this);
      }
      
      public T fromExternalNetwork(ExternalNetwork in) {
         return fromNetwork(in)
            .providerInfo(in.getProviderInfo());
      }
   }
   
   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override protected ConcreteBuilder self() {
         return this;
      }
   }

   private ExternalNetwork() {
      // For JAXB
   }

   private ExternalNetwork(Builder<?> b) {
      super(b);
      providerInfo = b.providerInfo;
   }

    @XmlElement(name = "ProviderInfo", required = true)
    protected String providerInfo;

    /**
     * Gets the value of the providerInfo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProviderInfo() {
        return providerInfo;
    }

   @Override
   public boolean equals(Object o) {
      if (this == o)
          return true;
      if (o == null || getClass() != o.getClass())
         return false;
      ExternalNetwork that = ExternalNetwork.class.cast(o);
      return super.equals(that) && equal(providerInfo, that.providerInfo);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), providerInfo);
   }

   @Override
   public ToStringHelper string() {
      return super.string()
            .add("providerInfo", providerInfo);
   }

}
