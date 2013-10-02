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
package org.jclouds.rest.suppliers;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import javax.inject.Inject;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Supplier;

public class URIFromStringSupplier implements Supplier<URI> {

   private final URI endpoint;

   @Inject
   protected URIFromStringSupplier(String uri) {
      this.endpoint = URI.create(checkNotNull(uri, "uri"));
   }

   @Override
   public URI get() {
      return endpoint;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof URIFromStringSupplier) {
         final URIFromStringSupplier other = URIFromStringSupplier.class.cast(object);
         return equal(endpoint, other.endpoint);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(endpoint);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   protected ToStringHelper string() {
      return toStringHelper("").add("endpoint", endpoint);
   }

}
