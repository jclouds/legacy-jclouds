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

package org.jclouds.virtualbox.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;

/**
 * The information needed to create a machine from a .iso file.
 */
public class IsoSpec {

   private final String installationKeySequence;
   private final String sourcePath;

   public IsoSpec(String sourcePath, String installationKeySequence) {
      this.sourcePath = checkNotNull(sourcePath, "sourcePath can't be null");
      this.installationKeySequence = checkNotNull(installationKeySequence, "installationKeySequence can't be null");
   }

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {

      private String installationSequence;
      private String sourcePath;

      public Builder installationScript(String installationSequence) {
         this.installationSequence = installationSequence;
         return this;
      }

      public Builder sourcePath(String sourcePath) {
         this.sourcePath = sourcePath;
         return this;
      }


      public IsoSpec build() {
         return new IsoSpec(sourcePath, installationSequence);
      }
   }

   public String getInstallationKeySequence() {
      return installationKeySequence;
   }

   public String getSourcePath() {
      return sourcePath;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o instanceof VmSpec) {
         IsoSpec other = (IsoSpec) o;
         return Objects.equal(sourcePath, other.sourcePath) &&
                 Objects.equal(installationKeySequence, other.installationKeySequence);
      }
      return false;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(sourcePath, installationKeySequence);
   }

   @Override
   public String toString() {
      return "IsoSpec{" +
              "sourcePath='" + sourcePath + '\'' +
              "installationKeySequence='" + installationKeySequence + '\'' +
              '}';
   }
}
