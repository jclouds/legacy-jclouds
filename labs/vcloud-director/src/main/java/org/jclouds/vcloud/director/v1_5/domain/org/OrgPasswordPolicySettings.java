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
 * Java class for OrgPasswordPolicySettings complex type.
 * 
 * <pre>
 * &lt;complexType name="OrgPasswordPolicySettings" /&gt;
 * </pre>
 */
@XmlRootElement(name = "OrgPasswordPolicySettings")
@XmlType(propOrder = {
    "accountLockoutEnabled",
    "invalidLoginsBeforeLockout",
    "accountLockoutIntervalMinutes"
})
public class OrgPasswordPolicySettings extends Resource {
   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return builder().fromOrgPasswordPolicySettings(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public abstract static class Builder<B extends Builder<B>> extends Resource.Builder<B> {
      
      private boolean accountLockoutEnabled;
      private int invalidLoginsBeforeLockout;
      private int accountLockoutIntervalMinutes;

      /**
       * @see OrgPasswordPolicySettings#getAccountLockoutEnabled()
       */
      public B accountLockoutEnabled(boolean accountLockoutEnabled) {
         this.accountLockoutEnabled = accountLockoutEnabled;
         return self();
      }

      /**
       * @see OrgPasswordPolicySettings#getInvalidLoginsBeforeLockout()
       */
      public B invalidLoginsBeforeLockout(int invalidLoginsBeforeLockout) {
         this.invalidLoginsBeforeLockout = invalidLoginsBeforeLockout;
         return self();
      }

      /**
       * @see OrgPasswordPolicySettings#getAccountLockoutIntervalMinutes()
       */
      public B accountLockoutIntervalMinutes(int accountLockoutIntervalMinutes) {
         this.accountLockoutIntervalMinutes = accountLockoutIntervalMinutes;
         return self();
      }

      @Override
      public OrgPasswordPolicySettings build() {
         return new OrgPasswordPolicySettings(this);
      }
      
      public B fromOrgPasswordPolicySettings(OrgPasswordPolicySettings in) {
         return fromResource(in)
            .accountLockoutEnabled(in.isAccountLockoutEnabled())
            .invalidLoginsBeforeLockout(in.getInvalidLoginsBeforeLockout())
            .accountLockoutIntervalMinutes(in.getAccountLockoutIntervalMinutes());
      }
   }

   protected OrgPasswordPolicySettings() {
      // For JAXB
   }

   protected OrgPasswordPolicySettings(Builder<?> builder) {
      super(builder);
      this.accountLockoutEnabled = builder.accountLockoutEnabled;
      this.invalidLoginsBeforeLockout = builder.invalidLoginsBeforeLockout;
      this.accountLockoutIntervalMinutes = builder.accountLockoutIntervalMinutes;
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
