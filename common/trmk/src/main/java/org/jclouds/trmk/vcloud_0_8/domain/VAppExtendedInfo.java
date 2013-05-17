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
import java.util.List;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

/**
 * @author Seshu Pasam, Adrian Cole
 */
public class VAppExtendedInfo implements Comparable<VAppExtendedInfo> {
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromVAppExtendedInfo(this);
   }

   public static class Builder {
      private String id;
      private URI href;
      private String name;
      private String longName;
      private List<String> tags = Lists.newArrayList();
      private List<NetworkAdapter> networkAdapters = Lists.newArrayList();
      private ComputePoolReference computePoolReference;

      public Builder id(String id) {
         this.id = id;
         return this;
      }

      public Builder href(URI href) {
         this.href = href;
         return this;
      }

      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public Builder longName(String longName) {
         this.longName = longName;
         return this;
      }

      public Builder tags(List<String> tags) {
         this.tags = tags;
         return this;
      }

      public Builder networkAdapters(List<NetworkAdapter> networkAdapters) {
         this.networkAdapters = networkAdapters;
         return this;
      }

      public Builder networkAdapter(NetworkAdapter networkAdapter) {
         this.networkAdapters.add(networkAdapter);
         return this;
      }

      public Builder computePoolReference(ComputePoolReference computePoolReference) {
         this.computePoolReference = computePoolReference;
         return this;
      }

      public VAppExtendedInfo build() {
         return new VAppExtendedInfo(id, href, name, tags, longName, networkAdapters, computePoolReference);
      }

      public Builder fromVAppExtendedInfo(VAppExtendedInfo in) {
         return id(in.getId()).href(in.getHref()).name(in.getName()).longName(in.getLongName()).tags(in.getTags())
                  .networkAdapters(in.getNetworkAdapters()).computePoolReference(in.getComputePoolReference());
      }

   }

   private final String id;
   private final URI href;
   private final String name;
   private final String longName;
   private final List<String> tags;
   private final List<NetworkAdapter> networkAdapters;
   private final ComputePoolReference computePoolReference;

   public VAppExtendedInfo(String id, URI href, String name, List<String> tags, String longName,
            List<NetworkAdapter> networkAdapters, ComputePoolReference computePoolReference) {
      this.id = id;
      this.href = href;
      this.name = name;
      this.tags = tags;
      this.longName = longName;
      this.networkAdapters = networkAdapters;
      this.computePoolReference = computePoolReference;
   }

   public int compareTo(VAppExtendedInfo that) {
      return (this == that) ? 0 : getHref().compareTo(that.getHref());
   }

   public String getId() {
      return id;
   }

   public URI getHref() {
      return href;
   }

   public String getName() {
      return name;
   }

   public String getLongName() {
      return longName;
   }

   public List<String> getTags() {
      return tags;
   }

   public List<NetworkAdapter> getNetworkAdapters() {
      return networkAdapters;
   }

   public ComputePoolReference getComputePoolReference() {
      return computePoolReference;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      VAppExtendedInfo that = VAppExtendedInfo.class.cast(o);
      return equal(this.id, that.id) && equal(this.href, that.href) && equal(this.name, that.name)
               && equal(this.longName, that.longName) && equal(this.tags, that.tags)
               && equal(this.networkAdapters, that.networkAdapters)
               && equal(this.computePoolReference, that.computePoolReference);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, href, name, longName, tags, networkAdapters, computePoolReference);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("").add("id", id).add("href", href).add("name", name).add("longName", longName)
               .add("tags", tags).add("networkAdapters", networkAdapters).add("computePoolReference",
                        computePoolReference).toString();
   }
}
