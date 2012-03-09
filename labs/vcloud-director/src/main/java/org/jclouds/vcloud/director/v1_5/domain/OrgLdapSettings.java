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
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;


/**
 * 
 *                 Base settings for LDAP connection
 *             
 * 
 * <p>Java class for OrgLdapSettings complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OrgLdapSettings">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}ResourceType">
 *       &lt;sequence>
 *         &lt;element name="OrgLdapMode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CustomUsersOu" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CustomOrgLdapSettings" type="{http://www.vmware.com/vcloud/v1.5}CustomOrgLdapSettings" minOccurs="0"/>
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
@XmlType(name = "OrgLdapSettings", propOrder = {
    "orgLdapMode",
    "customUsersOu",
    "customOrgLdapSettings"
})
public class OrgLdapSettings extends ResourceType<OrgLdapSettings> {
   public static final class LdapMode {
      public static final String NONE = "none";
      public static final String SYSTEM = "system";
      public static final String CUSTOM = "custom";

      /**
       * All acceptable {@link OrgLdapSettings#getOrgLdapMode()} values.
       * <p/>
       * This list must be updated whenever a new mode is added.
       */
      public static final List<String> ALL = Arrays.asList(
            NONE, SYSTEM, CUSTOM
      );
   }
   
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromOrgLdapSettings(this);
   }

   public static class Builder extends ResourceType.Builder<OrgLdapSettings> {
      
      private String orgLdapMode;
      private String customUsersOu;
      private CustomOrgLdapSettings customOrgLdapSettings;

      /**
       * @see OrgLdapSettings#getOrgLdapMode()
       */
      public Builder orgLdapMode(String orgLdapMode) {
         this.orgLdapMode = orgLdapMode;
         return this;
      }

      /**
       * @see OrgLdapSettings#getCustomUsersOu()
       */
      public Builder customUsersOu(String customUsersOu) {
         this.customUsersOu = customUsersOu;
         return this;
      }

      /**
       * @see OrgLdapSettings#getCustomOrgLdapSettings()
       */
      public Builder customOrgLdapSettings(CustomOrgLdapSettings customOrgLdapSettings) {
         this.customOrgLdapSettings = customOrgLdapSettings;
         return this;
      }

      public OrgLdapSettings build() {
         OrgLdapSettings orgLdapSettings = new OrgLdapSettings(href, type, links, 
               orgLdapMode, customUsersOu, customOrgLdapSettings);
         return orgLdapSettings;
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
      public Builder fromResourceType(ResourceType<OrgLdapSettings> in) {
          return Builder.class.cast(super.fromResourceType(in));
      }
      public Builder fromOrgLdapSettings(OrgLdapSettings in) {
         return fromResourceType(in)
            .orgLdapMode(in.getOrgLdapMode())
            .customUsersOu(in.getCustomUsersOu())
            .customOrgLdapSettings(in.getCustomOrgLdapSettings());
      }
   }

   @SuppressWarnings("unused")
   private OrgLdapSettings() {
      // For JAXB
   }

    public OrgLdapSettings(URI href, String type, Set<Link> links, 
          String orgLdapMode, String customUsersOu, CustomOrgLdapSettings customOrgLdapSettings) {
      super(href, type, links);
      this.orgLdapMode = orgLdapMode;
      this.customUsersOu = customUsersOu;
      this.customOrgLdapSettings = customOrgLdapSettings;
   }



   @XmlElement(name = "OrgLdapMode")
    protected String orgLdapMode;
    @XmlElement(name = "CustomUsersOu")
    protected String customUsersOu;
    @XmlElement(name = "CustomOrgLdapSettings")
    protected CustomOrgLdapSettings customOrgLdapSettings;

    /**
     * Gets the value of the orgLdapMode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrgLdapMode() {
        return orgLdapMode;
    }

    /**
     * Gets the value of the customUsersOu property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomUsersOu() {
        return customUsersOu;
    }

    /**
     * Gets the value of the customOrgLdapSettings property.
     * 
     * @return
     *     possible object is
     *     {@link CustomOrgLdapSettings }
     *     
     */
    public CustomOrgLdapSettings getCustomOrgLdapSettings() {
        return customOrgLdapSettings;
    }

   @Override
   public boolean equals(Object o) {
      if (this == o)
          return true;
      if (o == null || getClass() != o.getClass())
         return false;
      OrgLdapSettings that = OrgLdapSettings.class.cast(o);
      return super.equals(that) && 
           equal(orgLdapMode, that.orgLdapMode) && 
           equal(customUsersOu, that.customUsersOu) && 
           equal(customOrgLdapSettings, that.customOrgLdapSettings);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(),
           orgLdapMode, 
           customUsersOu, 
           customOrgLdapSettings);
   }

   @Override
   public ToStringHelper string() {
      return super.string()
            .add("orgLdapMode", orgLdapMode)
            .add("customUsersOu", customUsersOu)
            .add("customOrgLdapSettings", customOrgLdapSettings);
   }

}
