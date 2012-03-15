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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;


/**
 * 
 *                 Represents group in the system.
 *             
 * 
 * <p>Java class for Group complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Group">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}EntityType">
 *       &lt;sequence>
 *         &lt;element name="NameInSource" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="UsersList" type="{http://www.vmware.com/vcloud/v1.5}UsersListType" minOccurs="0"/>
 *         &lt;element name="Role" type="{http://www.vmware.com/vcloud/v1.5}ReferenceType" minOccurs="0"/>
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
@XmlRootElement(name = "Group")
@XmlType(propOrder = {
    "nameInSource",
    "usersList",
    "role"
})
public class Group extends EntityType {
   
   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return builder().fromGroup(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public static abstract class Builder<B extends Builder<B>> extends EntityType.Builder<B> {
      
      private String nameInSource;
      private UsersList usersList;
      private Reference role;

      /**
       * @see Group#getNameInSource()
       */
      public B nameInSource(String nameInSource) {
         this.nameInSource = nameInSource;
         return self();
      }

      /**
       * @see Group#getUsersList()
       */
      public B usersList(UsersList usersList) {
         this.usersList = usersList;
         return self();
      }

      /**
       * @see Group#getRole()
       */
      public B role(Reference role) {
         this.role = role;
         return self();
      }

      public Group build() {
         return new Group(this);
      }
      
      public B fromGroup(Group in) {
         return fromEntityType(in)
            .nameInSource(in.getNameInSource())
            .usersList(in.getUsersList())
            .role(in.getRole());
      }
   }

   @SuppressWarnings("unused")
   private Group() {
      // For JAXB
   }
   
   protected Group(Builder<?> builder) {
      super(builder);
      this.nameInSource = builder.nameInSource;
      this.usersList = builder.usersList;
      this.role = builder.role;
   }

    @XmlElement(name = "NameInSource")
    protected String nameInSource;
    @XmlElement(name = "UsersList")
    protected UsersList usersList;
    @XmlElement(name = "Role")
    protected Reference role;

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
     * Gets the value of the usersList property.
     * 
     * @return
     *     possible object is
     *     {@link UsersList }
     *     
     */
    public UsersList getUsersList() {
        return usersList;
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

   @Override
   public boolean equals(Object o) {
      if (this == o)
          return true;
      if (o == null || getClass() != o.getClass())
         return false;
      Group that = Group.class.cast(o);
      return super.equals(that) && 
           equal(nameInSource, that.nameInSource) &&
           equal(usersList, that.usersList) &&
           equal(role, that.role);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(),
           nameInSource, 
           usersList, 
           role);
   }

   @Override
   public String toString() {
      return super.string()
            .add("nameInSource", nameInSource)
            .add("usersList", usersList)
            .add("role", role).toString();
   }

}
