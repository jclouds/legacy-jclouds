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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Defines how LDAP attributes are used when importing a user.
 *
 * <pre>
 * &lt;complexType name="OrgLdapUserAttributes">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}VCloudExtensibleType">
 *       &lt;sequence>
 *         &lt;element name="ObjectClass" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ObjectIdentifier" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="UserName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Email" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="FullName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="GivenName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Surname" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Telephone" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="GroupMembershipIdentifier" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="GroupBackLinkIdentifier" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlType(name = "OrgLdapUserAttributes", propOrder = {
    "objectClass",
    "objectIdentifier",
    "userName",
    "email",
    "fullName",
    "givenName",
    "surname",
    "telephone",
    "groupMembershipIdentifier",
    "groupBackLinkIdentifier"
})
public class OrgLdapUserAttributes {
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromOrgLdapUserAttributes(this);
   }

   public static class Builder {
      
      private String objectClass;
      private String objectIdentifier;
      private String userName;
      private String email;
      private String fullName;
      private String givenName;
      private String surname;
      private String telephone;
      private String groupMembershipIdentifier;
      private String groupBackLinkIdentifier;

      /**
       * @see OrgLdapUserAttributes#getObjectClass()
       */
      public Builder objectClass(String objectClass) {
         this.objectClass = objectClass;
         return this;
      }

      /**
       * @see OrgLdapUserAttributes#getObjectIdentifier()
       */
      public Builder objectIdentifier(String objectIdentifier) {
         this.objectIdentifier = objectIdentifier;
         return this;
      }

      /**
       * @see OrgLdapUserAttributes#getUserName()
       */
      public Builder userName(String userName) {
         this.userName = userName;
         return this;
      }

      /**
       * @see OrgLdapUserAttributes#getEmail()
       */
      public Builder email(String email) {
         this.email = email;
         return this;
      }

      /**
       * @see OrgLdapUserAttributes#getFullName()
       */
      public Builder fullName(String fullName) {
         this.fullName = fullName;
         return this;
      }

      /**
       * @see OrgLdapUserAttributes#getGivenName()
       */
      public Builder givenName(String givenName) {
         this.givenName = givenName;
         return this;
      }

      /**
       * @see OrgLdapUserAttributes#getSurname()
       */
      public Builder surname(String surname) {
         this.surname = surname;
         return this;
      }

      /**
       * @see OrgLdapUserAttributes#getTelephone()
       */
      public Builder telephone(String telephone) {
         this.telephone = telephone;
         return this;
      }

      /**
       * @see OrgLdapUserAttributes#getGroupMembershipIdentifier()
       */
      public Builder groupMembershipIdentifier(String groupMembershipIdentifier) {
         this.groupMembershipIdentifier = groupMembershipIdentifier;
         return this;
      }

      /**
       * @see OrgLdapUserAttributes#getGroupBackLinkIdentifier()
       */
      public Builder groupBackLinkIdentifier(String groupBackLinkIdentifier) {
         this.groupBackLinkIdentifier = groupBackLinkIdentifier;
         return this;
      }


      public OrgLdapUserAttributes build() {
         return new OrgLdapUserAttributes(objectClass, objectIdentifier, userName, email, 
               fullName, givenName, surname, telephone, groupMembershipIdentifier, groupBackLinkIdentifier);
      }

      public Builder fromOrgLdapUserAttributes(OrgLdapUserAttributes in) {
         return objectClass(in.getObjectClass())
            .objectIdentifier(in.getObjectIdentifier())
            .userName(in.getUserName())
            .email(in.getEmail())
            .fullName(in.getFullName())
            .givenName(in.getGivenName())
            .surname(in.getSurname())
            .telephone(in.getTelephone())
            .groupMembershipIdentifier(in.getGroupMembershipIdentifier())
            .groupBackLinkIdentifier(in.getGroupBackLinkIdentifier());
      }
   }

   @SuppressWarnings("unused")
   private OrgLdapUserAttributes() {
      // For JAXB
   }

    public OrgLdapUserAttributes(String objectClass, String objectIdentifier,
         String userName, String email, String fullName, String givenName,
         String surname, String telephone, String groupMembershipIdentifier,
         String groupBackLinkIdentifier) {
      this.objectClass = objectClass;
      this.objectIdentifier = objectIdentifier;
      this.userName = userName;
      this.email = email;
      this.fullName = fullName;
      this.givenName = givenName;
      this.surname = surname;
      this.telephone = telephone;
      this.groupMembershipIdentifier = groupMembershipIdentifier;
      this.groupBackLinkIdentifier = groupBackLinkIdentifier;
   }

   @XmlElement(name = "ObjectClass", required = true)
    protected String objectClass;
    @XmlElement(name = "ObjectIdentifier", required = true)
    protected String objectIdentifier;
    @XmlElement(name = "UserName", required = true)
    protected String userName;
    @XmlElement(name = "Email", required = true)
    protected String email;
    @XmlElement(name = "FullName", required = true)
    protected String fullName;
    @XmlElement(name = "GivenName", required = true)
    protected String givenName;
    @XmlElement(name = "Surname", required = true)
    protected String surname;
    @XmlElement(name = "Telephone", required = true)
    protected String telephone;
    @XmlElement(name = "GroupMembershipIdentifier", required = true)
    protected String groupMembershipIdentifier;
    @XmlElement(name = "GroupBackLinkIdentifier")
    protected String groupBackLinkIdentifier;

    /**
     * Gets the value of the objectClass property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getObjectClass() {
        return objectClass;
    }

    /**
     * Gets the value of the objectIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getObjectIdentifier() {
        return objectIdentifier;
    }

    /**
     * Gets the value of the userName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Gets the value of the email property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmail() {
        return email;
    }

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
     * Gets the value of the givenName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGivenName() {
        return givenName;
    }

    /**
     * Gets the value of the surname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSurname() {
        return surname;
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
     * Gets the value of the groupMembershipIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGroupMembershipIdentifier() {
        return groupMembershipIdentifier;
    }

    /**
     * Gets the value of the groupBackLinkIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGroupBackLinkIdentifier() {
        return groupBackLinkIdentifier;
    }

   @Override
   public boolean equals(Object o) {
      if (this == o)
          return true;
      if (o == null || getClass() != o.getClass())
         return false;
      OrgLdapUserAttributes that = OrgLdapUserAttributes.class.cast(o);
      return equal(objectClass, that.objectClass) && 
           equal(objectIdentifier, that.objectIdentifier) && 
           equal(userName, that.userName) && 
           equal(email, that.email) && 
           equal(fullName, that.fullName) && 
           equal(givenName, that.givenName) && 
           equal(surname, that.surname) && 
           equal(telephone, that.telephone) && 
           equal(groupMembershipIdentifier, that.groupMembershipIdentifier) && 
           equal(groupBackLinkIdentifier, that.groupBackLinkIdentifier);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(objectClass, 
           objectIdentifier, 
           userName, 
           email, 
           fullName, 
           givenName, 
           surname, 
           telephone, 
           groupMembershipIdentifier, 
           groupBackLinkIdentifier);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public ToStringHelper string() {
      return Objects.toStringHelper("")
            .add("objectClass", objectClass)
            .add("objectIdentifier", objectIdentifier)
            .add("userName", userName)
            .add("email", email)
            .add("fullName", fullName)
            .add("givenName", givenName)
            .add("surname", surname)
            .add("telephone", telephone)
            .add("groupMembershipIdentifier", groupMembershipIdentifier)
            .add("groupBackLinkIdentifier", groupBackLinkIdentifier);
   }

}
