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

import java.util.Set;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.jclouds.vcloud.director.v1_5.domain.query.ContainerType;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.Sets;

/**
 * This is the container for returned elements in referenceView
 *
 * <pre>
 * &lt;complexType name="References">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}ContainerType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.vmware.com/vcloud/v1.5}Reference" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement(name = "References")
@XmlType(propOrder = {
    "references"
})
public class References extends ContainerType {
   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromReferences(this);
   }
   
   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   public abstract static class Builder<T extends Builder<T>> extends ContainerType.Builder<T> {
      private Set<Reference> references;

      /**
       * @see References#getReference()
       */
      public T references(Set<Reference> references) {
         this.references = references;
         return self();
      }

      @Override
      public References build() {
         return new References(this);
      }
      
      public T fromReferences(References in) {
         return fromContainerType(in)
            .references(in.getReferences());
      }
   }

   private References(Builder<?> b) {
      super(b);
      this.references = b.references;
   }


    @XmlElementRef(name = "Reference", namespace = "http://www.vmware.com/vcloud/v1.5")
    protected Set<Reference> references = Sets.newLinkedHashSet();

    /**
     * Gets the value of the reference property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the reference property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReference().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link ReferenceType }{@code >}
     * 
     * 
     */
    public Set<Reference> getReferences() {
        return this.references;
    }

   @Override
   public boolean equals(Object o) {
      if (this == o)
          return true;
      if (o == null || getClass() != o.getClass())
         return false;
      References that = References.class.cast(o);
      return super.equals(that) && equal(references, that.references);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), references);
   }

   @Override
   public ToStringHelper string() {
      return super.string()
            .add("references", references);
   }

}
