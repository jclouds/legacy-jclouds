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
package org.jclouds.glesys.domain;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * The allowed arguments for archive manipulation, such as archivesize
 *
 * @author Adam Lowe
 * @see <a href= "https://customer.glesys.com/api.php?a=doc#archive_allowedarguments" />
 */
public class ArchiveAllowedArguments {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private List<Integer> archiveSizes;

      public Builder archiveSizes(List<Integer> archiveSizes) {
         this.archiveSizes = archiveSizes;
         return this;
      }

      public Builder archiveSizes(Integer... archiveSizes) {
         return archiveSizes(Arrays.asList(archiveSizes));
      }

      public ArchiveAllowedArguments build() {
         return new ArchiveAllowedArguments(archiveSizes);
      }
      
      public Builder fromArchiveAllowedArguments(ArchiveAllowedArguments in) {
         return archiveSizes(in.getArchiveSizes());
      }
   }

   @SerializedName("archivesize")
   private final List<Integer> archiveSizes;

   public ArchiveAllowedArguments(List<Integer> archiveSizes) {
      checkArgument(archiveSizes != null, "archiveSizes");
      this.archiveSizes = archiveSizes;
   }

   /**
    * @return the list of allowed archive sizes, in GB
    */
   public List<Integer> getArchiveSizes() {
      return archiveSizes;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      return object instanceof ArchiveAllowedArguments
         && Objects.equal(archiveSizes, ((ArchiveAllowedArguments) object).archiveSizes);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(archiveSizes);
   }

   @Override
   public String toString() {
      Joiner commaJoiner = Joiner.on(", ");
      return String.format(
            "[archiveSizes=[%s]]", commaJoiner.join(archiveSizes));
   }

}
