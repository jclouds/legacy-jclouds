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

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.Sets;

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
public class Org extends EntityType<Org> {

   public static final String MEDIA_TYPE = VCloudDirectorMediaType.ORG;

   public static NewBuilder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public NewBuilder<?> toNewBuilder() {
      return new ConcreteBuilder().fromOrg(this);
   }
   
   public static abstract class NewBuilder<T extends NewBuilder<T>> extends EntityType.NewBuilder<T> {
      
      protected String fullName;
      protected Boolean isEnabled;

      /**
       * @see Org#getFullName()
       */
      public T fullName(String fullName) {
         this.fullName = fullName;
         return self();
      }

      /**
       * @see Org#isEnabled()
       */
      public T isEnabled(Boolean isEnabled) {
         this.isEnabled = isEnabled;
         return self();
      }
      
      @Override
      public Org build() {
         return new Org(href, type, links, description, tasks, id, name, fullName, isEnabled);
      }
      
      public T fromOrg(Org in) {
         return fromEntityType(in).fullName(in.getFullName());
      }
   }
   
   private static class ConcreteBuilder extends NewBuilder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }
   
   public static Builder oldBuilder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return new Builder().fromOrg(this);
   }

   public static class Builder extends EntityType.Builder<Org> {

      private String fullName;
      private Boolean isEnabled;

      /**
       * @see Org#getFullName()
       */
      public Builder fullName(String fullName) {
         this.fullName = fullName;
         return this;
      }

      /**
       * @see Org#isEnabled()
       */
      public Builder isEnabled(Boolean isEnabled) {
         this.isEnabled = isEnabled;
         return this;
      }

      /**
       * @see Org#isEnabled()
       */
      public Builder enabled() {
         this.isEnabled = Boolean.TRUE;
         return this;
      }

      /**
       * @see Org#isEnabled()
       */
      public Builder disabled() {
         this.isEnabled = Boolean.FALSE;
         return this;
      }

      @Override
      public Org build() {
         return new Org(href, type, links, description, tasks, id, name, fullName, isEnabled);
      }

      /**
       * @see EntityType#getName()
       */
      @Override
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @see EntityType#getDescription()
       */
      @Override
      public Builder description(String description) {
         this.description = description;
         return this;
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
       * @see EntityType#getTasks()
       */
      @Override
      public Builder tasks(Set<Task> tasks) {
         if (checkNotNull(tasks, "tasks").size() > 0)
            this.tasks = Sets.newLinkedHashSet(tasks);
         return this;
      }

      /**
       * @see EntityType#getTasks()
       */
      @Override
      public Builder task(Task task) {
         if (tasks == null)
            tasks = Sets.newLinkedHashSet();
         this.tasks.add(checkNotNull(task, "task"));
         return this;
      }

      /**
       * @see ResourceType#getHref()
       */
      @Override
      public Builder href(URI href) {
         this.href = href;
         return this;
      }

      /**
       * @see ResourceType#getType()
       */
      @Override
      public Builder type(String type) {
         this.type = type;
         return this;
      }

      /**
       * @see ResourceType#getLinks()
       */
      @Override
      public Builder links(Set<Link> links) {
         if (checkNotNull(links, "links").size() > 0)
            this.links = Sets.newLinkedHashSet(links);
         return this;
      }

      /**
       * @see ResourceType#getLinks()
       */
      @Override
      public Builder link(Link link) {
         if (links == null)
            links = Sets.newLinkedHashSet();
         this.links.add(checkNotNull(link, "link"));
         return this;
      }

      @Override
      public Builder fromEntityType(EntityType<Org> in) {
         return Builder.class.cast(super.fromEntityType(in));
      }

      public Builder fromOrg(Org in) {
         return fromEntityType(in).fullName(in.getFullName());
      }
   }

   protected Org() {
      // for JAXB
   }

   public Org(URI href, String type, @Nullable Set<Link> links, String description, 
         @Nullable Set<Task> tasks, String id, String name, 
         String fullName, Boolean enabled) {
      super(href, type, links, description, tasks, id, name);
      this.fullName = fullName;
      this.isEnabled = enabled;
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
