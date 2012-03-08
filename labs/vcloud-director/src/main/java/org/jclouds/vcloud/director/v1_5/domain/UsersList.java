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
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;


/**
 * 
 *                 Container for ReferenceType elements that reference users.
 *             
 * 
 * <p>Java class for UsersList complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UsersList">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}VCloudExtensibleType">
 *       &lt;sequence>
 *         &lt;element name="UserReference" type="{http://www.vmware.com/vcloud/v1.5}ReferenceType" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlRootElement(name = "UsersList")
@XmlType(propOrder = {
    "userReference"
})
public class UsersList {
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromUsersList(this);
   }

   public static class Builder {
      private List<Reference> users;

      /**
       * @see UsersList#getUsers()
       */
      public Builder users(List<Reference> users) {
         this.users = ImmutableList.copyOf(users);
         return this;
      }
      
      /**
       * @see UsersList#getUsers()
       */
      public Builder user(Reference user) {
         users.add(checkNotNull(user, "user"));
         return this;
      }

      public UsersList build() {
         return new UsersList(users);
      }

      public Builder fromUsersList(UsersList in) {
         return users(in.getUsers());
      }
   }

   private UsersList() {
      // For JAXB and builder use
   }

   private UsersList(List<Reference> users) {
      this.users = users;
   }

    @XmlElement(name = "UserReference")
    protected List<Reference> users;

    /**
     * Gets the value of the userReference property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the userReference property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUserReference().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ReferenceType }
     * 
     * 
     */
    public List<Reference> getUsers() {
        if (users == null) {
            users = new ArrayList<Reference>();
        }
        return this.users;
    }

   @Override
   public boolean equals(Object o) {
      if (this == o)
          return true;
      if (o == null || getClass() != o.getClass())
         return false;
      UsersList that = UsersList.class.cast(o);
      return equal(users, that.users);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(users);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("")
            .add("userReference", users).toString();
   }

}
