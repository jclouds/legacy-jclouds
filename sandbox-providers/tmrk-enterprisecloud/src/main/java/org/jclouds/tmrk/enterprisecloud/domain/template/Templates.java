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
package org.jclouds.tmrk.enterprisecloud.domain.template;

import com.google.common.collect.Sets;
import org.jclouds.tmrk.enterprisecloud.domain.Action;
import org.jclouds.tmrk.enterprisecloud.domain.Link;
import org.jclouds.tmrk.enterprisecloud.domain.internal.BaseResource;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Templates is more than a simple wrapper as it extends BaseResource.
 * <xs:complexType name="Templates">
 * @author Jason King
 * 
 */
@XmlRootElement(name = "Templates")
public class Templates extends BaseResource<Templates> {


   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Builder toBuilder() {
      return new Builder().fromTemplates(this);
   }

   public static class Builder extends BaseResource.Builder<Templates> {
      private Set<TemplateFamily> families = Sets.newLinkedHashSet();

      /**
       * @see Templates#getTemplateFamilies
       */
      public Builder families(Set<TemplateFamily> families) {
         this.families =(checkNotNull(families,"families"));
         return this;
      }

      @Override
      public Templates build() {
         return new Templates(href, type, links, actions, families);
      }

      public Builder fromTemplates(Templates in) {
         return fromResource(in).families(in.getTemplateFamilies());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromResource(BaseResource<Templates> in) {
         return Builder.class.cast(super.fromResource(in));
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
      public Builder links(Set<Link> links) {
         return Builder.class.cast(super.links(links));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder actions(Set<Action> actions) {
         return Builder.class.cast(super.actions(actions));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromAttributes(Map<String, String> attributes) {
         return Builder.class.cast(super.fromAttributes(attributes));
      }

   }

   @XmlElement(name = "Families", required = false)
   private TemplateFamilies families;

   private Templates(URI href, String type, Set<Link> links, Set<Action> actions, Set<TemplateFamily> families) {
      super(href, type, links, actions);
      this.families = TemplateFamilies.builder().families(families).build();
   }

   private Templates() {
       //For JAXB
   }

   public Set<Link> getLinks() {
       return Collections.unmodifiableSet(links.getLinks());
   }

   public Set<TemplateFamily> getTemplateFamilies() {
      return families.getTemplateFamilies();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;

      Templates templates = (Templates) o;

      if (families != null ? !families.equals(templates.families) : templates.families != null)
         return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + (families != null ? families.hashCode() : 0);
      return result;
   }

   @Override
   public String string() {
      return super.string()+", families="+families;
   }
}