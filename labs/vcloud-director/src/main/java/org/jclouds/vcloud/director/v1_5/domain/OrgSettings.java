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
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;


/**
 * 
 *                 Describes various settings for some organization.
 *                 This type establishes quotas and policies for the organization.
 *                 It also contains elements that specify the details of
 *                 how the organization connects to LDAP and email services.
 *             
 * 
 * <p>Java class for OrgSettings complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
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
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "OrgSettings")
@XmlType(propOrder = {
    "orgGeneralSettings",
    "vAppLeaseSettings",
    "vAppTemplateLeaseSettings",
    "orgLdapSettings",
    "orgEmailSettings",
    "orgPasswordPolicySettings"
})
public class OrgSettings extends ResourceType<OrgSettings> {
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromOrgSettings(this);
   }

   public static class Builder extends ResourceType.Builder<OrgSettings> {
      
      private OrgGeneralSettings orgGeneralSettings;
      private OrgLeaseSettings vAppLeaseSettings;
      private OrgVAppTemplateLeaseSettings vAppTemplateLeaseSettings;
      private OrgLdapSettings orgLdapSettings;
      private OrgEmailSettings orgEmailSettings;
      private OrgPasswordPolicySettings orgPasswordPolicySettings;

      /**
       * @see OrgSettings#getOrgGeneralSettings()
       */
      public Builder orgGeneralSettings(OrgGeneralSettings orgGeneralSettings) {
         this.orgGeneralSettings = orgGeneralSettings;
         return this;
      }

      /**
       * @see OrgSettings#getVAppLeaseSettings()
       */
      public Builder vAppLeaseSettings(OrgLeaseSettings vAppLeaseSettings) {
         this.vAppLeaseSettings = vAppLeaseSettings;
         return this;
      }

      /**
       * @see OrgSettings#getVAppTemplateLeaseSettings()
       */
      public Builder vAppTemplateLeaseSettings(OrgVAppTemplateLeaseSettings vAppTemplateLeaseSettings) {
         this.vAppTemplateLeaseSettings = vAppTemplateLeaseSettings;
         return this;
      }

      /**
       * @see OrgSettings#getOrgLdapSettings()
       */
      public Builder orgLdapSettings(OrgLdapSettings orgLdapSettings) {
         this.orgLdapSettings = orgLdapSettings;
         return this;
      }

      /**
       * @see OrgSettings#getOrgEmailSettings()
       */
      public Builder orgEmailSettings(OrgEmailSettings orgEmailSettings) {
         this.orgEmailSettings = orgEmailSettings;
         return this;
      }

      /**
       * @see OrgSettings#getOrgPasswordPolicySettings()
       */
      public Builder orgPasswordPolicySettings(OrgPasswordPolicySettings orgPasswordPolicySettings) {
         this.orgPasswordPolicySettings = orgPasswordPolicySettings;
         return this;
      }

      public OrgSettings build() {
         return new OrgSettings(href, type, links, orgGeneralSettings, vAppLeaseSettings, 
               vAppTemplateLeaseSettings, orgLdapSettings, orgEmailSettings, orgPasswordPolicySettings);
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
      public Builder fromResourceType(ResourceType<OrgSettings> in) {
          return Builder.class.cast(super.fromResourceType(in));
      }
      public Builder fromOrgSettings(OrgSettings in) {
         return fromResourceType(in)
            .orgGeneralSettings(in.getOrgGeneralSettings())
            .vAppLeaseSettings(in.getVAppLeaseSettings())
            .vAppTemplateLeaseSettings(in.getVAppTemplateLeaseSettings())
            .orgLdapSettings(in.getOrgLdapSettings())
            .orgEmailSettings(in.getOrgEmailSettings())
            .orgPasswordPolicySettings(in.getOrgPasswordPolicySettings());
      }
   }

   @SuppressWarnings("unused")
   private OrgSettings() {
      // For JAXB
   }

    public OrgSettings(URI href, String type, Set<Link> links,
         OrgGeneralSettings orgGeneralSettings,
         OrgLeaseSettings vAppLeaseSettings,
         OrgVAppTemplateLeaseSettings vAppTemplateLeaseSettings,
         OrgLdapSettings orgLdapSettings, OrgEmailSettings orgEmailSettings,
         OrgPasswordPolicySettings orgPasswordPolicySettings) {
      super(href, type, links);
      this.orgGeneralSettings = orgGeneralSettings;
      this.vAppLeaseSettings = vAppLeaseSettings;
      this.vAppTemplateLeaseSettings = vAppTemplateLeaseSettings;
      this.orgLdapSettings = orgLdapSettings;
      this.orgPasswordPolicySettings = orgPasswordPolicySettings;
   }

   @XmlElement(name = "OrgGeneralSettings")
    protected OrgGeneralSettings orgGeneralSettings;
    @XmlElement(name = "VAppLeaseSettings")
    protected OrgLeaseSettings vAppLeaseSettings;
    @XmlElement(name = "VAppTemplateLeaseSettings")
    protected OrgVAppTemplateLeaseSettings vAppTemplateLeaseSettings;
    @XmlElement(name = "OrgLdapSettings")
    protected OrgLdapSettings orgLdapSettings;
    @XmlElement(name = "OrgEmailSettings")
    protected OrgEmailSettings orgEmailSettings;
    @XmlElement(name = "OrgPasswordPolicySettings")
    protected OrgPasswordPolicySettings orgPasswordPolicySettings;

    /**
     * Gets the value of the orgGeneralSettings property.
     * 
     * @return
     *     possible object is
     *     {@link OrgGeneralSettings }
     *     
     */
    public OrgGeneralSettings getOrgGeneralSettings() {
        return orgGeneralSettings;
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
    public OrgLdapSettings getOrgLdapSettings() {
        return orgLdapSettings;
    }

    /**
     * Gets the value of the orgEmailSettings property.
     * 
     * @return
     *     possible object is
     *     {@link OrgEmailSettings }
     *     
     */
    public OrgEmailSettings getOrgEmailSettings() {
        return orgEmailSettings;
    }

    /**
     * Gets the value of the orgPasswordPolicySettings property.
     * 
     * @return
     *     possible object is
     *     {@link OrgPasswordPolicySettings }
     *     
     */
    public OrgPasswordPolicySettings getOrgPasswordPolicySettings() {
        return orgPasswordPolicySettings;
    }

   @Override
   public boolean equals(Object o) {
      if (this == o)
          return true;
      if (o == null || getClass() != o.getClass())
         return false;
      OrgSettings that = OrgSettings.class.cast(o);
      return super.equals(that) && 
           equal(orgGeneralSettings, that.orgGeneralSettings) && 
           equal(vAppLeaseSettings, that.vAppLeaseSettings) && 
           equal(vAppTemplateLeaseSettings, that.vAppTemplateLeaseSettings) && 
           equal(orgLdapSettings, that.orgLdapSettings) && 
           equal(orgEmailSettings, that.orgEmailSettings) && 
           equal(orgPasswordPolicySettings, that.orgPasswordPolicySettings);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), 
           orgGeneralSettings, 
           vAppLeaseSettings, 
           vAppTemplateLeaseSettings, 
           orgLdapSettings, 
           orgEmailSettings, 
           orgPasswordPolicySettings);
   }

   @Override
   public ToStringHelper string() {
      return super.string()
            .add("orgGeneralSettings", orgGeneralSettings)
            .add("vAppLeaseSettings", vAppLeaseSettings)
            .add("vAppTemplateLeaseSettings", vAppTemplateLeaseSettings)
            .add("orgLdapSettings", orgLdapSettings)
            .add("orgEmailSettings", orgEmailSettings)
            .add("orgPasswordPolicySettings", orgPasswordPolicySettings);
   }

}
