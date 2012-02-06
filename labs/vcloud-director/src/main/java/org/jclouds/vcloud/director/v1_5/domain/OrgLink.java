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

import java.net.URI;

public class OrgLink extends BaseNamedResource<OrgLink> {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Builder toBuilder() {
      return Builder.class.cast(new Builder().fromNamedResource(this));
   }

   public static class Builder extends BaseNamedResource.Builder<OrgLink> {

      @Override
      public OrgLink build() {
         return new OrgLink(href, type, name);
      }

      @Override
      public Builder name(String name) {
         return Builder.class.cast(super.name(name));
      }

      @Override
      public Builder href(URI href) {
         return Builder.class.cast(super.href(href));
      }

      @Override
      public Builder type(String type) {
         return Builder.class.cast(super.type(type));
      }

   }

   private OrgLink(URI href, String type, String name) {
      super(href, type, name);
   }

   private OrgLink() {
      // for JAXB
   }
}
