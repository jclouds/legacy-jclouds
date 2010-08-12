/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.atmosonline.saas.domain;

import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * User metadata
 * 
 * @author Adrian Cole
 */
public class UserMetadata {
   private final SortedMap<String, String> metadata = Maps.newTreeMap();
   private final SortedMap<String, String> listableMetadata = Maps.newTreeMap();
   private final SortedSet<String> tags = Sets.newTreeSet();
   private final SortedSet<String> listableTags = Sets.newTreeSet();

   public Map<String, String> getMetadata() {
      return metadata;
   }

   public Map<String, String> getListableMetadata() {
      return listableMetadata;
   }

   public Set<String> getTags() {
      return tags;
   }

   public Set<String> getListableTags() {
      return listableTags;
   }

}