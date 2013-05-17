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
package org.jclouds.ultradns.ws;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

import com.google.common.base.Objects;
import com.google.common.base.Optional;

/**
 * @author Adrian Cole
 */
public final class UltraDNSWSError {
   public static UltraDNSWSError fromCodeAndDescription(int code, Optional<String> description) {
      return new UltraDNSWSError(code, description);
   }

   private final int code;
   private final Optional<String> description;

   private UltraDNSWSError(int code, Optional<String> description) {
      this.code = code;
      this.description = checkNotNull(description, "description for code %s", code);
   }

   /**
    * The error code. ex {@code 1801}
    */
   public int getCode() {
      return code;
   }

   /**
    * The description of the error. ex {@code Zone does not exist in the system.}
    */
   public Optional<String> getDescription() {
      return description;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(code, description);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      UltraDNSWSError that = UltraDNSWSError.class.cast(obj);
      return equal(this.code, that.code) && equal(this.description, that.description);
   }

   @Override
   public String toString() {
      return description.isPresent() ? format("Error %s: %s", code, description.get()) : format("Error %s", code);
   }
}
