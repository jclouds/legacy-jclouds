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
package org.jclouds.tmrk.enterprisecloud.domain.software;

import org.jclouds.tmrk.enterprisecloud.domain.Action;
import org.jclouds.tmrk.enterprisecloud.domain.Link;
import org.jclouds.tmrk.enterprisecloud.domain.internal.BaseResource;
import org.jclouds.tmrk.enterprisecloud.domain.internal.Resource;

import java.net.URI;
import java.util.Map;
import java.util.Set;

/**
 * <xs:complexType name="OperatingSystem">
 * @author Jason King
 * 
 */
public class OperatingSystem extends Resource<OperatingSystem> {

   //TODO There are other fields

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Builder toBuilder() {
      return new Builder().fromOperatingSystem(this);
   }

   public static class Builder extends Resource.Builder<OperatingSystem> {

      /**
       * {@inheritDoc}
       */
      @Override
      public OperatingSystem build() {
         return new OperatingSystem(href, type, links, actions, name);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromResource(Resource<OperatingSystem> in) {
         return Builder.class.cast(super.fromResource(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromBaseResource(BaseResource<OperatingSystem> in) {
         return Builder.class.cast(super.fromBaseResource(in));
      }

      public Builder fromOperatingSystem(OperatingSystem in) {
         return fromResource(in);
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
      public Builder href(URI href) {
         return Builder.class.cast(super.href(href));
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
      public Builder fromAttributes(Map<String, String> in) {
         return Builder.class.cast(super.fromAttributes(in));
      }

   }

   private OperatingSystem(URI href, String type, Set<Link> links, Set<Action> actions, String name) {
      super(href, type, name, links, actions);
   }

   private OperatingSystem() {
      //For JAXB
   }
}