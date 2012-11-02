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
package org.jclouds.vcloud.director.v1_5.domain.org;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.jclouds.vcloud.director.v1_5.domain.Resource;
import org.jclouds.vcloud.director.v1_5.domain.network.SmtpServerSettings;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableList;

/**
 * Defines the email settings for an organization.
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
 */
@XmlRootElement(name = "OrgEmailSettings")
@XmlType(propOrder = {
    "isDefaultSmtpServer",
    "isDefaultOrgEmail",
    "fromEmailAddress",
    "defaultSubjectPrefix",
    "isAlertEmailToAllAdmins",
    "alertEmailTo",
    "smtpServerSettings"
})
public class OrgEmailSettings extends Resource {
   
   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return builder().fromOrgEmailSettings(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public abstract static class Builder<B extends Builder<B>> extends Resource.Builder<B> {
      
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
      public B isDefaultSmtpServer(boolean isDefaultSmtpServer) {
         this.isDefaultSmtpServer = isDefaultSmtpServer;
         return self();
      }

      /**
       * @see OrgEmailSettings#getIsDefaultOrgEmail()
       */
      public B isDefaultOrgEmail(boolean isDefaultOrgEmail) {
         this.isDefaultOrgEmail = isDefaultOrgEmail;
         return self();
      }

      /**
       * @see OrgEmailSettings#getFromEmailAddress()
       */
      public B fromEmailAddress(String fromEmailAddress) {
         this.fromEmailAddress = fromEmailAddress;
         return self();
      }

      /**
       * @see OrgEmailSettings#getDefaultSubjectPrefix()
       */
      public B defaultSubjectPrefix(String defaultSubjectPrefix) {
         this.defaultSubjectPrefix = defaultSubjectPrefix;
         return self();
      }

      /**
       * @see OrgEmailSettings#getIsAlertEmailToAllAdmins()
       */
      public B isAlertEmailToAllAdmins(boolean isAlertEmailToAllAdmins) {
         this.isAlertEmailToAllAdmins = isAlertEmailToAllAdmins;
         return self();
      }

      /**
       * @see OrgEmailSettings#getAlertEmailTo()
       */
      public B alertEmailTo(List<String> alertEmailTo) {
         this.alertEmailTo = alertEmailTo == null ? null : ImmutableList.copyOf(alertEmailTo);
         return self();
      }
      
      /**
       * @see OrgEmailSettings#getAlertEmailTo()
       */
      public B alertEmailTo(String alertEmailTo) {
         this.alertEmailTo.add(checkNotNull(alertEmailTo, "alertEmailTo"));
         return self();
      }

      /**
       * @see OrgEmailSettings#getSmtpServerSettings()
       */
      public B smtpServerSettings(SmtpServerSettings smtpServerSettings) {
         this.smtpServerSettings = smtpServerSettings;
         return self();
      }

      @Override
      public OrgEmailSettings build() {
         return new OrgEmailSettings(this);
      }
      
      public B fromOrgEmailSettings(OrgEmailSettings in) {
         return fromResource(in)
            .isDefaultSmtpServer(in.isDefaultSmtpServer())
            .isDefaultOrgEmail(in.isDefaultOrgEmail())
            .fromEmailAddress(in.getFromEmailAddress())
            .defaultSubjectPrefix(in.getDefaultSubjectPrefix())
            .isAlertEmailToAllAdmins(in.isAlertEmailToAllAdmins())
            .alertEmailTo(in.getAlertEmailTo())
            .smtpServerSettings(in.getSmtpServerSettings());
      }
   }

   private OrgEmailSettings() {
      // For JAXB
   }

   private OrgEmailSettings(Builder<?> builder) {
      super(builder);
      this.isDefaultSmtpServer = builder.isDefaultSmtpServer;
      this.isDefaultOrgEmail = builder.isDefaultOrgEmail;
      this.fromEmailAddress = builder.fromEmailAddress;
      this.defaultSubjectPrefix = builder.defaultSubjectPrefix;
      this.isAlertEmailToAllAdmins = builder.isAlertEmailToAllAdmins;
      this.alertEmailTo = builder.alertEmailTo;
      this.smtpServerSettings = builder.smtpServerSettings;
   }

    @XmlElement(name = "IsDefaultSmtpServer")
    private boolean isDefaultSmtpServer;
    @XmlElement(name = "IsDefaultOrgEmail")
    private boolean isDefaultOrgEmail;
    @XmlElement(name = "FromEmailAddress", required = true)
    private String fromEmailAddress;
    @XmlElement(name = "DefaultSubjectPrefix", required = true)
    private String defaultSubjectPrefix;
    @XmlElement(name = "IsAlertEmailToAllAdmins")
    private boolean isAlertEmailToAllAdmins;
    @XmlElement(name = "AlertEmailTo")
    private List<String> alertEmailTo;
    @XmlElement(name = "SmtpServerSettings")
    private SmtpServerSettings smtpServerSettings;

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
    public List<String> getAlertEmailTo() {
        return this.alertEmailTo;
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
           equal(alertEmailTo, that.alertEmailTo) && 
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
           alertEmailTo, 
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
            .add("alertEmailTo", alertEmailTo)
            .add("smtpServerSettings", smtpServerSettings);
   }

}
