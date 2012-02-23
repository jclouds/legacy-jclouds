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

import java.net.URI;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;


/**
 * 
 *                 Represents the owner of this entity.
 *             
 * 
 * <p>Java class for Owner complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Owner">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}ResourceType">
 *       &lt;sequence>
 *         &lt;element name="User" type="{http://www.vmware.com/vcloud/v1.5}ReferenceType"/>
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
@XmlRootElement(name = "Owner")
@XmlType(propOrder = {"user"})
public class Owner
    extends ResourceType<Owner>

{
   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromOwner(this);
   }

   public static class Builder extends ResourceType.Builder<Owner> {
      
      private Reference user;

      /**
       * @see Owner#getUser()
       */
      public Builder user(Reference user) {
         this.user = user;
         return this;
      }


      public Owner build() {
         Owner owner = new Owner(href);
         owner.setUser(user);
         owner.setType(type);
         owner.setLinks(links);
         return owner;
      }

      
      /**
       * @see ResourceType#getHref()
       */
      @Override
      public Builder href(URI href) {
         super.href(href);
         return this;
      }

      /**
       * @see ResourceType#getType()
       */
      @Override
      public Builder type(String type) {
         super.type(type);
         return this;
      }

      /**
       * @see ResourceType#getLinks()
       */
      @Override
      public Builder links(Set<Link> links) {
         super.links(Sets.newLinkedHashSet(checkNotNull(links, "links")));
         return this;
      }

      /**
       * @see ResourceType#getLinks()
       */
      @Override
      public Builder link(Link link) {
         super.link(link);
         return this;
      }


      @Override
      public Builder fromResourceType(ResourceType<Owner> in) {
          return Builder.class.cast(super.fromResourceType(in));
      }
      public Builder fromOwner(Owner in) {
         return fromResourceType(in)
            .user(in.getUser());
      }
   }

   private Owner() {
      // For JAXB and builder use
   }
   
   private Owner(URI href) {
      super(href);
   }



    @XmlElement(name = "User", required = true)
    protected Reference user;

    /**
     * Gets the value of the user property.
     * 
     * @return
     *     possible object is
     *     {@link Reference }
     *     
     */
    public Reference getUser() {
        return user;
    }

    /**
     * Sets the value of the user property.
     * 
     * @param value
     *     allowed object is
     *     {@link Reference }
     *     
     */
    public void setUser(Reference value) {
        this.user = value;
    }

   @Override
   public boolean equals(Object o) {
      if (this == o)
          return true;
      if (o == null || getClass() != o.getClass())
         return false;
      Owner that = Owner.class.cast(o);
      return equal(user, that.user);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(user);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("")
            .add("user", user).toString();
   }

}
