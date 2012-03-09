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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;


/**
 * 
 *                 Defines how a group is imported from LDAP.
 *             
 * 
 * <p>Java class for OrgLdapGroupAttributes complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OrgLdapGroupAttributes">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}VCloudExtensibleType">
 *       &lt;sequence>
 *         &lt;element name="ObjectClass" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ObjectIdentifier" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="GroupName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Membership" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="MembershipIdentifier" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="BackLinkIdentifier" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
@XmlType(name = "OrgLdapGroupAttributes", propOrder = {
    "objectClass",
    "objectIdentifier",
    "groupName",
    "membership",
    "membershipIdentifier",
    "backLinkIdentifier"
})
public class OrgLdapGroupAttributes {
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromOrgLdapGroupAttributes(this);
   }

   public static class Builder {
      
      private String objectClass;
      private String objectIdentifier;
      private String groupName;
      private String membership;
      private String membershipIdentifier;
      private String backLinkIdentifier;

      /**
       * @see OrgLdapGroupAttributes#getObjectClass()
       */
      public Builder objectClass(String objectClass) {
         this.objectClass = objectClass;
         return this;
      }

      /**
       * @see OrgLdapGroupAttributes#getObjectIdentifier()
       */
      public Builder objectIdentifier(String objectIdentifier) {
         this.objectIdentifier = objectIdentifier;
         return this;
      }

      /**
       * @see OrgLdapGroupAttributes#getGroupName()
       */
      public Builder groupName(String groupName) {
         this.groupName = groupName;
         return this;
      }

      /**
       * @see OrgLdapGroupAttributes#getMembership()
       */
      public Builder membership(String membership) {
         this.membership = membership;
         return this;
      }

      /**
       * @see OrgLdapGroupAttributes#getMembershipIdentifier()
       */
      public Builder membershipIdentifier(String membershipIdentifier) {
         this.membershipIdentifier = membershipIdentifier;
         return this;
      }

      /**
       * @see OrgLdapGroupAttributes#getBackLinkIdentifier()
       */
      public Builder backLinkIdentifier(String backLinkIdentifier) {
         this.backLinkIdentifier = backLinkIdentifier;
         return this;
      }


      public OrgLdapGroupAttributes build() {
         return new OrgLdapGroupAttributes(objectClass, objectIdentifier, groupName, 
               membership, membershipIdentifier, backLinkIdentifier);
      }

      public Builder fromOrgLdapGroupAttributes(OrgLdapGroupAttributes in) {
         return objectClass(in.getObjectClass())
            .objectIdentifier(in.getObjectIdentifier())
            .groupName(in.getGroupName())
            .membership(in.getMembership())
            .membershipIdentifier(in.getMembershipIdentifier())
            .backLinkIdentifier(in.getBackLinkIdentifier());
      }
   }

   @SuppressWarnings("unused")
   private OrgLdapGroupAttributes() {
      // For JAXB
   }

    public OrgLdapGroupAttributes(String objectClass, String objectIdentifier,
         String groupName, String membership, String membershipIdentifier,
         String backLinkIdentifier) {
      this.objectClass = objectClass;
      this.objectIdentifier = objectIdentifier;
      this.groupName = groupName;
      this.membership = membership;
      this.membershipIdentifier = membershipIdentifier;
      this.backLinkIdentifier = backLinkIdentifier;
   }

   @XmlElement(name = "ObjectClass", required = true)
    protected String objectClass;
    @XmlElement(name = "ObjectIdentifier", required = true)
    protected String objectIdentifier;
    @XmlElement(name = "GroupName", required = true)
    protected String groupName;
    @XmlElement(name = "Membership", required = true)
    protected String membership;
    @XmlElement(name = "MembershipIdentifier", required = true)
    protected String membershipIdentifier;
    @XmlElement(name = "BackLinkIdentifier")
    protected String backLinkIdentifier;

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
     * Gets the value of the groupName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * Gets the value of the membership property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMembership() {
        return membership;
    }

    /**
     * Gets the value of the membershipIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMembershipIdentifier() {
        return membershipIdentifier;
    }

    /**
     * Gets the value of the backLinkIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBackLinkIdentifier() {
        return backLinkIdentifier;
    }

   @Override
   public boolean equals(Object o) {
      if (this == o)
          return true;
      if (o == null || getClass() != o.getClass())
         return false;
      OrgLdapGroupAttributes that = OrgLdapGroupAttributes.class.cast(o);
      return equal(objectClass, that.objectClass) && 
           equal(objectIdentifier, that.objectIdentifier) && 
           equal(groupName, that.groupName) && 
           equal(membership, that.membership) && 
           equal(membershipIdentifier, that.membershipIdentifier) && 
           equal(backLinkIdentifier, that.backLinkIdentifier);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(objectClass, 
           objectIdentifier, 
           groupName, 
           membership, 
           membershipIdentifier, 
           backLinkIdentifier);
   }
   
   @Override
   public String toString() {
      return string().toString();
   }

   public ToStringHelper string() {
      return Objects.toStringHelper("")
            .add("objectClass", objectClass)
            .add("objectIdentifier", objectIdentifier)
            .add("groupName", groupName)
            .add("membership", membership)
            .add("membershipIdentifier", membershipIdentifier)
            .add("backLinkIdentifier", backLinkIdentifier);
   }

}
