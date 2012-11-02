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
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * Represents users in the vCloud system.
 * <p/>
 * <p/>
 * <p>Java class for User complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="User">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}EntityType">
 *       &lt;sequence>
 *         &lt;element name="FullName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="EmailAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Telephone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="IsEnabled" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="IsLocked" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="IM" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="NameInSource" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="IsAlertEnabled" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="AlertEmailPrefix" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="AlertEmail" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="IsExternal" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="IsDefaultCached" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="IsGroupRole" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="StoredVmQuota" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="DeployedVmQuota" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="Role" type="{http://www.vmware.com/vcloud/v1.5}ReferenceType" minOccurs="0"/>
 *         &lt;element name="Password" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="GroupReferences" type="{http://www.vmware.com/vcloud/v1.5}GroupsListType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement(name = "User")
@XmlType(propOrder = {
      "fullName",
      "emailAddress",
      "telephone",
      "isEnabled",
      "isLocked",
      "im",
      "nameInSource",
      "isAlertEnabled",
      "alertEmailPrefix",
      "alertEmail",
      "isExternal",
      "isDefaultCached",
      "isGroupRole",
      "storedVmQuota",
      "deployedVmQuota",
      "role",
      "password",
      "groups"
})
public class User extends Entity {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return builder().fromUser(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public abstract static class Builder<B extends Builder<B>> extends Entity.Builder<B> {

      private String fullName;
      private String emailAddress;
      private String telephone;
      private Boolean isEnabled;
      private Boolean isLocked;
      private String im;
      private String nameInSource;
      private Boolean isAlertEnabled;
      private String alertEmailPrefix;
      private String alertEmail;
      private Boolean isExternal;
      private Boolean isDefaultCached;
      private Boolean isGroupRole;
      private Integer storedVmQuota;
      private Integer deployedVmQuota;
      private Reference role;
      private String password;
      private List<Reference> groups = Lists.newArrayList();
      
      /**
       * @see EntityType#getName()
       */
      @Override
      public B name(String name) {
         return super.name(name.toLowerCase());
      }
      
      /**
       * @see User#getFullName()
       */
      public B fullName(String fullName) {
         this.fullName = fullName;
         return self();
      }

      /**
       * @see User#getEmailAddress()
       */
      public B emailAddress(String emailAddress) {
         this.emailAddress = emailAddress;
         return self();
      }

      /**
       * @see User#getTelephone()
       */
      public B telephone(String telephone) {
         this.telephone = telephone;
         return self();
      }

      /**
       * @see User#isEnabled()
       */
      public B isEnabled(Boolean isEnabled) {
         this.isEnabled = isEnabled;
         return self();
      }

      /**
       * @see User#isLocked()
       */
      public B isLocked(Boolean isLocked) {
         this.isLocked = isLocked;
         return self();
      }

      /**
       * @see User#getIM()
       */
      public B im(String im) {
         this.im = im;
         return self();
      }

      /**
       * @see User#getNameInSource()
       */
      public B nameInSource(String nameInSource) {
         this.nameInSource = nameInSource;
         return self();
      }

      /**
       * @see User#isAlertEnabled()
       */
      public B isAlertEnabled(Boolean isAlertEnabled) {
         this.isAlertEnabled = isAlertEnabled;
         return self();
      }

      /**
       * @see User#getAlertEmailPrefix()
       */
      public B alertEmailPrefix(String alertEmailPrefix) {
         this.alertEmailPrefix = alertEmailPrefix;
         return self();
      }

      /**
       * @see User#getAlertEmail()
       */
      public B alertEmail(String alertEmail) {
         this.alertEmail = alertEmail;
         return self();
      }

      /**
       * @see User#isExternal()
       */
      public B isExternal(Boolean isExternal) {
         this.isExternal = isExternal;
         return self();
      }

      /**
       * @see User#isDefaultCached()
       */
      public B isDefaultCached(Boolean isDefaultCached) {
         this.isDefaultCached = isDefaultCached;
         return self();
      }

      /**
       * @see User#isGroupRole()
       */
      public B isGroupRole(Boolean isGroupRole) {
         this.isGroupRole = isGroupRole;
         return self();
      }

      /**
       * @see User#getStoredVmQuota()
       */
      public B storedVmQuota(Integer storedVmQuota) {
         this.storedVmQuota = storedVmQuota;
         return self();
      }

      /**
       * @see User#getDeployedVmQuota()
       */
      public B deployedVmQuota(Integer deployedVmQuota) {
         this.deployedVmQuota = deployedVmQuota;
         return self();
      }

      /**
       * @see User#getRole()
       */
      public B role(Reference role) {
         this.role = role;
         return self();
      }

      /**
       * @see User#getPassword()
       */
      public B password(String password) {
         this.password = password;
         return self();
      }

      /**
       * @see User#getGroups()
       */
      public B groups(List<Reference> groups) {
         this.groups = groups == null ? null : ImmutableList.copyOf(groups);
         return self();
      }
      
      /**
       * @see User#getGroups()
       */
      public B group(Reference group) {
         this.groups.add(checkNotNull(group, "group"));
         return self();
      }

      @Override
      public User build() {
         return new User(this);
      }

      public B fromUser(User in) {
         return fromEntityType(in)
               .fullName(in.getFullName())
               .emailAddress(in.getEmailAddress())
               .telephone(in.getTelephone())
               .isEnabled(in.isEnabled())
               .isLocked(in.isLocked())
               .im(in.getIM())
               .nameInSource(in.getNameInSource())
               .isAlertEnabled(in.isAlertEnabled())
               .alertEmailPrefix(in.getAlertEmailPrefix())
               .alertEmail(in.getAlertEmail())
               .isExternal(in.isExternal())
               .isDefaultCached(in.isDefaultCached())
               .isGroupRole(in.isGroupRole())
               .storedVmQuota(in.getStoredVmQuota())
               .deployedVmQuota(in.getDeployedVmQuota())
               .role(in.getRole())
               .password(in.getPassword())
               .groups(in.getGroups());
      }
   }

   @XmlElement(name = "FullName")
   private String fullName;
   @XmlElement(name = "EmailAddress")
   private String emailAddress;
   @XmlElement(name = "Telephone")
   private String telephone;
   @XmlElement(name = "IsEnabled")
   private Boolean isEnabled;
   @XmlElement(name = "IsLocked")
   private Boolean isLocked;
   @XmlElement(name = "IM")
   private String im;
   @XmlElement(name = "NameInSource")
   private String nameInSource;
   @XmlElement(name = "IsAlertEnabled")
   private Boolean isAlertEnabled;
   @XmlElement(name = "AlertEmailPrefix")
   private String alertEmailPrefix;
   @XmlElement(name = "AlertEmail")
   private String alertEmail;
   @XmlElement(name = "IsExternal")
   private Boolean isExternal;
   @XmlElement(name = "IsDefaultCached")
   private Boolean isDefaultCached;
   @XmlElement(name = "IsGroupRole")
   private Boolean isGroupRole;
   @XmlElement(name = "StoredVmQuota")
   private Integer storedVmQuota;
   @XmlElement(name = "DeployedVmQuota")
   private Integer deployedVmQuota;
   @XmlElement(name = "Role")
   private Reference role;
   @XmlElement(name = "Password")
   private String password;
   @XmlElementWrapper(name = "GroupReferences")
   private List<Reference> groups;

   protected User(Builder<?> builder) {
      super(builder);
      this.fullName = builder.fullName;
      this.emailAddress = builder.emailAddress;
      this.telephone = builder.telephone;
      isEnabled = builder.isEnabled;
      isLocked = builder.isLocked;
      this.im = builder.im;
      this.nameInSource = builder.nameInSource;
      isAlertEnabled = builder.isAlertEnabled;
      this.alertEmailPrefix = builder.alertEmailPrefix;
      this.alertEmail = builder.alertEmail;
      isExternal = builder.isExternal;
      isDefaultCached = builder.isDefaultCached;
      isGroupRole = builder.isGroupRole;
      this.storedVmQuota = builder.storedVmQuota;
      this.deployedVmQuota = builder.deployedVmQuota;
      this.role = builder.role;
      this.password = builder.password;
      this.groups = builder.groups;
   }

   protected User() {
      // For JAXB
   }

   /**
    * Gets the value of the fullName property.
    *
    * @return possible object is
    *         {@link String }
    */
   public String getFullName() {
      return fullName;
   }

   /**
    * Gets the value of the emailAddress property.
    *
    * @return possible object is
    *         {@link String }
    */
   public String getEmailAddress() {
      return emailAddress;
   }

   /**
    * Gets the value of the telephone property.
    *
    * @return possible object is
    *         {@link String }
    */
   public String getTelephone() {
      return telephone;
   }

   /**
    * Gets the value of the isEnabled property.
    *
    * @return possible object is
    *         {@link Boolean }
    */
   public Boolean isEnabled() {
      return isEnabled;
   }

   /**
    * Gets the value of the isLocked property.
    *
    * @return possible object is
    *         {@link Boolean }
    */
   public Boolean isLocked() {
      return isLocked;
   }

   /**
    * Gets the value of the im property.
    *
    * @return possible object is
    *         {@link String }
    */
   public String getIM() {
      return im;
   }

   /**
    * Gets the value of the nameInSource property.
    *
    * @return possible object is
    *         {@link String }
    */
   public String getNameInSource() {
      return nameInSource;
   }

   /**
    * Gets the value of the isAlertEnabled property.
    *
    * @return possible object is
    *         {@link Boolean }
    */
   public Boolean isAlertEnabled() {
      return isAlertEnabled;
   }

   /**
    * Gets the value of the alertEmailPrefix property.
    *
    * @return possible object is
    *         {@link String }
    */
   public String getAlertEmailPrefix() {
      return alertEmailPrefix;
   }

   /**
    * Gets the value of the alertEmail property.
    *
    * @return possible object is
    *         {@link String }
    */
   public String getAlertEmail() {
      return alertEmail;
   }

   /**
    * Gets the value of the isExternal property.
    *
    * @return possible object is
    *         {@link Boolean }
    */
   public Boolean isExternal() {
      return isExternal;
   }

   /**
    * Gets the value of the isDefaultCached property.
    *
    * @return possible object is
    *         {@link Boolean }
    */
   public Boolean isDefaultCached() {
      return isDefaultCached;
   }

   /**
    * Gets the value of the isGroupRole property.
    *
    * @return possible object is
    *         {@link Boolean }
    */
   public Boolean isGroupRole() {
      return isGroupRole;
   }

   /**
    * Gets the value of the storedVmQuota property.
    *
    * @return possible object is
    *         {@link Integer }
    */
   public Integer getStoredVmQuota() {
      return storedVmQuota;
   }

   /**
    * Gets the value of the deployedVmQuota property.
    *
    * @return possible object is
    *         {@link Integer }
    */
   public Integer getDeployedVmQuota() {
      return deployedVmQuota;
   }

   /**
    * Gets the value of the role property.
    *
    * @return possible object is
    *         {@link Reference }
    */
   public Reference getRole() {
      return role;
   }

   /**
    * Gets the value of the password property.
    *
    * @return possible object is
    *         {@link String }
    */
   public String getPassword() {
      return password;
   }

   /**
    * Gets the value of the groupReferences property.
    *
    * @return possible object is
    *         {@link GroupsListType }
    */
   public List<Reference> getGroups() {
      return groups == null ? Lists.<Reference>newArrayList() : groups;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      User that = User.class.cast(o);
      return equal(fullName, that.fullName) &&
            equal(emailAddress, that.emailAddress) &&
            equal(telephone, that.telephone) &&
            equal(isEnabled, that.isEnabled) &&
            equal(isLocked, that.isLocked) &&
            equal(im, that.im) &&
            equal(nameInSource, that.nameInSource) &&
            equal(isAlertEnabled, that.isAlertEnabled) &&
            equal(alertEmailPrefix, that.alertEmailPrefix) &&
            equal(alertEmail, that.alertEmail) &&
            equal(isExternal, that.isExternal) &&
            equal(isDefaultCached, that.isDefaultCached) &&
            equal(isGroupRole, that.isGroupRole) &&
            equal(storedVmQuota, that.storedVmQuota) &&
            equal(deployedVmQuota, that.deployedVmQuota) &&
            equal(role, that.role) &&
            equal(password, that.password) &&
            equal(groups, that.groups);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(fullName,
            emailAddress,
            telephone,
            isEnabled,
            isLocked,
            im,
            nameInSource,
            isAlertEnabled,
            alertEmailPrefix,
            alertEmail,
            isExternal,
            isDefaultCached,
            isGroupRole,
            storedVmQuota,
            deployedVmQuota,
            role,
            password,
            groups);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("")
            .add("fullName", fullName)
            .add("emailAddress", emailAddress)
            .add("telephone", telephone)
            .add("isEnabled", isEnabled)
            .add("isLocked", isLocked)
            .add("im", im)
            .add("nameInSource", nameInSource)
            .add("isAlertEnabled", isAlertEnabled)
            .add("alertEmailPrefix", alertEmailPrefix)
            .add("alertEmail", alertEmail)
            .add("isExternal", isExternal)
            .add("isDefaultCached", isDefaultCached)
            .add("isGroupRole", isGroupRole)
            .add("storedVmQuota", storedVmQuota)
            .add("deployedVmQuota", deployedVmQuota)
            .add("role", role)
            .add("password", password)
            .add("groups", groups).toString();
   }
}
