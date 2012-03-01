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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;


/**
 * Represents a list of supported VM hardware versions.
 * <p/>
 * <p/>
 * <p>Java class for SupportedHardwareVersions complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
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
 */
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

      private Set<String> supportedHardwareVersions = Sets.newLinkedHashSet();

      /**
       * @see SupportedHardwareVersions#getSupportedHardwareVersions()
       */
      public Builder supportedHardwareVersions(Set<String> supportedHardwareVersions) {
         this.supportedHardwareVersions = Sets.newLinkedHashSet(checkNotNull(supportedHardwareVersions, "supportedHardwareVersions"));
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

   private SupportedHardwareVersions(Set<String> supportedHardwareVersions) {
      this.supportedHardwareVersions = supportedHardwareVersions;
   }


   @XmlElement(name = "SupportedHardwareVersion")
   protected Set<String> supportedHardwareVersions = Sets.newLinkedHashSet();

   /**
    * Gets the value of the supportedHardwareVersion property.
    */
   public Set<String> getSupportedHardwareVersions() {
      return Collections.unmodifiableSet(supportedHardwareVersions);
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
