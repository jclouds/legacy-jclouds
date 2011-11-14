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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 
 * @author Jason King
 * 
 */
@XmlRootElement(name = "VirtualMachine")
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
      private Links links;
      private Actions actions;
      private Tasks tasks;
      private String description;
      private Layout layout;

      /**
       * @see VirtualMachine#getLinks
       */
      public Builder links(Set<Link> links) {
         this.links = new Links();
         for(Link link:links) this.links.setLink(link);
         return this;
      }

       /**
        * @see VirtualMachine#getActions
        */
       public Builder actions(Set<Action> actions) {
          this.actions = new Actions();
          for(Action action:actions) this.actions.setAction(action);
          return this;
       }

       /**
        * @see VirtualMachine#getTasks
        */
       public Builder tasks(Set<Task> tasks) {
          this.tasks = new Tasks();
          for(Task task: tasks) this.tasks.setTask(task);
          return this;
       }


       /**
        * @see VirtualMachine#getDescription
        */
       public Builder description(String description) {
          this.description = description;
          return this;
       }

       /**
        * @see VirtualMachine#getLayout()
        */
       public Builder description(Layout layout) {
          this.layout = layout;
          return this;
       }

      @Override
      public VirtualMachine build() {
         return new VirtualMachine(href, type, name, tasks, actions, links, description, layout);
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

   @XmlElement(name = "Links", required = true)
   private Links links;

   @XmlElement(name = "Tasks", required = true)
   private Tasks tasks;

   @XmlElement(name = "Actions", required = true)
   private Actions actions;

   @XmlElement(name = "Description", required = true)
   private String description;

   @XmlElement(name = "Layout", required = false)
   private Layout layout;

   public VirtualMachine(URI href, String type, String name, Tasks tasks, Actions actions, Links links, String description, Layout layout) {
      super(href, type, name);
      this.description = checkNotNull(description, "description");
      this.links = checkNotNull(links, "links");
      this.tasks = checkNotNull(tasks, "tasks");
      this.actions = checkNotNull(actions, "actions");
      this.layout = layout;
   }

   protected VirtualMachine() {
        //For JAXB
   }


   public Set<Link> getLinks() {
       return Collections.unmodifiableSet(links.getLinks());
   }

    /**
     * refers to tasks regarding the virtual machine.
     * Only the most recent tasks, up to twenty, are returned.
     * Use the href to retrieve the complete list of tasks.
     * @return most recent tasks
     */
   public Set<Task> getTasks() {
       return Collections.unmodifiableSet(tasks.getTasks());
   }

   public Set<Action> getActions() {
       return Collections.unmodifiableSet(actions.getActions());
   }

   public String getDescription() {
       return description;
   }

   public Layout getLayout() {
       return layout;
   }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        VirtualMachine that = (VirtualMachine) o;

        if (!actions.equals(that.actions)) return false;
        if (!description.equals(that.description)) return false;
        if (layout != null ? !layout.equals(that.layout) : that.layout != null)
            return false;
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
        result = 31 * result + (layout != null ? layout.hashCode() : 0);
        return result;
    }

    @Override
   public String string() {
      return super.string()+", links="+links+", tasks="+tasks+", actions="+actions+", description="+description+", layout="+layout;
   }

}