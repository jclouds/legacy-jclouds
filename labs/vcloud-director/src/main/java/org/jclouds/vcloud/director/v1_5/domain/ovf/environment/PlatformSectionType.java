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
package org.jclouds.vcloud.director.v1_5.domain.ovf.environment;

import java.util.Set;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.jclouds.vcloud.director.v1_5.domain.cim.CimString;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;

/**
 * Information about deployment platform
 *
 * <pre>
 * &lt;complexType name="PlatformSection_Type">
 *   &lt;complexContent>
 *     &lt;extension base="{http://schemas.dmtf.org/ovf/environment/1}Section_Type">
 *       &lt;sequence>
 *         &lt;element name="Kind" type="{http://schemas.dmtf.org/wbem/wscim/1/common}cimString" minOccurs="0"/>
 *         &lt;element name="Version" type="{http://schemas.dmtf.org/wbem/wscim/1/common}cimString" minOccurs="0"/>
 *         &lt;element name="Vendor" type="{http://schemas.dmtf.org/wbem/wscim/1/common}cimString" minOccurs="0"/>
 *         &lt;element name="Locale" type="{http://schemas.dmtf.org/wbem/wscim/1/common}cimString" minOccurs="0"/>
 *         &lt;element name="Timezone" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement(name = "PlatformSection")
@XmlType(name = "PlatformSection_Type")
public class PlatformSectionType extends SectionType<PlatformSectionType> {

   @XmlElement(name = "Kind")
   protected CimString kind;
   @XmlElement(name = "Version")
   protected CimString version;
   @XmlElement(name = "Vendor")
   protected CimString vendor;
   @XmlElement(name = "Locale")
   protected CimString locale;
   @XmlElement(name = "Timezone")
   protected Integer timezone;
   @XmlAnyElement(lax = true)
   protected Set<Object> any = Sets.newLinkedHashSet();

   /**
    * Gets the value of the kind property.
    */
   public CimString getKind() {
      return kind;
   }

   /**
    * Gets the value of the version property.
    */
   public CimString getVersion() {
      return version;
   }

   /**
    * Gets the value of the vendor property.
    */
   public CimString getVendor() {
      return vendor;
   }

   /**
    * Gets the value of the locale property.
    */
   public CimString getLocale() {
      return locale;
   }

   /**
    * Gets the value of the timezone property.
    */
   public Integer getTimezone() {
      return timezone;
   }

   /**
    * Gets the value of the any property.
    */
   public Set<Object> getAny() {
      return any;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), version, vendor, timezone, locale, kind, any);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      PlatformSectionType that = (PlatformSectionType) obj;
      return super.equals(that) &&
            Objects.equal(this.version, that.version) &&
            Objects.equal(this.vendor, that.vendor) &&
            Objects.equal(this.timezone, that.timezone) &&
            Objects.equal(this.locale, that.locale) &&
            Objects.equal(this.kind, that.kind) &&
            Objects.equal(this.any, that.any);
   }

   @Override
   protected Objects.ToStringHelper string() {
      return super.string()
            .add("version", version).add("vendor", vendor).add("timezone", timezone)
            .add("locale", locale).add("kind", kind).add("any", any);
   }
}
