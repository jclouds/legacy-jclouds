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

import java.net.URI;
import java.util.Collections;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import org.jclouds.vcloud.director.v1_5.domain.ovf.SectionType;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * Represents a guest customization settings.
 *
 * <pre>
 * &lt;complexType name="GuestCustomizationSection">
 *   &lt;complexContent>
 *     &lt;extension base="{http://schemas.dmtf.org/ovf/envelope/1}Section_Type">
 *       &lt;sequence>
 *         &lt;element name="Enabled" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ChangeSid" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="VirtualMachineId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="JoinDomainEnabled" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="UseOrgSettings" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="DomainName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DomainUserName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DomainUserPassword" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="AdminPasswordEnabled" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="AdminPasswordAuto" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="AdminPassword" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ResetPasswordRequired" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="CustomizationScript" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ComputerName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Link" type="{http://www.vmware.com/vcloud/v1.5}LinkType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="href" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;anyAttribute processContents='lax'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement(name = "GuestCustomizationSection")
@XmlType(propOrder = {
      "enabled",
      "changeSid",
      "virtualMachineId",
      "joinDomainEnabled",
      "useOrgSettings",
      "domainName",
      "domainUserName",
      "domainUserPassword",
      "adminPasswordEnabled",
      "adminPasswordAuto",
      "adminPassword",
      "resetPasswordRequired",
      "customizationScript",
      "computerName",
      "links"
})
public class GuestCustomizationSection extends SectionType {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return builder().fromGuestCustomizationSection(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public static class Builder<B extends Builder<B>> extends SectionType.Builder<B> {
      private Boolean enabled;
      private Boolean changeSid;
      private String virtualMachineId;
      private Boolean joinDomainEnabled;
      private Boolean useOrgSettings;
      private String domainName;
      private String domainUserName;
      private String domainUserPassword;
      private Boolean adminPasswordEnabled;
      private Boolean adminPasswordAuto;
      private String adminPassword;
      private Boolean resetPasswordRequired;
      private String customizationScript;
      private String computerName;
      private Set<Link> links = Sets.newLinkedHashSet();
      private URI href;
      private String type;

      /**
       * @see org.jclouds.vcloud.director.v1_5.domain.GuestCustomizationSection#isEnabled()
       */
      public B enabled(Boolean enabled) {
         this.enabled = enabled;
         return self();
      }

      /**
       * @see org.jclouds.vcloud.director.v1_5.domain.GuestCustomizationSection#isChangeSid()
       */
      public B changeSid(Boolean changeSid) {
         this.changeSid = changeSid;
         return self();
      }

      /**
       * @see org.jclouds.vcloud.director.v1_5.domain.GuestCustomizationSection#getVirtualMachineId()
       */
      public B virtualMachineId(String virtualMachineId) {
         this.virtualMachineId = virtualMachineId;
         return self();
      }

      /**
       * @see org.jclouds.vcloud.director.v1_5.domain.GuestCustomizationSection#isJoinDomainEnabled()
       */
      public B joinDomainEnabled(Boolean joinDomainEnabled) {
         this.joinDomainEnabled = joinDomainEnabled;
         return self();
      }

      /**
       * @see org.jclouds.vcloud.director.v1_5.domain.GuestCustomizationSection#isUseOrgSettings()
       */
      public B useOrgSettings(Boolean useOrgSettings) {
         this.useOrgSettings = useOrgSettings;
         return self();
      }

      /**
       * @see org.jclouds.vcloud.director.v1_5.domain.GuestCustomizationSection#getDomainName()
       */
      public B domainName(String domainName) {
         this.domainName = domainName;
         return self();
      }

      /**
       * @see org.jclouds.vcloud.director.v1_5.domain.GuestCustomizationSection#getDomainUserName()
       */
      public B domainUserName(String domainUserName) {
         this.domainUserName = domainUserName;
         return self();
      }

      /**
       * @see org.jclouds.vcloud.director.v1_5.domain.GuestCustomizationSection#getDomainUserPassword()
       */
      public B domainUserPassword(String domainUserPassword) {
         this.domainUserPassword = domainUserPassword;
         return self();
      }

      /**
       * @see org.jclouds.vcloud.director.v1_5.domain.GuestCustomizationSection#isAdminPasswordEnabled()
       */
      public B adminPasswordEnabled(Boolean adminPasswordEnabled) {
         this.adminPasswordEnabled = adminPasswordEnabled;
         return self();
      }

      /**
       * @see org.jclouds.vcloud.director.v1_5.domain.GuestCustomizationSection#isAdminPasswordAuto()
       */
      public B adminPasswordAuto(Boolean adminPasswordAuto) {
         this.adminPasswordAuto = adminPasswordAuto;
         return self();
      }

      /**
       * @see org.jclouds.vcloud.director.v1_5.domain.GuestCustomizationSection#getAdminPassword()
       */
      public B adminPassword(String adminPassword) {
         this.adminPassword = adminPassword;
         return self();
      }

      /**
       * @see org.jclouds.vcloud.director.v1_5.domain.GuestCustomizationSection#isResetPasswordRequired()
       */
      public B resetPasswordRequired(Boolean resetPasswordRequired) {
         this.resetPasswordRequired = resetPasswordRequired;
         return self();
      }

      /**
       * @see org.jclouds.vcloud.director.v1_5.domain.GuestCustomizationSection#getCustomizationScript()
       */
      public B customizationScript(String customizationScript) {
         this.customizationScript = customizationScript;
         return self();
      }

      /**
       * @see org.jclouds.vcloud.director.v1_5.domain.GuestCustomizationSection#getComputerName()
       */
      public B computerName(String computerName) {
         this.computerName = computerName;
         return self();
      }

      /**
       * @see org.jclouds.vcloud.director.v1_5.domain.GuestCustomizationSection#getLinks()
       */
      public B links(Set<Link> links) {
         this.links = checkNotNull(links, "links");
         return self();
      }

      /**
       * @see org.jclouds.vcloud.director.v1_5.domain.GuestCustomizationSection#getHref()
       */
      public B href(URI href) {
         this.href = href;
         return self();
      }

      /**
       * @see org.jclouds.vcloud.director.v1_5.domain.GuestCustomizationSection#getType()
       */
      public B type(String type) {
         this.type = type;
         return self();
      }

      @Override
      public GuestCustomizationSection build() {
         return new GuestCustomizationSection(this);
      }

      public B fromGuestCustomizationSection(GuestCustomizationSection in) {
         return fromSectionType(in)
               .enabled(in.isEnabled())
               .changeSid(in.isChangeSid())
               .virtualMachineId(in.getVirtualMachineId())
               .joinDomainEnabled(in.isJoinDomainEnabled())
               .useOrgSettings(in.isUseOrgSettings())
               .domainName(in.getDomainName())
               .domainUserName(in.getDomainUserName())
               .domainUserPassword(in.getDomainUserPassword())
               .adminPasswordEnabled(in.isAdminPasswordEnabled())
               .adminPasswordAuto(in.isAdminPasswordAuto())
               .adminPassword(in.getAdminPassword())
               .resetPasswordRequired(in.isResetPasswordRequired())
               .customizationScript(in.getCustomizationScript())
               .computerName(in.getComputerName())
               .links(in.getLinks())
               .href(in.getHref())
               .type(in.getType());
      }
   }

   private GuestCustomizationSection(Builder<?> builder) {
      super(builder);
      this.enabled = builder.enabled;
      this.changeSid = builder.changeSid;
      this.virtualMachineId = builder.virtualMachineId;
      this.joinDomainEnabled = builder.joinDomainEnabled;
      this.useOrgSettings = builder.useOrgSettings;
      this.domainName = builder.domainName;
      this.domainUserName = builder.domainUserName;
      this.domainUserPassword = builder.domainUserPassword;
      this.adminPasswordEnabled = builder.adminPasswordEnabled;
      this.adminPasswordAuto = builder.adminPasswordAuto;
      this.adminPassword = builder.adminPassword;
      this.resetPasswordRequired = builder.resetPasswordRequired;
      this.customizationScript = builder.customizationScript;
      this.computerName = builder.computerName;
      this.href = builder.href;
      this.links = ImmutableSet.copyOf(builder.links);
      this.type = builder.type;
   }

   private GuestCustomizationSection() {
      // for JAXB
   }


   @XmlElement(name = "Enabled")
   private Boolean enabled;
   @XmlElement(name = "ChangeSid")
   private Boolean changeSid;
   @XmlElement(name = "VirtualMachineId")
   private String virtualMachineId;
   @XmlElement(name = "JoinDomainEnabled")
   private Boolean joinDomainEnabled;
   @XmlElement(name = "UseOrgSettings")
   private Boolean useOrgSettings;
   @XmlElement(name = "DomainName")
   private String domainName;
   @XmlElement(name = "DomainUserName")
   private String domainUserName;
   @XmlElement(name = "DomainUserPassword")
   private String domainUserPassword;
   @XmlElement(name = "AdminPasswordEnabled")
   private Boolean adminPasswordEnabled;
   @XmlElement(name = "AdminPasswordAuto")
   private Boolean adminPasswordAuto;
   @XmlElement(name = "AdminPassword")
   private String adminPassword;
   @XmlElement(name = "ResetPasswordRequired")
   private Boolean resetPasswordRequired;
   @XmlElement(name = "CustomizationScript")
   private String customizationScript;
   @XmlElement(name = "ComputerName")
   private String computerName;
   @XmlElement(name = "Link")
   private Set<Link> links = Sets.newLinkedHashSet();
   @XmlAttribute
   @XmlSchemaType(name = "anyURI")
   private URI href;
   @XmlAttribute
   private String type;

   /**
    * Gets the value of the enabled property.
    *
    * @return possible object is
    *         {@link Boolean }
    */
   public Boolean isEnabled() {
      return enabled;
   }

   /**
    * Gets the value of the changeSid property.
    *
    * @return possible object is
    *         {@link Boolean }
    */
   public Boolean isChangeSid() {
      return changeSid;
   }

   /**
    * Gets the value of the virtualMachineId property.
    *
    * @return possible object is
    *         {@link String }
    */
   public String getVirtualMachineId() {
      return virtualMachineId;
   }

   /**
    * Gets the value of the joinDomainEnabled property.
    *
    * @return possible object is
    *         {@link Boolean }
    */
   public Boolean isJoinDomainEnabled() {
      return joinDomainEnabled;
   }

   /**
    * Gets the value of the useOrgSettings property.
    *
    * @return possible object is
    *         {@link Boolean }
    */
   public Boolean isUseOrgSettings() {
      return useOrgSettings;
   }


   /**
    * Gets the value of the domainName property.
    *
    * @return possible object is
    *         {@link String }
    */
   public String getDomainName() {
      return domainName;
   }

   /**
    * Gets the value of the domainUserName property.
    *
    * @return possible object is
    *         {@link String }
    */
   public String getDomainUserName() {
      return domainUserName;
   }

   /**
    * Gets the value of the domainUserPassword property.
    *
    * @return possible object is
    *         {@link String }
    */
   public String getDomainUserPassword() {
      return domainUserPassword;
   }

   /**
    * Gets the value of the adminPasswordEnabled property.
    *
    * @return possible object is
    *         {@link Boolean }
    */
   public Boolean isAdminPasswordEnabled() {
      return adminPasswordEnabled;
   }

   /**
    * Gets the value of the adminPasswordAuto property.
    *
    * @return possible object is
    *         {@link Boolean }
    */
   public Boolean isAdminPasswordAuto() {
      return adminPasswordAuto;
   }

   /**
    * Gets the value of the adminPassword property.
    *
    * @return possible object is
    *         {@link String }
    */
   public String getAdminPassword() {
      return adminPassword;
   }

   /**
    * Gets the value of the resetPasswordRequired property.
    *
    * @return possible object is
    *         {@link Boolean }
    */
   public Boolean isResetPasswordRequired() {
      return resetPasswordRequired;
   }

   /**
    * Gets the value of the customizationScript property.
    *
    * @return possible object is
    *         {@link String }
    */
   public String getCustomizationScript() {
      return customizationScript;
   }

   /**
    * Gets the value of the computerName property.
    *
    * @return possible object is
    *         {@link String }
    */
   public String getComputerName() {
      return computerName;
   }

   /**
    * Gets the value of the link property.
    * <p/>
    * Objects of the following type(s) are allowed in the list
    * {@link Link }
    */
   public Set<Link> getLinks() {
      return Collections.unmodifiableSet(this.links);
   }

   /**
    * Gets the value of the href property.
    */
   public URI getHref() {
      return href;
   }

   /**
    * Gets the value of the type property.
    *
    * @return possible object is
    *         {@link String }
    */
   public String getType() {
      return type;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      GuestCustomizationSection that = GuestCustomizationSection.class.cast(o);
      return super.equals(that) &&
            equal(enabled, that.enabled) &&
            equal(changeSid, that.changeSid) &&
            equal(virtualMachineId, that.virtualMachineId) &&
            equal(joinDomainEnabled, that.joinDomainEnabled) &&
            equal(useOrgSettings, that.useOrgSettings) &&
            equal(domainName, that.domainName) &&
            equal(domainUserName, that.domainUserName) &&
            equal(domainUserPassword, that.domainUserPassword) &&
            equal(adminPasswordEnabled, that.adminPasswordEnabled) &&
            equal(adminPasswordAuto, that.adminPasswordAuto) &&
            equal(adminPassword, that.adminPassword) &&
            equal(resetPasswordRequired, that.resetPasswordRequired) &&
            equal(customizationScript, that.customizationScript) &&
            equal(computerName, that.computerName) &&
            equal(links, that.links) &&
            equal(href, that.href) &&
            equal(type, that.type);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(),
            enabled,
            changeSid,
            virtualMachineId,
            joinDomainEnabled,
            useOrgSettings,
            domainName,
            domainUserName,
            domainUserPassword,
            adminPasswordEnabled,
            adminPasswordAuto,
            adminPassword,
            resetPasswordRequired,
            customizationScript,
            computerName,
            links,
            href,
            type);
   }

   @Override
   public Objects.ToStringHelper string() {
      return super.string()
            .add("enabled", enabled)
            .add("changeSid", changeSid)
            .add("virtualMachineId", virtualMachineId)
            .add("joinDomainEnabled", joinDomainEnabled)
            .add("useOrgSettings", useOrgSettings)
            .add("domainName", domainName)
            .add("domainUserName", domainUserName)
            .add("domainUserPassword", domainUserPassword)
            .add("adminPasswordEnabled", adminPasswordEnabled)
            .add("adminPasswordAuto", adminPasswordAuto)
            .add("adminPassword", adminPassword)
            .add("resetPasswordRequired", resetPasswordRequired)
            .add("customizationScript", customizationScript)
            .add("computerName", computerName)
            .add("links", links)
            .add("href", href)
            .add("type", type);
   }

}
