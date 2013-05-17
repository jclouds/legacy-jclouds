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
package org.jclouds.trmk.vcloud_0_8.domain;

import static com.google.common.base.Objects.equal;

import java.net.URI;

import com.google.common.base.Objects;

/**
 * @author Seshu Pasam
 */
public class Subnet implements Comparable<Subnet> {
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromSubnet(this);
   }

   public static class Builder {
      private URI href;
      private String name;

      public Builder href(URI href) {
         this.href = href;
         return this;
      }

      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public Subnet build() {
         return new Subnet(href, name);
      }

      public Builder fromSubnet(Subnet in) {
         return href(in.getHref()).name(in.getName());
      }

   }

   private final URI href;
   private final String name;

   public Subnet(URI href, String name) {
      this.href = href;
      this.name = name;
   }

   public int compareTo(Subnet that) {
      return (this == that) ? 0 : getHref().compareTo(that.getHref());
   }

   public URI getHref() {
      return href;
   }

   public String getName() {
      return name;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      Subnet that = Subnet.class.cast(o);
      return equal(this.href, that.href) && equal(this.name, that.name);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(href, name);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("").add("href", href).add("name", name).toString();
   }
}
