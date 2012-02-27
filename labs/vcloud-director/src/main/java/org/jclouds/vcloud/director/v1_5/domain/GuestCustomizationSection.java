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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.vcloud.director.v1_5.domain.SectionType;
import org.jclouds.vcloud.director.v1_5.domain.SectionType;

import com.google.common.base.Objects;


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
@XmlAccessorType(XmlAccessType.FIELD)
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
      "link",
      "any"
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
      private List<Link> link;
      private List<Object> any;
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
       * @see org.jclouds.vcloud.director.v1_5.domain.GuestCustomizationSection#getLink()
       */
      public Builder link(List<Link> link) {
         this.link = link;
         return this;
      }

      /**
       * @see org.jclouds.vcloud.director.v1_5.domain.GuestCustomizationSection#getAny()
       */
      public Builder any(List<Object> any) {
         this.any = any;
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
         GuestCustomizationSection guestCustomizationSection = new GuestCustomizationSection(info, resetPasswordRequired, link, any);
         guestCustomizationSection.setEnabled(enabled);
         guestCustomizationSection.setChangeSid(changeSid);
         guestCustomizationSection.setVirtualMachineId(virtualMachineId);
         guestCustomizationSection.setJoinDomainEnabled(joinDomainEnabled);
         guestCustomizationSection.setUseOrgSettings(useOrgSettings);
         guestCustomizationSection.setDomainName(domainName);
         guestCustomizationSection.setDomainUserName(domainUserName);
         guestCustomizationSection.setDomainUserPassword(domainUserPassword);
         guestCustomizationSection.setAdminPasswordEnabled(adminPasswordEnabled);
         guestCustomizationSection.setAdminPasswordAuto(adminPasswordAuto);
         guestCustomizationSection.setAdminPassword(adminPassword);
         guestCustomizationSection.setCustomizationScript(customizationScript);
         guestCustomizationSection.setComputerName(computerName);
         guestCustomizationSection.setHref(href);
         guestCustomizationSection.setType(type);
         return guestCustomizationSection;
      }
      
      public Builder fromGuestCustomizationSection(GuestCustomizationSection in) {
         return fromSection(in)
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
               .link(in.getLink())
               .any(in.getAny())
               .href(in.getHref())
               .type(in.getType());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromSection(SectionType<GuestCustomizationSection> in) {
         return Builder.class.cast(super.fromSection(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder info(String info) {
         return Builder.class.cast(super.info(info));
      }

   }

   private GuestCustomizationSection() {
      // For JAXB and builder use
      super(null);
   }

   private GuestCustomizationSection(@Nullable String info, Boolean resetPasswordRequired, List<Link> link, List<Object> any) {
      super(info);
      this.resetPasswordRequired = resetPasswordRequired;
      this.link = link;
      this.any = any;
   }


   @XmlElement(name = "Enabled")
   protected Boolean enabled;
   @XmlElement(name = "ChangeSid")
   protected Boolean changeSid;
   @XmlElement(name = "VirtualMachineId")
   protected String virtualMachineId;
   @XmlElement(name = "JoinDomainEnabled")
   protected Boolean joinDomainEnabled;
   @XmlElement(name = "UseOrgSettings")
   protected Boolean useOrgSettings;
   @XmlElement(name = "DomainName")
   protected String domainName;
   @XmlElement(name = "DomainUserName")
   protected String domainUserName;
   @XmlElement(name = "DomainUserPassword")
   protected String domainUserPassword;
   @XmlElement(name = "AdminPasswordEnabled")
   protected Boolean adminPasswordEnabled;
   @XmlElement(name = "AdminPasswordAuto")
   protected Boolean adminPasswordAuto;
   @XmlElement(name = "AdminPassword")
   protected String adminPassword;
   @XmlElement(name = "ResetPasswordRequired")
   protected Boolean resetPasswordRequired;
   @XmlElement(name = "CustomizationScript")
   protected String customizationScript;
   @XmlElement(name = "ComputerName")
   protected String computerName;
   @XmlElement(name = "Link")
   protected List<Link> link;
   @XmlAnyElement(lax = true)
   protected List<Object> any;
   @XmlAttribute
   @XmlSchemaType(name = "anyURI")
   protected URI href;
   @XmlAttribute
   protected String type;

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
    * Sets the value of the enabled property.
    *
    * @param value allowed object is
    *              {@link Boolean }
    */
   public void setEnabled(Boolean value) {
      this.enabled = value;
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
    * Sets the value of the changeSid property.
    *
    * @param value allowed object is
    *              {@link Boolean }
    */
   public void setChangeSid(Boolean value) {
      this.changeSid = value;
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
    * Sets the value of the virtualMachineId property.
    *
    * @param value allowed object is
    *              {@link String }
    */
   public void setVirtualMachineId(String value) {
      this.virtualMachineId = value;
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
    * Sets the value of the joinDomainEnabled property.
    *
    * @param value allowed object is
    *              {@link Boolean }
    */
   public void setJoinDomainEnabled(Boolean value) {
      this.joinDomainEnabled = value;
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
    * Sets the value of the useOrgSettings property.
    *
    * @param value allowed object is
    *              {@link Boolean }
    */
   public void setUseOrgSettings(Boolean value) {
      this.useOrgSettings = value;
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
    * Sets the value of the domainName property.
    *
    * @param value allowed object is
    *              {@link String }
    */
   public void setDomainName(String value) {
      this.domainName = value;
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
    * Sets the value of the domainUserName property.
    *
    * @param value allowed object is
    *              {@link String }
    */
   public void setDomainUserName(String value) {
      this.domainUserName = value;
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
    * Sets the value of the domainUserPassword property.
    *
    * @param value allowed object is
    *              {@link String }
    */
   public void setDomainUserPassword(String value) {
      this.domainUserPassword = value;
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
    * Sets the value of the adminPasswordEnabled property.
    *
    * @param value allowed object is
    *              {@link Boolean }
    */
   public void setAdminPasswordEnabled(Boolean value) {
      this.adminPasswordEnabled = value;
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
    * Sets the value of the adminPasswordAuto property.
    *
    * @param value allowed object is
    *              {@link Boolean }
    */
   public void setAdminPasswordAuto(Boolean value) {
      this.adminPasswordAuto = value;
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
    * Sets the value of the adminPassword property.
    *
    * @param value allowed object is
    *              {@link String }
    */
   public void setAdminPassword(String value) {
      this.adminPassword = value;
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
    * Sets the value of the resetPasswordRequired property.
    *
    * @param value allowed object is
    *              {@link Boolean }
    */
   public void setResetPasswordRequired(Boolean value) {
      this.resetPasswordRequired = value;
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
    * Sets the value of the customizationScript property.
    *
    * @param value allowed object is
    *              {@link String }
    */
   public void setCustomizationScript(String value) {
      this.customizationScript = value;
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
    * Sets the value of the computerName property.
    *
    * @param value allowed object is
    *              {@link String }
    */
   public void setComputerName(String value) {
      this.computerName = value;
   }

   /**
    * Gets the value of the link property.
    * <p/>
    * <p/>
    * This accessor method returns a reference to the live list,
    * not a snapshot. Therefore any modification you make to the
    * returned list will be present inside the JAXB object.
    * This is why there is not a <CODE>set</CODE> method for the link property.
    * <p/>
    * <p/>
    * For example, to add a new item, do as follows:
    * <pre>
    *    getLink().add(newItem);
    * </pre>
    * <p/>
    * <p/>
    * <p/>
    * Objects of the following type(s) are allowed in the list
    * {@link Link }
    */
   public List<Link> getLink() {
      if (link == null) {
         link = new ArrayList<Link>();
      }
      return this.link;
   }

   /**
    * Gets the value of the any property.
    * <p/>
    * <p/>
    * This accessor method returns a reference to the live list,
    * not a snapshot. Therefore any modification you make to the
    * returned list will be present inside the JAXB object.
    * This is why there is not a <CODE>set</CODE> method for the any property.
    * <p/>
    * <p/>
    * For example, to add a new item, do as follows:
    * <pre>
    *    getAny().add(newItem);
    * </pre>
    * <p/>
    * <p/>
    * <p/>
    * Objects of the following type(s) are allowed in the list
    * {@link Object }
    * {@link org.w3c.dom.Element }
    */
   public List<Object> getAny() {
      if (any == null) {
         any = new ArrayList<Object>();
      }
      return this.any;
   }

   /**
    * Gets the value of the href property.
    */
   public URI getHref() {
      return href;
   }

   /**
    * Sets the value of the href property.
    */
   public void setHref(URI value) {
      this.href = value;
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

   /**
    * Sets the value of the type property.
    *
    * @param value allowed object is
    *              {@link String }
    */
   public void setType(String value) {
      this.type = value;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      GuestCustomizationSection that = GuestCustomizationSection.class.cast(o);
      return equal(enabled, that.enabled) &&
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
            equal(link, that.link) &&
            equal(any, that.any) &&
            equal(href, that.href) &&
            equal(type, that.type);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(enabled,
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
            link,
            any,
            href,
            type);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("")
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
            .add("link", link)
            .add("any", any)
            .add("href", href)
            .add("type", type).toString();
   }

}
