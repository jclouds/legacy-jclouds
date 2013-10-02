/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.elasticstack.domain;

import java.util.Map;

import org.jclouds.javax.annotation.Nullable;


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
      public Builder tags(Iterable<String> tags) {
         return Builder.class.cast(super.tags(tags));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder userMetadata(Map<String, String> userMetadata) {
         return Builder.class.cast(super.userMetadata(userMetadata));
      }

      public DriveData build() {
         return new DriveData(uuid, name, size, claimType, readers, tags, userMetadata);
      }
   }

   public DriveData(@Nullable String uuid, String name, long size, @Nullable ClaimType claimType, Iterable<String> readers,
         Iterable<String> tags, Map<String, String> userMetadata) {
      super(uuid, name, size, claimType, readers, tags, userMetadata);
   }
}
