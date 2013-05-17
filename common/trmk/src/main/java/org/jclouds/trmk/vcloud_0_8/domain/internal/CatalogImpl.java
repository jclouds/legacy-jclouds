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
package org.jclouds.trmk.vcloud_0_8.domain.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.trmk.vcloud_0_8.domain.Catalog;
import org.jclouds.trmk.vcloud_0_8.domain.ReferenceType;

/**
 * Locations of resources in vCloud
 * 
 * @author Adrian Cole
 * 
 */
public class CatalogImpl extends LinkedHashMap<String, ReferenceType> implements Catalog {

   private final String name;
   private final String type;
   private final URI href;
   @Nullable
   private final String description;

   public CatalogImpl(String name, String type, URI href, @Nullable String description,
         Map<String, ReferenceType> contents) {
      this.name = checkNotNull(name, "name");
      this.type = checkNotNull(type, "type");
      this.description = description;
      this.href = checkNotNull(href, "href");
      putAll(checkNotNull(contents, "contents"));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public URI getHref() {
      return href;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getName() {
      return name;
   }

   /**
    * {@inheritDoc}
    */
   public String getDescription() {
      return description;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getType() {
      return type;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((description == null) ? 0 : description.hashCode());
      result = prime * result + ((href == null) ? 0 : href.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((type == null) ? 0 : type.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      CatalogImpl other = (CatalogImpl) obj;
      if (description == null) {
         if (other.description != null)
            return false;
      } else if (!description.equals(other.description))
         return false;
      if (href == null) {
         if (other.href != null)
            return false;
      } else if (!href.equals(other.href))
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (type == null) {
         if (other.type != null)
            return false;
      } else if (!type.equals(other.type))
         return false;
      return true;
   }

   @Override
   public int compareTo(ReferenceType o) {
      return (this == o) ? 0 : getHref().compareTo(o.getHref());
   }

}
