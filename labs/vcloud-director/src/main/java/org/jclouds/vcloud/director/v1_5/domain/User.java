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

import static com.google.common.base.Objects.*;
import static com.google.common.base.Preconditions.*;

import java.net.URI;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;

/**
 * 
 *                 Represents users in the vCloud system.
 *             
 * 
 * <p>Java class for User complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
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
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "User", propOrder = {
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
   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

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
       * @see User#getIsEnabled()
       */
      public Builder isEnabled(Boolean isEnabled) {
         this.isEnabled = isEnabled;
         return this;
      }

      /**
       * @see User#getIsLocked()
       */
      public Builder isLocked(Boolean isLocked) {
         this.isLocked = isLocked;
         return this;
      }

      /**
       * @see User#getIm()
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
       * @see User#getIsAlertEnabled()
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
       * @see User#getIsExternal()
       */
      public Builder isExternal(Boolean isExternal) {
         this.isExternal = isExternal;
         return this;
      }

      /**
       * @see User#getIsDefaultCached()
       */
      public Builder isDefaultCached(Boolean isDefaultCached) {
         this.isDefaultCached = isDefaultCached;
         return this;
      }

      /**
       * @see User#getIsGroupRole()
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
         User user = new User(im);
         user.setFullName(fullName);
         user.setEmailAddress(emailAddress);
         user.setTelephone(telephone);
         user.setIsEnabled(isEnabled);
         user.setIsLocked(isLocked);
         user.setNameInSource(nameInSource);
         user.setIsAlertEnabled(isAlertEnabled);
         user.setAlertEmailPrefix(alertEmailPrefix);
         user.setAlertEmail(alertEmail);
         user.setIsExternal(isExternal);
         user.setIsDefaultCached(isDefaultCached);
         user.setIsGroupRole(isGroupRole);
         user.setStoredVmQuota(storedVmQuota);
         user.setDeployedVmQuota(deployedVmQuota);
         user.setRole(role);
         user.setPassword(password);
         user.setGroupReferences(groupReferences);
         return user;
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
       * @see ReferenceType#getLinks()
       */
      @Override
      public Builder links(Set<Link> links) {
         this.links = Sets.newLinkedHashSet(checkNotNull(links, "links"));
         return this;
      }

      /**
       * @see ReferenceType#getLinks()
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

   private User() {
      // For JAXB and builder use
   }

   private User(String im) {
      this.im = im;
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

    /**
     * Gets the value of the fullName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Sets the value of the fullName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFullName(String value) {
        this.fullName = value;
    }

    /**
     * Gets the value of the emailAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * Sets the value of the emailAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmailAddress(String value) {
        this.emailAddress = value;
    }

    /**
     * Gets the value of the telephone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTelephone() {
        return telephone;
    }

    /**
     * Sets the value of the telephone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTelephone(String value) {
        this.telephone = value;
    }

    /**
     * Gets the value of the isEnabled property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isEnabled() {
        return isEnabled;
    }

    /**
     * Sets the value of the isEnabled property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsEnabled(Boolean value) {
        this.isEnabled = value;
    }

    /**
     * Gets the value of the isLocked property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isLocked() {
        return isLocked;
    }

    /**
     * Sets the value of the isLocked property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsLocked(Boolean value) {
        this.isLocked = value;
    }

    /**
     * Gets the value of the im property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIM() {
        return im;
    }

    /**
     * Sets the value of the im property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIM(String value) {
        this.im = value;
    }

    /**
     * Gets the value of the nameInSource property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNameInSource() {
        return nameInSource;
    }

    /**
     * Sets the value of the nameInSource property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNameInSource(String value) {
        this.nameInSource = value;
    }

    /**
     * Gets the value of the isAlertEnabled property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAlertEnabled() {
        return isAlertEnabled;
    }

    /**
     * Sets the value of the isAlertEnabled property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsAlertEnabled(Boolean value) {
        this.isAlertEnabled = value;
    }

    /**
     * Gets the value of the alertEmailPrefix property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAlertEmailPrefix() {
        return alertEmailPrefix;
    }

    /**
     * Sets the value of the alertEmailPrefix property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAlertEmailPrefix(String value) {
        this.alertEmailPrefix = value;
    }

    /**
     * Gets the value of the alertEmail property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAlertEmail() {
        return alertEmail;
    }

    /**
     * Sets the value of the alertEmail property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAlertEmail(String value) {
        this.alertEmail = value;
    }

    /**
     * Gets the value of the isExternal property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isExternal() {
        return isExternal;
    }

    /**
     * Sets the value of the isExternal property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsExternal(Boolean value) {
        this.isExternal = value;
    }

    /**
     * Gets the value of the isDefaultCached property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDefaultCached() {
        return isDefaultCached;
    }

    /**
     * Sets the value of the isDefaultCached property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsDefaultCached(Boolean value) {
        this.isDefaultCached = value;
    }

    /**
     * Gets the value of the isGroupRole property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isGroupRole() {
        return isGroupRole;
    }

    /**
     * Sets the value of the isGroupRole property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsGroupRole(Boolean value) {
        this.isGroupRole = value;
    }

    /**
     * Gets the value of the storedVmQuota property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getStoredVmQuota() {
        return storedVmQuota;
    }

    /**
     * Sets the value of the storedVmQuota property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setStoredVmQuota(Integer value) {
        this.storedVmQuota = value;
    }

    /**
     * Gets the value of the deployedVmQuota property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getDeployedVmQuota() {
        return deployedVmQuota;
    }

    /**
     * Sets the value of the deployedVmQuota property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setDeployedVmQuota(Integer value) {
        this.deployedVmQuota = value;
    }

    /**
     * Gets the value of the role property.
     * 
     * @return
     *     possible object is
     *     {@link Reference }
     *     
     */
    public Reference getRole() {
        return role;
    }

    /**
     * Sets the value of the role property.
     * 
     * @param value
     *     allowed object is
     *     {@link Reference }
     *     
     */
    public void setRole(Reference value) {
        this.role = value;
    }

    /**
     * Gets the value of the password property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the value of the password property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPassword(String value) {
        this.password = value;
    }

    /**
     * Gets the value of the groupReferences property.
     * 
     * @return
     *     possible object is
     *     {@link GroupsList }
     *     
     */
    public Object /* GroupsList */ getGroupReferences() {
        return groupReferences;
    }

    /**
     * Sets the value of the groupReferences property.
     * 
     * @param value
     *     allowed object is
     *     {@link GroupsList }
     *     
     */
    public void setGroupReferences(Object /* GroupsList */ value) {
        this.groupReferences = value;
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
