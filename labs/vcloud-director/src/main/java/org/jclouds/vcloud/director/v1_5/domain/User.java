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

import java.net.URI;
import java.util.Set;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;

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
      "groupReferences"
})
public class User
      extends EntityType<User>

{
   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return new Builder().fromUser(this);
   }

   public static class Builder extends EntityType.Builder<User> {

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
      private Object /* GroupsList */ groupReferences;

      /**
       * @see User#getFullName()
       */
      public Builder fullName(String fullName) {
         this.fullName = fullName;
         return this;
      }

      /**
       * @see User#getEmailAddress()
       */
      public Builder emailAddress(String emailAddress) {
         this.emailAddress = emailAddress;
         return this;
      }

      /**
       * @see User#getTelephone()
       */
      public Builder telephone(String telephone) {
         this.telephone = telephone;
         return this;
      }

      /**
       * @see User#isEnabled()
       */
      public Builder isEnabled(Boolean isEnabled) {
         this.isEnabled = isEnabled;
         return this;
      }

      /**
       * @see User#isLocked()
       */
      public Builder isLocked(Boolean isLocked) {
         this.isLocked = isLocked;
         return this;
      }

      /**
       * @see User#getIM()
       */
      public Builder im(String im) {
         this.im = im;
         return this;
      }

      /**
       * @see User#getNameInSource()
       */
      public Builder nameInSource(String nameInSource) {
         this.nameInSource = nameInSource;
         return this;
      }

      /**
       * @see User#isAlertEnabled()
       */
      public Builder isAlertEnabled(Boolean isAlertEnabled) {
         this.isAlertEnabled = isAlertEnabled;
         return this;
      }

      /**
       * @see User#getAlertEmailPrefix()
       */
      public Builder alertEmailPrefix(String alertEmailPrefix) {
         this.alertEmailPrefix = alertEmailPrefix;
         return this;
      }

      /**
       * @see User#getAlertEmail()
       */
      public Builder alertEmail(String alertEmail) {
         this.alertEmail = alertEmail;
         return this;
      }

      /**
       * @see User#isExternal()
       */
      public Builder isExternal(Boolean isExternal) {
         this.isExternal = isExternal;
         return this;
      }

      /**
       * @see User#isDefaultCached()
       */
      public Builder isDefaultCached(Boolean isDefaultCached) {
         this.isDefaultCached = isDefaultCached;
         return this;
      }

      /**
       * @see User#isGroupRole()
       */
      public Builder isGroupRole(Boolean isGroupRole) {
         this.isGroupRole = isGroupRole;
         return this;
      }

      /**
       * @see User#getStoredVmQuota()
       */
      public Builder storedVmQuota(Integer storedVmQuota) {
         this.storedVmQuota = storedVmQuota;
         return this;
      }

      /**
       * @see User#getDeployedVmQuota()
       */
      public Builder deployedVmQuota(Integer deployedVmQuota) {
         this.deployedVmQuota = deployedVmQuota;
         return this;
      }

      /**
       * @see User#getRole()
       */
      public Builder role(Reference role) {
         this.role = role;
         return this;
      }

      /**
       * @see User#getPassword()
       */
      public Builder password(String password) {
         this.password = password;
         return this;
      }

      /**
       * @see User#getGroupReferences()
       */
      public Builder groupReferences(Object /* GroupsList */ groupReferences) {
         this.groupReferences = groupReferences;
         return this;
      }


      public User build() {
         return new User(href, type, links, description, tasksInProgress, id,
               name, fullName, emailAddress, telephone, isEnabled, isLocked,
               im, nameInSource, isAlertEnabled, alertEmailPrefix, alertEmail,
               isExternal, isDefaultCached, isGroupRole, storedVmQuota, deployedVmQuota,
               role, password, groupReferences);
      }


      /**
       * @see EntityType#getId()
       */
      @Override
      public Builder id(String id) {
         this.id = id;
         return this;
      }

      /**
       * @see EntityType#getTasksInProgress()
       */
      @Override
      public Builder tasksInProgress(TasksInProgress tasksInProgress) {
         this.tasksInProgress = tasksInProgress;
         return this;
      }

      /**
       * @see ReferenceType#getHref()
       */
      @Override
      public Builder href(URI href) {
         this.href = href;
         return this;
      }

      /**
       * @see ReferenceType#getType()
       */
      @Override
      public Builder type(String type) {
         this.type = type;
         return this;
      }

      /**
       * @see EntityType#getLinks()
       */
      @Override
      public Builder links(Set<Link> links) {
         this.links = Sets.newLinkedHashSet(checkNotNull(links, "links"));
         return this;
      }

      /**
       * @see EntityType#getLinks()
       */
      @Override
      public Builder link(Link link) {
         this.links.add(checkNotNull(link, "link"));
         return this;
      }


      @Override
      public Builder fromEntityType(EntityType<User> in) {
         return Builder.class.cast(super.fromEntityType(in));
      }

      public Builder fromUser(User in) {
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
               .groupReferences(in.getGroupReferences());
      }
   }

   @XmlElement(name = "FullName")
   protected String fullName;
   @XmlElement(name = "EmailAddress")
   protected String emailAddress;
   @XmlElement(name = "Telephone")
   protected String telephone;
   @XmlElement(name = "IsEnabled")
   protected Boolean isEnabled;
   @XmlElement(name = "IsLocked")
   protected Boolean isLocked;
   @XmlElement(name = "IM")
   protected String im;
   @XmlElement(name = "NameInSource")
   protected String nameInSource;
   @XmlElement(name = "IsAlertEnabled")
   protected Boolean isAlertEnabled;
   @XmlElement(name = "AlertEmailPrefix")
   protected String alertEmailPrefix;
   @XmlElement(name = "AlertEmail")
   protected String alertEmail;
   @XmlElement(name = "IsExternal")
   protected Boolean isExternal;
   @XmlElement(name = "IsDefaultCached")
   protected Boolean isDefaultCached;
   @XmlElement(name = "IsGroupRole")
   protected Boolean isGroupRole;
   @XmlElement(name = "StoredVmQuota")
   protected Integer storedVmQuota;
   @XmlElement(name = "DeployedVmQuota")
   protected Integer deployedVmQuota;
   @XmlElement(name = "Role")
   protected Reference role;
   @XmlElement(name = "Password")
   protected String password;
   @XmlElement(name = "GroupReferences")
   protected Object /* GroupsList */ groupReferences;

   public User(URI href, String type, Set<Link> links, String description, TasksInProgress tasksInProgress, String id,
               String name, String fullName, String emailAddress, String telephone, Boolean enabled, Boolean locked,
               String im, String nameInSource, Boolean alertEnabled, String alertEmailPrefix, String alertEmail,
               Boolean external, Boolean defaultCached, Boolean groupRole, Integer storedVmQuota, Integer deployedVmQuota,
               Reference role, String password, Object groupReferences) {
      super(href, type, links, description, tasksInProgress, id, name);
      this.fullName = fullName;
      this.emailAddress = emailAddress;
      this.telephone = telephone;
      isEnabled = enabled;
      isLocked = locked;
      this.im = checkNotNull(im, "im");
      this.nameInSource = nameInSource;
      isAlertEnabled = alertEnabled;
      this.alertEmailPrefix = alertEmailPrefix;
      this.alertEmail = alertEmail;
      isExternal = external;
      isDefaultCached = defaultCached;
      isGroupRole = groupRole;
      this.storedVmQuota = storedVmQuota;
      this.deployedVmQuota = deployedVmQuota;
      this.role = role;
      this.password = password;
      this.groupReferences = groupReferences;
   }

   private User() {
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
   public Object /* GroupsList */ getGroupReferences() {
      return groupReferences;
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
            equal(groupReferences, that.groupReferences);
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
            groupReferences);
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
            .add("groupReferences", groupReferences).toString();
   }

}
