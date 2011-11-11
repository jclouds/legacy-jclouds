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
package org.jclouds.openstack.nova.domain;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import com.google.common.base.Functions;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import sun.awt.image.ImageWatched;

import javax.annotation.Nullable;

/**
 * @author Dmitri Babaev
 * @author Matt Stephenson
 */
public class Resource {

   private List<Map<String, String>> links = Lists.newArrayList();

   //This is the preference order for returning a URI in getURI
   private enum LinkType {
       BOOKMARK_JSON(new Predicate<Map<String, String>>() {
           @Override
           public boolean apply(@Nullable Map<String, String> linkMap) {
               return Functions.forMap(linkMap, "").apply("rel").equals("bookmark") &&
                      Functions.forMap(linkMap, "").apply("type").contains("json");
           }
       }),
       BOOKMARK_ANY(new Predicate<Map<String, String>>() {
           @Override
           public boolean apply(@Nullable Map<String, String> linkMap) {
               return Functions.forMap(linkMap, "").apply("rel").equals("bookmark");
           }
       }),
       SELF(new Predicate<Map<String, String>>() {
           @Override
           public boolean apply(@Nullable Map<String, String> linkMap) {
               return Functions.forMap(linkMap, "").apply("rel").equals("self");
           }
       });

       Predicate<Map<String,String>> linkPredicate;

       LinkType(Predicate<Map<String,String>> linkPredicate) {
           this.linkPredicate = linkPredicate;
       };
   }

   private final ConcurrentSkipListMap<LinkType,URI> orderedSelfReferences = new ConcurrentSkipListMap<LinkType,URI>();

   private void populateOrderedSelfReferences() {
      for (Map<String, String> linkProperties : links) {
         for (LinkType type : LinkType.values()) {
            if(type.linkPredicate.apply(linkProperties)) {
               try {
                  orderedSelfReferences.put(type, new URI(linkProperties.get("href")));
               } catch (URISyntaxException e) {
                  throw new RuntimeException(e);
               }
            }
         }
      }
      if(orderedSelfReferences.isEmpty())
         throw new IllegalStateException("URI is not available");
   }

   public URI getURI() {
      if(orderedSelfReferences.isEmpty())
         populateOrderedSelfReferences();

      return orderedSelfReferences.firstEntry().getValue();
   }

   public URI getSelfURI() {
      if(orderedSelfReferences.isEmpty())
         populateOrderedSelfReferences();

      return orderedSelfReferences.get(LinkType.SELF);
   }
}
