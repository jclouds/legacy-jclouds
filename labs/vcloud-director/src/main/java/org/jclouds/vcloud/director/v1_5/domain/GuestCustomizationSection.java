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

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.vcloud.director.v1_5.domain.ovf.SectionType;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;


/**
 * Represents a guest customization settings.
 * <p/>
 * <p/>
 * <p>Java class for GuestCustomizationSection complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
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
public class GuestCustomizationSection extends SectionType<GuestCustomizationSection> {
   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromGuestCustomizationSection(this);
   }

   public static class Builder extends SectionType.Builder<GuestCustomizationSection> {
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
      public Builder enabled(Boolean enabled) {
         this.enabled = enabled;
         return this;
      }

      /**
       * @see org.jclouds.vcloud.director.v1_5.domain.GuestCustomizationSection#isChangeSid()
       */
      public Builder changeSid(Boolean changeSid) {
         this.changeSid = changeSid;
         return this;
      }

      /**
       * @see org.jclouds.vcloud.director.v1_5.domain.GuestCustomizationSection#getVirtualMachineId()
       */
      public Builder virtualMachineId(String virtualMachineId) {
         this.virtualMachineId = virtualMachineId;
         return this;
      }

      /**
       * @see org.jclouds.vcloud.director.v1_5.domain.GuestCustomizationSection#isJoinDomainEnabled()
       */
      public Builder joinDomainEnabled(Boolean joinDomainEnabled) {
         this.joinDomainEnabled = joinDomainEnabled;
         return this;
      }

      /**
       * @see org.jclouds.vcloud.director.v1_5.domain.GuestCustomizationSection#isUseOrgSettings()
       */
      public Builder useOrgSettings(Boolean useOrgSettings) {
         this.useOrgSettings = useOrgSettings;
         return this;
      }

      /**
       * @see org.jclouds.vcloud.director.v1_5.domain.GuestCustomizationSection#getDomainName()
       */
      public Builder domainName(String domainName) {
         this.domainName = domainName;
         return this;
      }

      /**
       * @see org.jclouds.vcloud.director.v1_5.domain.GuestCustomizationSection#getDomainUserName()
       */
      public Builder domainUserName(String domainUserName) {
         this.domainUserName = domainUserName;
         return this;
      }

      /**
       * @see org.jclouds.vcloud.director.v1_5.domain.GuestCustomizationSection#getDomainUserPassword()
       */
      public Builder domainUserPassword(String domainUserPassword) {
         this.domainUserPassword = domainUserPassword;
         return this;
      }

      /**
       * @see org.jclouds.vcloud.director.v1_5.domain.GuestCustomizationSection#isAdminPasswordEnabled()
       */
      public Builder adminPasswordEnabled(Boolean adminPasswordEnabled) {
         this.adminPasswordEnabled = adminPasswordEnabled;
         return this;
      }

      /**
       * @see org.jclouds.vcloud.director.v1_5.domain.GuestCustomizationSection#isAdminPasswordAuto()
       */
      public Builder adminPasswordAuto(Boolean adminPasswordAuto) {
         this.adminPasswordAuto = adminPasswordAuto;
         return this;
      }

      /**
       * @see org.jclouds.vcloud.director.v1_5.domain.GuestCustomizationSection#getAdminPassword()
       */
      public Builder adminPassword(String adminPassword) {
         this.adminPassword = adminPassword;
         return this;
      }

      /**
       * @see org.jclouds.vcloud.director.v1_5.domain.GuestCustomizationSection#isResetPasswordRequired()
       */
      public Builder resetPasswordRequired(Boolean resetPasswordRequired) {
         this.resetPasswordRequired = resetPasswordRequired;
         return this;
      }

      /**
       * @see org.jclouds.vcloud.director.v1_5.domain.GuestCustomizationSection#getCustomizationScript()
       */
      public Builder customizationScript(String customizationScript) {
         this.customizationScript = customizationScript;
         return this;
      }

      /**
       * @see org.jclouds.vcloud.director.v1_5.domain.GuestCustomizationSection#getComputerName()
       */
      public Builder computerName(String computerName) {
         this.computerName = computerName;
         return this;
      }

      /**
       * @see org.jclouds.vcloud.director.v1_5.domain.GuestCustomizationSection#getLinks()
       */
      public Builder links(Set<Link> links) {
         this.links = checkNotNull(links, "links");
         return this;
      }

      /**
       * @see org.jclouds.vcloud.director.v1_5.domain.GuestCustomizationSection#getHref()
       */
      public Builder href(URI href) {
         this.href = href;
         return this;
      }

      /**
       * @see org.jclouds.vcloud.director.v1_5.domain.GuestCustomizationSection#getType()
       */
      public Builder type(String type) {
         this.type = type;
         return this;
      }


      public GuestCustomizationSection build() {
         return new GuestCustomizationSection(info, required, enabled, changeSid, virtualMachineId,
               joinDomainEnabled, useOrgSettings, domainName, domainUserName,
               domainUserPassword, adminPasswordEnabled, adminPasswordAuto,
               adminPassword, resetPasswordRequired, customizationScript,
               computerName, links, href, type);
      }

      public Builder fromGuestCustomizationSection(GuestCustomizationSection in) {
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

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromSectionType(SectionType<GuestCustomizationSection> in) {
         return Builder.class.cast(super.fromSectionType(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder info(String info) {
         return Builder.class.cast(super.info(info));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder required(Boolean required) {
         return Builder.class.cast(super.required(required));
      }
   }

   private GuestCustomizationSection(@Nullable String info, @Nullable Boolean required, Boolean enabled, Boolean changeSid, String virtualMachineId,
                                     Boolean joinDomainEnabled, Boolean useOrgSettings, String domainName, String domainUserName,
                                     String domainUserPassword, Boolean adminPasswordEnabled, Boolean adminPasswordAuto,
                                     String adminPassword, Boolean resetPasswordRequired, String customizationScript,
                                     String computerName, Set<Link> links,  URI href, String type) {
      super(info, required);
      this.enabled = enabled;
      this.changeSid = changeSid;
      this.virtualMachineId = virtualMachineId;
      this.joinDomainEnabled = joinDomainEnabled;
      this.useOrgSettings = useOrgSettings;
      this.domainName = domainName;
      this.domainUserName = domainUserName;
      this.domainUserPassword = domainUserPassword;
      this.adminPasswordEnabled = adminPasswordEnabled;
      this.adminPasswordAuto = adminPasswordAuto;
      this.adminPassword = adminPassword;
      this.resetPasswordRequired = resetPasswordRequired;
      this.customizationScript = customizationScript;
      this.computerName = computerName;
      this.links = ImmutableSet.copyOf(links);
      this.href = href;
      this.type = type;
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
