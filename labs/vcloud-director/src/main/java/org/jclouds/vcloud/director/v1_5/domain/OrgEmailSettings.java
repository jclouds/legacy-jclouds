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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableList;


/**
 * 
 *                 Defines the email settings for an organization.
 *             
 * 
 * <p>Java class for OrgEmailSettings complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OrgEmailSettings">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}ResourceType">
 *       &lt;sequence>
 *         &lt;element name="IsDefaultSmtpServer" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="IsDefaultOrgEmail" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="FromEmailAddress" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="DefaultSubjectPrefix" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="IsAlertEmailToAllAdmins" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="AlertEmailTo" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="SmtpServerSettings" type="{http://www.vmware.com/vcloud/v1.5}SmtpServerSettingsType" minOccurs="0"/>
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
@XmlRootElement(name = "OrgEmailSettings")
@XmlType(propOrder = {
    "isDefaultSmtpServer",
    "isDefaultOrgEmail",
    "fromEmailAddress",
    "defaultSubjectPrefix",
    "isAlertEmailToAllAdmins",
    "alertEmailsTo",
    "smtpServerSettings"
})
public class OrgEmailSettings extends ResourceType<OrgEmailSettings> {
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromOrgEmailSettings(this);
   }

   public static class Builder extends ResourceType.Builder<OrgEmailSettings> {
      
      private boolean isDefaultSmtpServer;
      private boolean isDefaultOrgEmail;
      private String fromEmailAddress;
      private String defaultSubjectPrefix;
      private boolean isAlertEmailToAllAdmins;
      private List<String> alertEmailTo;
      private SmtpServerSettings smtpServerSettings;

      /**
       * @see OrgEmailSettings#getIsDefaultSmtpServer()
       */
      public Builder isDefaultSmtpServer(boolean isDefaultSmtpServer) {
         this.isDefaultSmtpServer = isDefaultSmtpServer;
         return this;
      }

      /**
       * @see OrgEmailSettings#getIsDefaultOrgEmail()
       */
      public Builder isDefaultOrgEmail(boolean isDefaultOrgEmail) {
         this.isDefaultOrgEmail = isDefaultOrgEmail;
         return this;
      }

      /**
       * @see OrgEmailSettings#getFromEmailAddress()
       */
      public Builder fromEmailAddress(String fromEmailAddress) {
         this.fromEmailAddress = fromEmailAddress;
         return this;
      }

      /**
       * @see OrgEmailSettings#getDefaultSubjectPrefix()
       */
      public Builder defaultSubjectPrefix(String defaultSubjectPrefix) {
         this.defaultSubjectPrefix = defaultSubjectPrefix;
         return this;
      }

      /**
       * @see OrgEmailSettings#getIsAlertEmailToAllAdmins()
       */
      public Builder isAlertEmailToAllAdmins(boolean isAlertEmailToAllAdmins) {
         this.isAlertEmailToAllAdmins = isAlertEmailToAllAdmins;
         return this;
      }

      /**
       * @see OrgEmailSettings#getAlertEmailTo()
       */
      public Builder alertEmailsTo(List<String> alertEmailsTo) {
         this.alertEmailTo = alertEmailsTo == null ? null : ImmutableList.copyOf(alertEmailsTo);
         return this;
      }
      
      /**
       * @see OrgEmailSettings#getAlertEmailTo()
       */
      public Builder alertEmailTo(String alertEmailTo) {
         this.alertEmailTo.add(checkNotNull(alertEmailTo, "alertEmailTo"));
         return this;
      }

      /**
       * @see OrgEmailSettings#getSmtpServerSettings()
       */
      public Builder smtpServerSettings(SmtpServerSettings smtpServerSettings) {
         this.smtpServerSettings = smtpServerSettings;
         return this;
      }

      public OrgEmailSettings build() {
         return new OrgEmailSettings(href, type, links, isDefaultSmtpServer, 
               isDefaultOrgEmail, fromEmailAddress, defaultSubjectPrefix, 
               isAlertEmailToAllAdmins, alertEmailTo, smtpServerSettings);
      }

      
      /**
       * @see ResourceType#getHref()
       */
      @Override
      public Builder href(URI href) {
         super.href(href);
         return this;
      }

      /**
       * @see ResourceType#getType()
       */
      @Override
      public Builder type(String type) {
         super.type(type);
         return this;
      }

      /**
       * @see ResourceType#getLinks()
       */
      @Override
      public Builder links(Set<Link> links) {
         super.links(links);
         return this;
      }

      /**
       * @see ResourceType#getLinks()
       */
      @Override
      public Builder link(Link link) {
         super.link(link);
         return this;
      }


      @Override
      public Builder fromResourceType(ResourceType<OrgEmailSettings> in) {
          return Builder.class.cast(super.fromResourceType(in));
      }
      public Builder fromOrgEmailSettings(OrgEmailSettings in) {
         return fromResourceType(in)
            .isDefaultSmtpServer(in.isDefaultSmtpServer())
            .isDefaultOrgEmail(in.isDefaultOrgEmail())
            .fromEmailAddress(in.getFromEmailAddress())
            .defaultSubjectPrefix(in.getDefaultSubjectPrefix())
            .isAlertEmailToAllAdmins(in.isAlertEmailToAllAdmins())
            .alertEmailsTo(in.getAlertEmailsTo())
            .smtpServerSettings(in.getSmtpServerSettings());
      }
   }

   private OrgEmailSettings() {
      // For JAXB
   }

   private OrgEmailSettings(URI href, String type, Set<Link> links, 
         boolean isDefaultSmtpServer, boolean isDefaultOrgEmail, 
         String fromEmailAddress, String defaultSubjectPrefix, boolean isAlertEmailToAllAdmins, 
         List<String> alertEmailTo, SmtpServerSettings smtpServerSettings) {
      super(href, type, links);
      this.isDefaultSmtpServer = isDefaultSmtpServer;
      this.isDefaultOrgEmail = isDefaultOrgEmail;
      this.fromEmailAddress = fromEmailAddress;
      this.defaultSubjectPrefix = defaultSubjectPrefix;
      this.isAlertEmailToAllAdmins = isAlertEmailToAllAdmins;
      this.alertEmailsTo = alertEmailTo;
      this.smtpServerSettings = smtpServerSettings;
   }

    @XmlElement(name = "IsDefaultSmtpServer")
    protected boolean isDefaultSmtpServer;
    @XmlElement(name = "IsDefaultOrgEmail")
    protected boolean isDefaultOrgEmail;
    @XmlElement(name = "FromEmailAddress", required = true)
    protected String fromEmailAddress;
    @XmlElement(name = "DefaultSubjectPrefix", required = true)
    protected String defaultSubjectPrefix;
    @XmlElement(name = "IsAlertEmailToAllAdmins")
    protected boolean isAlertEmailToAllAdmins;
    @XmlElement(name = "AlertEmailTo")
    protected List<String> alertEmailsTo;
    @XmlElement(name = "SmtpServerSettings")
    protected SmtpServerSettings smtpServerSettings;

    /**
     * Gets the value of the isDefaultSmtpServer property.
     * 
     */
    public boolean isDefaultSmtpServer() {
        return isDefaultSmtpServer;
    }

    /**
     * Gets the value of the isDefaultOrgEmail property.
     * 
     */
    public boolean isDefaultOrgEmail() {
        return isDefaultOrgEmail;
    }

    /**
     * Gets the value of the fromEmailAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFromEmailAddress() {
        return fromEmailAddress;
    }

    /**
     * Gets the value of the defaultSubjectPrefix property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefaultSubjectPrefix() {
        return defaultSubjectPrefix;
    }

    /**
     * Gets the value of the isAlertEmailToAllAdmins property.
     * 
     */
    public boolean isAlertEmailToAllAdmins() {
        return isAlertEmailToAllAdmins;
    }

    /**
     * Gets the value of the alertEmailTo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the alertEmailTo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAlertEmailTo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getAlertEmailsTo() {
        return this.alertEmailsTo;
    }

    /**
     * Gets the value of the smtpServerSettings property.
     * 
     * @return
     *     possible object is
     *     {@link SmtpServerSettings }
     *     
     */
    public SmtpServerSettings getSmtpServerSettings() {
        return smtpServerSettings;
    }

   @Override
   public boolean equals(Object o) {
      if (this == o)
          return true;
      if (o == null || getClass() != o.getClass())
         return false;
      OrgEmailSettings that = OrgEmailSettings.class.cast(o);
      return super.equals(that) &&
           equal(isDefaultSmtpServer, that.isDefaultSmtpServer) && 
           equal(isDefaultOrgEmail, that.isDefaultOrgEmail) && 
           equal(fromEmailAddress, that.fromEmailAddress) && 
           equal(defaultSubjectPrefix, that.defaultSubjectPrefix) && 
           equal(isAlertEmailToAllAdmins, that.isAlertEmailToAllAdmins) && 
           equal(alertEmailsTo, that.alertEmailsTo) && 
           equal(smtpServerSettings, that.smtpServerSettings);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), 
           isDefaultSmtpServer, 
           isDefaultOrgEmail, 
           fromEmailAddress, 
           defaultSubjectPrefix, 
           isAlertEmailToAllAdmins, 
           alertEmailsTo, 
           smtpServerSettings);
   }

   @Override
   public ToStringHelper string() {
      return super.string()
            .add("isDefaultSmtpServer", isDefaultSmtpServer)
            .add("isDefaultOrgEmail", isDefaultOrgEmail)
            .add("fromEmailAddress", fromEmailAddress)
            .add("defaultSubjectPrefix", defaultSubjectPrefix)
            .add("isAlertEmailToAllAdmins", isAlertEmailToAllAdmins)
            .add("alertEmailsTo", alertEmailsTo)
            .add("smtpServerSettings", smtpServerSettings);
   }

}
