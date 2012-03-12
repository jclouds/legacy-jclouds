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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.testng.collections.Lists;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableList;


/**
 * 
 *                 Represents a list of references to virtual data centers.
 *             
 * 
 * <p>Java class for Vdcs complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Vdcs">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}VCloudExtensibleType">
 *       &lt;sequence>
 *         &lt;element name="Vdc" type="{http://www.vmware.com/vcloud/v1.5}ReferenceType" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlRootElement(name = "Vdcs")
@XmlType(propOrder = {
    "vdcs"
})
public class Vdcs {
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromVdcs(this);
   }

   public static class Builder {
      
      private List<Reference> vdcs = Lists.newArrayList();

      /**
       * @see Vdcs#getVdc()
       */
      public Builder vdcs(List<Reference> vdcs) {
         this.vdcs = ImmutableList.copyOf(vdcs);
         return this;
      }
      
      /**
       * @see Vdcs#getVdc()
       */
      public Builder vdc(Reference vdc) {
         this.vdcs.add(checkNotNull(vdc, "vdc"));
         return this;
      }

      public Vdcs build() {
         return new Vdcs(vdcs);
      }

      public Builder fromVdcs(Vdcs in) {
         return vdcs(in.getVdcs());
      }
   }

   private Vdcs() {
      // For JAXB and builder use
   }

   private Vdcs(List<Reference> vdcs) {
      this.vdcs = vdcs;
   }


    @XmlElement(name = "Vdc")
    protected List<Reference> vdcs;

    /**
     * Gets the value of the vdc property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the vdc property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVdc().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ReferenceType }
     * 
     * 
     */
    public List<Reference> getVdcs() {
        if (vdcs == null) {
            vdcs = new ArrayList<Reference>();
        }
        return this.vdcs;
    }

   @Override
   public boolean equals(Object o) {
      if (this == o)
          return true;
      if (o == null || getClass() != o.getClass())
         return false;
      Vdcs that = Vdcs.class.cast(o);
      return equal(vdcs, that.vdcs);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(vdcs);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public ToStringHelper string() {
      return Objects.toStringHelper("")
            .add("vdcs", vdcs);
   }

}
