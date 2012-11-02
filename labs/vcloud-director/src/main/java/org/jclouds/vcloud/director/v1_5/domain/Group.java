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

import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * Represents group in the system.
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
 */
@XmlRootElement(name = "Group")
@XmlType(propOrder = {
    "nameInSource",
    "usersList",
    "role"
})
public class Group extends Entity {
   
   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return builder().fromGroup(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public abstract static class Builder<B extends Builder<B>> extends Entity.Builder<B> {
      
      private String nameInSource;
      private Set<Reference> users = Sets.newLinkedHashSet();
      private Reference role;

      /**
       * @see Group#getNameInSource()
       */
      public B nameInSource(String nameInSource) {
         this.nameInSource = nameInSource;
         return self();
      }

      /**
       * @see Group#getUsers()
       */
      public B users(Iterable<Reference> users) {
         this.users = Sets.newLinkedHashSet(checkNotNull(users, "users"));
         return self();
      }
      
      /**
       * @see Group#getUsers()
       */
      public B user(Reference user) {
         users.add(checkNotNull(user, "user"));
         return self();
      }

      /**
       * @see Group#getRole()
       */
      public B role(Reference role) {
         this.role = role;
         return self();
      }

      @Override
      public Group build() {
         return new Group(this);
      }
      
      public B fromGroup(Group in) {
         return fromEntityType(in)
            .nameInSource(in.getNameInSource())
            .users(in.getUsersList())
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
      this.usersList = builder.users == null ? Sets.<Reference>newLinkedHashSet() : ImmutableSet.copyOf(builder.users);
      this.role = builder.role;
   }

    @XmlElement(name = "NameInSource")
    protected String nameInSource;
    @XmlElementWrapper(name = "UsersList")
    @XmlElement(name = "UserReference")
    protected Set<Reference> usersList = Sets.newLinkedHashSet();
    @XmlElement(name = "Role")
    protected Reference role;

    /**
     * Gets the value of the nameInSource property.
     */
    public String getNameInSource() {
        return nameInSource;
    }

    /**
     * Gets the value of the users property.
     */
    public Set<Reference> getUsersList() {
        return usersList;
    }

    /**
     * Gets the value of the role property.
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
