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
 * Java class for OrgPasswordPolicySettings complex type.
 * 
 * <pre>
 * &lt;complexType name="OrgPasswordPolicySettings" /&gt;
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "OrgPasswordPolicySettings")
@XmlType(propOrder = {
    "accountLockoutEnabled",
    "invalidLoginsBeforeLockout",
    "accountLockoutIntervalMinutes"
})
public class OrgPasswordPolicySettings extends ResourceType<OrgPasswordPolicySettings> {
   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromOrgPasswordPolicySettings(this);
   }

   public static class Builder extends ResourceType.Builder<OrgPasswordPolicySettings> {
      
      private boolean accountLockoutEnabled;
      private int invalidLoginsBeforeLockout;
      private int accountLockoutIntervalMinutes;

      /**
       * @see OrgPasswordPolicySettings#getAccountLockoutEnabled()
       */
      public Builder accountLockoutEnabled(boolean accountLockoutEnabled) {
         this.accountLockoutEnabled = accountLockoutEnabled;
         return this;
      }

      /**
       * @see OrgPasswordPolicySettings#getInvalidLoginsBeforeLockout()
       */
      public Builder invalidLoginsBeforeLockout(int invalidLoginsBeforeLockout) {
         this.invalidLoginsBeforeLockout = invalidLoginsBeforeLockout;
         return this;
      }

      /**
       * @see OrgPasswordPolicySettings#getAccountLockoutIntervalMinutes()
       */
      public Builder accountLockoutIntervalMinutes(int accountLockoutIntervalMinutes) {
         this.accountLockoutIntervalMinutes = accountLockoutIntervalMinutes;
         return this;
      }

      public OrgPasswordPolicySettings build() {
         return new OrgPasswordPolicySettings(href, type, links, accountLockoutEnabled, invalidLoginsBeforeLockout, 
               accountLockoutIntervalMinutes);
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
      public Builder fromResourceType(ResourceType<OrgPasswordPolicySettings> in) {
          return Builder.class.cast(super.fromResourceType(in));
      }
      public Builder fromOrgPasswordPolicySettings(OrgPasswordPolicySettings in) {
         return fromResourceType(in)
            .accountLockoutEnabled(in.isAccountLockoutEnabled())
            .invalidLoginsBeforeLockout(in.getInvalidLoginsBeforeLockout())
            .accountLockoutIntervalMinutes(in.getAccountLockoutIntervalMinutes());
      }
   }

   @SuppressWarnings("unused")
   private OrgPasswordPolicySettings() {
      // For JAXB
   }

    public OrgPasswordPolicySettings(URI href, String type, Set<Link> links,
         boolean accountLockoutEnabled, int invalidLoginsBeforeLockout,
         int accountLockoutIntervalMinutes) {
      super(href, type, links);
      this.accountLockoutEnabled = accountLockoutEnabled;
      this.invalidLoginsBeforeLockout = invalidLoginsBeforeLockout;
      this.accountLockoutIntervalMinutes = accountLockoutIntervalMinutes;
   }

   @XmlElement(name = "AccountLockoutEnabled")
    protected boolean accountLockoutEnabled;
    @XmlElement(name = "InvalidLoginsBeforeLockout")
    protected int invalidLoginsBeforeLockout;
    @XmlElement(name = "AccountLockoutIntervalMinutes")
    protected int accountLockoutIntervalMinutes;

    /**
     * Gets the value of the accountLockoutEnabled property.
     * 
     */
    public boolean isAccountLockoutEnabled() {
        return accountLockoutEnabled;
    }

    /**
     * Gets the value of the invalidLoginsBeforeLockout property.
     * 
     */
    public Integer getInvalidLoginsBeforeLockout() {
        return invalidLoginsBeforeLockout;
    }

    /**
     * Gets the value of the accountLockoutIntervalMinutes property.
     * 
     */
    public Integer getAccountLockoutIntervalMinutes() {
        return accountLockoutIntervalMinutes;
    }

   @Override
   public boolean equals(Object o) {
      if (this == o)
          return true;
      if (o == null || getClass() != o.getClass())
         return false;
      OrgPasswordPolicySettings that = OrgPasswordPolicySettings.class.cast(o);
      return super.equals(that) &&
           equal(accountLockoutEnabled, that.accountLockoutEnabled) && 
           equal(invalidLoginsBeforeLockout, that.invalidLoginsBeforeLockout) && 
           equal(accountLockoutIntervalMinutes, that.accountLockoutIntervalMinutes);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), 
           accountLockoutEnabled, 
           invalidLoginsBeforeLockout, 
           accountLockoutIntervalMinutes);
   }

   @Override
   public ToStringHelper string() {
      return super.string()
            .add("accountLockoutEnabled", accountLockoutEnabled)
            .add("invalidLoginsBeforeLockout", invalidLoginsBeforeLockout)
            .add("accountLockoutIntervalMinutes", accountLockoutIntervalMinutes);
   }

}
