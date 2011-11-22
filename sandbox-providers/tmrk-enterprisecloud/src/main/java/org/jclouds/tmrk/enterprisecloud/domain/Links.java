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
package org.jclouds.tmrk.enterprisecloud.domain;

import com.google.common.collect.Sets;

import javax.xml.bind.annotation.XmlElement;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Wraps individual Link elements.
 * Needed because parsing is done with JAXB and it does not handle Generic collections
 * @author Jason King
 */
public class Links {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromLinks(this);
   }

   public static class Builder {

       private Set<Link> links = Sets.newLinkedHashSet();

       /**
        * @see Links#getLinks
        */
       public Builder links(Set<Link> links) {
          this.links = Sets.newLinkedHashSet(checkNotNull(links, "links"));
          return this;
       }

       public Builder addLink(Link link) {
          links.add(checkNotNull(link,"link"));
          return this;
       }

       public Links build() {
           return new Links(links);
       }

       public Builder fromLinks(Links in) {
         return links(in.getLinks());
       }
   }

   @XmlElement(name = "Link")
   private LinkedHashSet<Link> links = Sets.newLinkedHashSet();

   private Links() {
      //For JAXB
   }

   private Links(Set<Link> links) {
      this.links = Sets.newLinkedHashSet(links);
   }

   public Set<Link> getLinks() {
      return Collections.unmodifiableSet(links);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Links links1 = (Links) o;

      if (!links.equals(links1.links)) return false;

      return true;
   }

   @Override
   public int hashCode() {
      return links.hashCode();
   }

   public String toString() {
      return "["+ links.toString()+"]";
   }

}
