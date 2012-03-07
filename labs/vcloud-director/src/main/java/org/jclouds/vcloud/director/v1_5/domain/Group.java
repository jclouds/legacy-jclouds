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
public class Group extends EntityType<Group> {
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromGroup(this);
   }

   public static class Builder extends EntityType.Builder<Group> {
      
      private String nameInSource;
      private UsersList usersList;
      private Reference role;

      /**
       * @see Group#getNameInSource()
       */
      public Builder nameInSource(String nameInSource) {
         this.nameInSource = nameInSource;
         return this;
      }

      /**
       * @see Group#getUsersList()
       */
      public Builder usersList(UsersList usersList) {
         this.usersList = usersList;
         return this;
      }

      /**
       * @see Group#getRole()
       */
      public Builder role(Reference role) {
         this.role = role;
         return this;
      }

      public Group build() {
         return new Group(href, type, links, description, tasksInProgress, id, name, 
               nameInSource, usersList, role);
      }
      
      /**
       * @see EntityType#getName()
       */
      @Override
      public Builder name(String name) {
         super.name(name);
         return this;
      }
      
      /**
       * @see EntityType#getDescription()
       */
      @Override
      public Builder description(String idname) {
         super.description(name);
         return this;
      }

      /**
       * @see EntityType#getId()
       */
      @Override
      public Builder id(String id) {
         super.id(id);
         return this;
      }

      /**
       * @see EntityType#getTasksInProgress()
       */
      @Override
      public Builder tasksInProgress(TasksInProgress tasksInProgress) {
         super.tasksInProgress(tasksInProgress);
         return this;
      }

      /**
       * @see ReferenceType#getHref()
       */
      @Override
      public Builder href(URI href) {
         super.href(href);
         return this;
      }

      /**
       * @see ReferenceType#getType()
       */
      @Override
      public Builder type(String type) {
         super.type(type);
         return this;
      }

      /**
       * @see ReferenceType#getLinks()
       */
      @Override
      public Builder links(Set<Link> links) {
         super.links(links);
         return this;
      }

      /**
       * @see ReferenceType#getLinks()
       */
      @Override
      public Builder link(Link link) {
         super.link(link);
         return this;
      }

      @Override
      public Builder fromEntityType(EntityType<Group> in) {
          return Builder.class.cast(super.fromEntityType(in));
      }
      public Builder fromGroup(Group in) {
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
   
   public Group(URI href, String type, Set<Link> links, String description, 
         TasksInProgress tasksInProgress, String id, String name, String nameInSource, 
         UsersList usersList, Reference role) {
      super(href, type, links, description, tasksInProgress, id, name);
      this.nameInSource = nameInSource;
      this.usersList = usersList;
      this.role = role;
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
