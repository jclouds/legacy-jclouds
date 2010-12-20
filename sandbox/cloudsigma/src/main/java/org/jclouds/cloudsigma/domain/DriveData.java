/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.cloudsigma.domain;

import javax.annotation.Nullable;

/**
 * 
 * @author Adrian Cole
 */
public class DriveData extends Drive {
   public static class Builder extends Drive.Builder {

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder claimType(ClaimType claimType) {
         return Builder.class.cast(super.claimType(claimType));
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
      public Builder readers(Iterable<String> readers) {
         return Builder.class.cast(super.readers(readers));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder size(long size) {
         return Builder.class.cast(super.size(size));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder use(Iterable<String> use) {
         return Builder.class.cast(super.use(use));
      }

      public DriveData build() {
         return new DriveData(uuid, name, size, claimType, readers, use);
      }
   }

   public DriveData(@Nullable String uuid, String name, long size, @Nullable ClaimType claimType,
         Iterable<String> readers, Iterable<String> use) {
      super(uuid, name, size, claimType, readers, use);
   }
}