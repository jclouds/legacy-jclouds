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
package org.jclouds.openstack.v2_0.predicates;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import org.jclouds.openstack.v2_0.domain.Link;
import org.jclouds.openstack.v2_0.domain.Link.Relation;

import com.google.common.base.Predicate;

/**
 * Predicates handy when working with Link Types
 * 
 * @author Adrian Cole
 */

public class LinkPredicates {
   /**
    * matches links of the given relation
    * 
    * @param rel relation of the link
    * @return predicate that will match links of the given rel
    */
   public static Predicate<Link> relationEquals(final Relation rel) {
      checkNotNull(rel, "rel must be defined");

      return new Predicate<Link>() {
         @Override
         public boolean apply(Link link) {
            return rel.equals(link.getRelation());
         }

         @Override
         public String toString() {
            return "relEquals(" + rel + ")";
         }
      };
   }

   /**
    * matches links of the given href
    * 
    * @param href
    * @return predicate that will match links of the given href
    */
   public static Predicate<Link> hrefEquals(final URI href) {
      checkNotNull(href, "href must be defined");

      return new Predicate<Link>() {
         @Override
         public boolean apply(Link link) {
            return href.equals(link.getHref());
         }

         @Override
         public String toString() {
            return "hrefEquals(" + href + ")";
         }
      };
   }

   /**
    * matches links of the given type
    * 
    * @param type
    *           ex. application/pdf
    * @return predicate that will match links of the given type
    */
   public static Predicate<Link> typeEquals(final String type) {
      checkNotNull(type, "type must be defined");

      return new Predicate<Link>() {
         @Override
         public boolean apply(Link link) {
            return type.equals(link.getType());
         }

         @Override
         public String toString() {
            return "typeEquals(" + type + ")";
         }
      };
   }
}
