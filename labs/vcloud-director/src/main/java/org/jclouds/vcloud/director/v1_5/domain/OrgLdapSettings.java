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
package org.jclouds.vcloud.director.v1_5.domain;

import static com.google.common.base.Objects.equal;

import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
/**
 * Base settings for LDAP connection
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
 */
@XmlRootElement(name = "OrgLdapSettings")
@XmlType(propOrder = {
    "ldapMode",
    "customUsersOu",
    "customOrgLdapSettings"
})
public class OrgLdapSettings extends ResourceType {

   public static final class LdapMode {
      public static final String NONE = "NONE";
      public static final String SYSTEM = "SYSTEM";
      public static final String CUSTOM = "CUSTOM";

      /**
       * All acceptable {@link #getLdapMode()} values.
       *
       * This list must be updated whenever a new mode is added.
       */
      public static final List<String> ALL = Arrays.asList(
            NONE, SYSTEM, CUSTOM
      );
   }
   
   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return builder().fromOrgLdapSettings(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public static abstract class Builder<B extends Builder<B>> extends ResourceType.Builder<B> {
      
      private String ldapMode;
      private String customUsersOu;
      private CustomOrgLdapSettings customOrgLdapSettings;

      /**
       * @see OrgLdapSettings#getLdapMode()
       */
      public B ldapMode(String ldapMode) {
         this.ldapMode = ldapMode;
         return self();
      }

      /**
       * @see OrgLdapSettings#getCustomUsersOu()
       */
      public B customUsersOu(String customUsersOu) {
         this.customUsersOu = customUsersOu;
         return self();
      }

      /**
       * @see OrgLdapSettings#getCustomOrgLdapSettings()
       */
      public B customOrgLdapSettings(CustomOrgLdapSettings customOrgLdapSettings) {
         this.customOrgLdapSettings = customOrgLdapSettings;
         return self();
      }

      @Override
      public OrgLdapSettings build() {
         return new OrgLdapSettings(this);
      }
      
      public B fromOrgLdapSettings(OrgLdapSettings in) {
         return fromResourceType(in)
            .ldapMode(in.getLdapMode())
            .customUsersOu(in.getCustomUsersOu())
            .customOrgLdapSettings(in.getCustomOrgLdapSettings());
      }
   }

   protected OrgLdapSettings() {
      // For JAXB
   }

   protected OrgLdapSettings(Builder<?> builder) {
      super(builder);
      this.ldapMode = builder.ldapMode;
      this.customUsersOu = builder.customUsersOu;
      this.customOrgLdapSettings = builder.customOrgLdapSettings;
   }

   @XmlElement(name = "OrgLdapMode")
    private String ldapMode;
    @XmlElement(name = "CustomUsersOu")
    private String customUsersOu;
    @XmlElement(name = "CustomOrgLdapSettings")
    private CustomOrgLdapSettings customOrgLdapSettings;

    /**
     * Gets the value of the orgLdapMode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLdapMode() {
        return ldapMode;
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
           equal(ldapMode, that.ldapMode) && 
           equal(customUsersOu, that.customUsersOu) && 
           equal(customOrgLdapSettings, that.customOrgLdapSettings);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(),
           ldapMode, 
           customUsersOu, 
           customOrgLdapSettings);
   }

   @Override
   public ToStringHelper string() {
      return super.string()
            .add("orgLdapMode", ldapMode)
            .add("customUsersOu", customUsersOu)
            .add("customOrgLdapSettings", customOrgLdapSettings);
   }

}
