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
package org.jclouds.vcloud.domain.internal;

import static com.google.common.base.Objects.equal;

import java.net.URI;

import org.jclouds.vcloud.domain.ReferenceType;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Location of a Rest resource
 * 
 * @author Adrian Cole
 * 
 */
public class ReferenceTypeImpl implements ReferenceType {
   private final String name;
   private final String type;
   private final URI href;

   public ReferenceTypeImpl(String name, String type, URI href) {
      this.name = name;
      this.type = type;
      this.href = href;
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public String getType() {
      return type;
   }

   @Override
   public URI getHref() {
      return href;
   }

   @Override
   public int compareTo(ReferenceType that) {
      return (this == that) ? 0 : getHref().compareTo(that.getHref());
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      ReferenceTypeImpl that = ReferenceTypeImpl.class.cast(o);
      return equal(this.href, that.href);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(href);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("").omitNullValues().add("href", href).add("name", name).add("type", type);
   }
}
