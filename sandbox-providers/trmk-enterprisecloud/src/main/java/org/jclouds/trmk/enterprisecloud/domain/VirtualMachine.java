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
package org.jclouds.trmk.enterprisecloud.domain;

import org.jclouds.trmk.enterprisecloud.domain.internal.BaseNamedResource;
import org.jclouds.trmk.enterprisecloud.domain.internal.BaseResource;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 
 * @author Jason King
 * 
 */
public class VirtualMachine extends BaseNamedResource<VirtualMachine> {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Builder toBuilder() {
      return new Builder().fromVirtualMachine(this);
   }

   public static class Builder extends BaseNamedResource.Builder<VirtualMachine> {
      private List<Link> links;
      private List<Action> actions;
      private List<Task> tasks;
      private String description;

      /**
       * @see org.jclouds.trmk.enterprisecloud.domain.VirtualMachine#getLinks
       */
      public Builder links(List<Link> links) {
         this.links = links;
         return this;
      }

       /**
        * @see org.jclouds.trmk.enterprisecloud.domain.VirtualMachine#getActions
        */
       public Builder actions(List<Action> actions) {
          this.actions = actions;
          return this;
       }

       /**
        * @see org.jclouds.trmk.enterprisecloud.domain.VirtualMachine#getTasks
        */
       public Builder tasks(List<Task> tasks) {
          this.tasks = tasks;
          return this;
       }


       /**
        * @see org.jclouds.trmk.enterprisecloud.domain.VirtualMachine#getDescription
        */
       public Builder description(String description) {
          this.description = description;
          return this;
       }

      @Override
      public VirtualMachine build() {
         return new VirtualMachine(href, type, name, tasks, actions, links, description);
      }

      public Builder fromVirtualMachine(VirtualMachine in) {
        return fromNamedResource(in)
            .links(in.getLinks())
            .tasks(in.getTasks())
            .actions(in.getActions())
            .description(in.getDescription());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromResource(BaseResource<VirtualMachine> in) {
         return Builder.class.cast(super.fromResource(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromNamedResource(BaseNamedResource<VirtualMachine> in) {
         return Builder.class.cast(super.fromNamedResource(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder type(String type) {
         return Builder.class.cast(super.type(type));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder href(URI href) {
         return Builder.class.cast(super.href(href));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder name(String name) {
         return Builder.class.cast(super.name(name));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromAttributes(Map<String, String> attributes) {
         return Builder.class.cast(super.fromAttributes(attributes));
          // TODO Other fields?
      }

   }

   private final List<Link> links;
   private final List<Task> tasks;
   private final List<Action> actions;
   private final String description;

   public VirtualMachine(URI href, String type, String name, List<Task> tasks, List<Action> actions, List<Link> links, String description) {
      super(href, type, name);
      this.description = checkNotNull(description, "description");
      this.links = checkNotNull(links, "links");
      this.tasks = checkNotNull(tasks, "tasks");
      this.actions = checkNotNull(actions, "actions");
   }

   public List<Link> getLinks() {
       return links;
   }

    /**
     * refers to tasks regarding the virtual machine.
     * Only the most recent tasks, up to twenty, are returned.
     * Use the href to retrieve the complete list of tasks.
     * @return most recent tasks
     */
   public List<Task> getTasks() {
       return tasks;
   }

   public List<Action> getActions() {
       return actions;
   }

   public String getDescription() {
       return description;
   }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        VirtualMachine that = (VirtualMachine) o;

        if (!actions.equals(that.actions)) return false;
        if (!description.equals(that.description)) return false;
        if (!links.equals(that.links)) return false;
        if (!tasks.equals(that.tasks)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + links.hashCode();
        result = 31 * result + tasks.hashCode();
        result = 31 * result + actions.hashCode();
        result = 31 * result + description.hashCode();
        return result;
    }

   @Override
   public String string() {
      return super.string()+", links="+links+", tasks="+tasks+", actions="+actions+", description="+description;
   }

}