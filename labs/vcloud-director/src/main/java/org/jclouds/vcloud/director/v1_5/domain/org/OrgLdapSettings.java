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
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.jclouds.vcloud.director.v1_5.domain.Resource;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
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
public class OrgLdapSettings extends Resource {
   
   @XmlType
   @XmlEnum(String.class)
   public static enum LdapMode {
      @XmlEnumValue("NONE") NONE("NONE"),
      @XmlEnumValue("SYSTEM") SYSTEM("SYSTEM"),
      @XmlEnumValue("CUSTOM") CUSTOM("CUSTOM"),
      UNRECOGNIZED("unrecognized");
      
      public static final List<LdapMode> ALL = ImmutableList.of( NONE, SYSTEM, CUSTOM );

      protected final String stringValue;

      LdapMode(String stringValue) {
         this.stringValue = stringValue;
      }

      public String value() {
         return stringValue;
      }

      protected static final Map<String, LdapMode> LDAP_MODE_BY_ID = Maps.uniqueIndex(
            ImmutableSet.copyOf(LdapMode.values()), new Function<LdapMode, String>() {
               @Override
               public String apply(LdapMode input) {
                  return input.stringValue;
               }
            });

      public static LdapMode fromValue(String value) {
         LdapMode mode = LDAP_MODE_BY_ID.get(checkNotNull(value, "stringValue"));
         return mode == null ? UNRECOGNIZED : mode;
      }
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
   
   public abstract static class Builder<B extends Builder<B>> extends Resource.Builder<B> {
      
      private LdapMode ldapMode;
      private String customUsersOu;
      private CustomOrgLdapSettings customOrgLdapSettings;

      /**
       * @see OrgLdapSettings#getLdapMode()
       */
      public B ldapMode(LdapMode ldapMode) {
         this.ldapMode = ldapMode;
         return self();
      }

      /**
       * @see OrgLdapSettings#getLdapMode()
       */
      public B ldapMode(String ldapMode) {
         this.ldapMode = LdapMode.fromValue(ldapMode);
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
         return fromResource(in)
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
    private LdapMode ldapMode;
    @XmlElement(name = "CustomUsersOu")
    private String customUsersOu;
    @XmlElement(name = "CustomOrgLdapSettings")
    private CustomOrgLdapSettings customOrgLdapSettings;

    /**
     * Gets the value of the orgLdapMode property.
     */
    public LdapMode getLdapMode() {
        return ldapMode;
    }

    /**
     * Gets the value of the customUsersOu property.
     */
    public String getCustomUsersOu() {
        return customUsersOu;
    }

    /**
     * Gets the value of the customOrgLdapSettings property.
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
