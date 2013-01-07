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

package org.jclouds.googlecompute.domain;

import com.google.common.annotations.Beta;

import java.beans.ConstructorProperties;
import java.net.URI;
import java.util.Date;

import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Represents a kernel.
 *
 * @author David Alves
 * @see <a href="https://developers.google.com/compute/docs/reference/v1beta13/kernels"/>
 */
@Beta
public final class Kernel extends Resource {

   @ConstructorProperties({
           "id", "creationTimestamp", "selfLink", "name", "description"
   })
   private Kernel(String id, Date creationTimestamp, URI selfLink, String name, String description) {
      super(Kind.KERNEL, checkNotNull(id, "id of %s", name), fromNullable(creationTimestamp),
              checkNotNull(selfLink, "selfLink of %s", name), checkNotNull(name, "name"),
              fromNullable(description));
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromKernel(this);
   }

   public static final class Builder extends Resource.Builder<Builder> {

      @Override
      protected Builder self() {
         return this;
      }

      public Kernel build() {
         return new Kernel(super.id, super.creationTimestamp, super.selfLink, super.name,
                 super.description);
      }

      public Builder fromKernel(Kernel in) {
         return super.fromResource(in);
      }
   }

}
