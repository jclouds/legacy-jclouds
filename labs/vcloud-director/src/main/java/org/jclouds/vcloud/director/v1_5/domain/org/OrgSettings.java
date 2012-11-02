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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.jclouds.vcloud.director.v1_5.domain.Resource;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
/**
 * Describes various settings for some organization.
 * This type establishes quotas and policies for the organization.
 * It also contains elements that specify the details of
 * how the organization connects to LDAP and email services.
 *
 * <pre>
 * &lt;complexType name="OrgSettings">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}ResourceType">
 *       &lt;sequence>
 *         &lt;element name="OrgGeneralSettings" type="{http://www.vmware.com/vcloud/v1.5}OrgGeneralSettingsType" minOccurs="0"/>
 *         &lt;element name="VAppLeaseSettings" type="{http://www.vmware.com/vcloud/v1.5}OrgLeaseSettingsType" minOccurs="0"/>
 *         &lt;element name="VAppTemplateLeaseSettings" type="{http://www.vmware.com/vcloud/v1.5}OrgVAppTemplateLeaseSettingsType" minOccurs="0"/>
 *         &lt;element name="OrgLdapSettings" type="{http://www.vmware.com/vcloud/v1.5}OrgLdapSettingsType" minOccurs="0"/>
 *         &lt;element name="OrgEmailSettings" type="{http://www.vmware.com/vcloud/v1.5}OrgEmailSettingsType" minOccurs="0"/>
 *         &lt;element name="OrgPasswordPolicySettings" type="{http://www.vmware.com/vcloud/v1.5}OrgPasswordPolicySettingsType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement(name = "OrgSettings")
@XmlType(propOrder = {
    "generalSettings",
    "vAppLeaseSettings",
    "vAppTemplateLeaseSettings",
    "ldapSettings",
    "emailSettings",
    "passwordPolicy"
})
public class OrgSettings extends Resource {
   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return builder().fromOrgSettings(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public abstract static class Builder<B extends Builder<B>> extends Resource.Builder<B> {
      
      private OrgGeneralSettings generalSettings;
      private OrgLeaseSettings vAppLeaseSettings;
      private OrgVAppTemplateLeaseSettings vAppTemplateLeaseSettings;
      private OrgLdapSettings ldapSettings;
      private OrgEmailSettings emailSettings;
      private OrgPasswordPolicySettings passwordPolicy;

      /**
       * @see OrgSettings#getGeneralSettings()
       */
      public B generalSettings(OrgGeneralSettings generalSettings) {
         this.generalSettings = generalSettings;
         return self();
      }

      /**
       * @see OrgSettings#getVAppLeaseSettings()
       */
      public B vAppLeaseSettings(OrgLeaseSettings vAppLeaseSettings) {
         this.vAppLeaseSettings = vAppLeaseSettings;
         return self();
      }

      /**
       * @see OrgSettings#getVAppTemplateLeaseSettings()
       */
      public B vAppTemplateLeaseSettings(OrgVAppTemplateLeaseSettings vAppTemplateLeaseSettings) {
         this.vAppTemplateLeaseSettings = vAppTemplateLeaseSettings;
         return self();
      }

      /**
       * @see OrgSettings#getLdapSettings()
       */
      public B ldapSettings(OrgLdapSettings ldapSettings) {
         this.ldapSettings = ldapSettings;
         return self();
      }

      /**
       * @see OrgSettings#getEmailSettings()
       */
      public B emailSettings(OrgEmailSettings emailSettings) {
         this.emailSettings = emailSettings;
         return self();
      }

      /**
       * @see OrgSettings#getPasswordPolicy()
       */
      public B passwordPolicy(OrgPasswordPolicySettings passwordPolicy) {
         this.passwordPolicy = passwordPolicy;
         return self();
      }

      @Override
      public OrgSettings build() {
         return new OrgSettings(this);
      }

      public B fromOrgSettings(OrgSettings in) {
         return fromResource(in)
            .generalSettings(in.getGeneralSettings())
            .vAppLeaseSettings(in.getVAppLeaseSettings())
            .vAppTemplateLeaseSettings(in.getVAppTemplateLeaseSettings())
            .ldapSettings(in.getLdapSettings())
            .emailSettings(in.getEmailSettings())
            .passwordPolicy(in.getPasswordPolicy());
      }
   }

   @SuppressWarnings("unused")
   private OrgSettings() {
      // For JAXB
   }

   public OrgSettings(Builder<?> builder) {
      super(builder);
      this.generalSettings = builder.generalSettings;
      this.vAppLeaseSettings = builder.vAppLeaseSettings;
      this.vAppTemplateLeaseSettings = builder.vAppTemplateLeaseSettings;
      this.ldapSettings = builder.ldapSettings;
      this.emailSettings = builder.emailSettings;
      this.passwordPolicy = builder.passwordPolicy;
   }

   @XmlElement(name = "OrgGeneralSettings")
    private OrgGeneralSettings generalSettings;
    @XmlElement(name = "VAppLeaseSettings")
    private OrgLeaseSettings vAppLeaseSettings;
    @XmlElement(name = "VAppTemplateLeaseSettings")
    private OrgVAppTemplateLeaseSettings vAppTemplateLeaseSettings;
    @XmlElement(name = "OrgLdapSettings")
    private OrgLdapSettings ldapSettings;
    @XmlElement(name = "OrgEmailSettings")
    private OrgEmailSettings emailSettings;
    @XmlElement(name = "OrgPasswordPolicySettings")
    private OrgPasswordPolicySettings passwordPolicy;

    /**
     * Gets the value of the orgGeneralSettings property.
     * 
     * @return
     *     possible object is
     *     {@link OrgGeneralSettings }
     *     
     */
    public OrgGeneralSettings getGeneralSettings() {
        return generalSettings;
    }

    /**
     * Gets the value of the vAppLeaseSettings property.
     * 
     * @return
     *     possible object is
     *     {@link OrgLeaseSettings }
     *     
     */
    public OrgLeaseSettings getVAppLeaseSettings() {
        return vAppLeaseSettings;
    }

    /**
     * Gets the value of the vAppTemplateLeaseSettings property.
     * 
     * @return
     *     possible object is
     *     {@link OrgVAppTemplateLeaseSettings }
     *     
     */
    public OrgVAppTemplateLeaseSettings getVAppTemplateLeaseSettings() {
        return vAppTemplateLeaseSettings;
    }

    /**
     * Gets the value of the orgLdapSettings property.
     * 
     * @return
     *     possible object is
     *     {@link OrgLdapSettings }
     *     
     */
    public OrgLdapSettings getLdapSettings() {
        return ldapSettings;
    }

    /**
     * Gets the value of the orgEmailSettings property.
     * 
     * @return
     *     possible object is
     *     {@link OrgEmailSettings }
     *     
     */
    public OrgEmailSettings getEmailSettings() {
        return emailSettings;
    }

    /**
     * Gets the value of the orgPasswordPolicySettings property.
     * 
     * @return
     *     possible object is
     *     {@link OrgPasswordPolicySettings }
     *     
     */
    public OrgPasswordPolicySettings getPasswordPolicy() {
        return passwordPolicy;
    }

   @Override
   public boolean equals(Object o) {
      if (this == o)
          return true;
      if (o == null || getClass() != o.getClass())
         return false;
      OrgSettings that = OrgSettings.class.cast(o);
      return super.equals(that) && 
           equal(generalSettings, that.generalSettings) && 
           equal(vAppLeaseSettings, that.vAppLeaseSettings) && 
           equal(vAppTemplateLeaseSettings, that.vAppTemplateLeaseSettings) && 
           equal(ldapSettings, that.ldapSettings) && 
           equal(emailSettings, that.emailSettings) && 
           equal(passwordPolicy, that.passwordPolicy);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), 
           generalSettings, 
           vAppLeaseSettings, 
           vAppTemplateLeaseSettings, 
           ldapSettings, 
           emailSettings, 
           passwordPolicy);
   }

   @Override
   public ToStringHelper string() {
      return super.string()
            .add("generalSettings", generalSettings)
            .add("vAppLeaseSettings", vAppLeaseSettings)
            .add("vAppTemplateLeaseSettings", vAppTemplateLeaseSettings)
            .add("ldapSettings", ldapSettings)
            .add("emailSettings", emailSettings)
            .add("passwordPolicy", passwordPolicy);
   }

}
