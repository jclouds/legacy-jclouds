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
package org.jclouds.vcloud.director.v1_5.domain.org;

import static com.google.common.base.Objects.equal;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.Entity;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Represents an organization.
 *
 * Unit of multi-tenancy and a top-level container. Contain vDCs, TasksList, Catalogs and Shared Network entities.
 *
 * <pre>
 * &lt;xs:complexType name="OrgType"&gt;
 * </pre>
 *
 * @author Adrian Cole
 */
@XmlRootElement(name = "Org")
@XmlSeeAlso({ AdminOrg.class })
public class Org extends Entity {

   public static final String MEDIA_TYPE = VCloudDirectorMediaType.ORG;

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return builder().fromOrg(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public abstract static class Builder<B extends Builder<B>> extends Entity.Builder<B> {
      
      private String fullName;
      private Boolean isEnabled;

      /**
       * @see Org#getFullName()
       */
      public B fullName(String fullName) {
         this.fullName = fullName;
         return self();
      }

      /**
       * @see Org#isEnabled()
       */
      public B isEnabled(Boolean isEnabled) {
         this.isEnabled = isEnabled;
         return self();
      }
      
      @Override
      public Org build() {
         return new Org(this);
      }
      
      public B fromOrg(Org in) {
         return fromEntityType(in).fullName(in.getFullName()).isEnabled(in.isEnabled());
      }
   }
   
   protected Org() {
      // for JAXB
   }

   public Org(Builder<?> builder) {
      super(builder);
      this.fullName = builder.fullName;
      this.isEnabled = builder.isEnabled;
   }

   @XmlElement(name = "FullName", required = true)
   private String fullName;
   @XmlElement(name = "IsEnabled")
   private Boolean isEnabled;

   /**
    * Full name of the organization.
    */
   public String getFullName() {
      return fullName;
   }

   /**
    * Is the organization enabled.
    */
   public Boolean isEnabled() {
      return isEnabled;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      Org that = Org.class.cast(o);
      return super.equals(that) && equal(fullName, that.fullName) && equal(this.isEnabled, that.isEnabled);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), fullName, isEnabled);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("fullName", fullName).add("isEnabled", isEnabled);
   }
}
