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

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.testng.collections.Lists;

import com.google.common.base.Objects;


/**
 * 
 *                 Represents a list of supported VM hardware versions.
 *             
 * 
 * <p>Java class for SupportedHardwareVersions complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SupportedHardwareVersions">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}VCloudExtensibleType">
 *       &lt;sequence>
 *         &lt;element name="SupportedHardwareVersion" type="{http://www.vmware.com/vcloud/v1.5}SupportedHardwareVersionType" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "SupportedHardwareVersions", propOrder = {
    "supportedHardwareVersions"
})
public class SupportedHardwareVersions {
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromSupportedHardwareVersions(this);
   }

   public static class Builder {
      
      private List<String> supportedHardwareVersions = Lists.newArrayList();

      /**
       * @see SupportedHardwareVersions#getSupportedHardwareVersions()
       */
      public Builder supportedHardwareVersions(List<String> supportedHardwareVersions) {
         this.supportedHardwareVersions = Lists.newArrayList(checkNotNull(supportedHardwareVersions, "supportedHardwareVersions"));
         return this;
      }
      
      /**
       * @see SupportedHardwareVersions#getSupportedHardwareVersions()
       */
      public Builder supportedHardwareVersion(String supportedHardwareVersion) {
         supportedHardwareVersions.add(checkNotNull(supportedHardwareVersion, "supportedHardwareVersion"));
         return this;
      }

      public SupportedHardwareVersions build() {
         SupportedHardwareVersions supportedHardwareVersions = new SupportedHardwareVersions(this.supportedHardwareVersions);
         return supportedHardwareVersions;
      }


      public Builder fromSupportedHardwareVersions(SupportedHardwareVersions in) {
         return supportedHardwareVersions(in.getSupportedHardwareVersions());
      }
   }

   private SupportedHardwareVersions() {
      // For JAXB and builder use
   }

   private SupportedHardwareVersions(List<String> supportedHardwareVersions) {
      this.supportedHardwareVersions = supportedHardwareVersions;
   }


    @XmlElement(name = "SupportedHardwareVersion")
    protected List<String> supportedHardwareVersions;

    /**
     * Gets the value of the supportedHardwareVersion property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the supportedHardwareVersion property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSupportedHardwareVersion().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getSupportedHardwareVersions() {
        if (supportedHardwareVersions == null) {
            supportedHardwareVersions = Lists.newArrayList();
        }
        return this.supportedHardwareVersions;
    }

   @Override
   public boolean equals(Object o) {
      if (this == o)
          return true;
      if (o == null || getClass() != o.getClass())
         return false;
      SupportedHardwareVersions that = SupportedHardwareVersions.class.cast(o);
      return equal(supportedHardwareVersions, that.supportedHardwareVersions);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(supportedHardwareVersions);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("")
            .add("supportedHardwareVersion", supportedHardwareVersions).toString();
   }

}
