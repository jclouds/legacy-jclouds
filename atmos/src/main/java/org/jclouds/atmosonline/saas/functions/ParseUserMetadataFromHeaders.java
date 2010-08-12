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

package org.jclouds.atmosonline.saas.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Set;

import javax.inject.Singleton;

import org.jclouds.atmosonline.saas.domain.UserMetadata;
import org.jclouds.atmosonline.saas.reference.AtmosStorageHeaders;
import org.jclouds.http.HttpResponse;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
@Singleton
public class ParseUserMetadataFromHeaders implements Function<HttpResponse, UserMetadata> {
   private final Set<String> sysKeys = ImmutableSet.of("atime", "ctime", "gid", "itime", "mtime",
            "nlink", "policyname", "size", "uid");

   public UserMetadata apply(HttpResponse from) {
      UserMetadata md = new UserMetadata();
      Map<String, String> metaMap = getMetaMap(checkNotNull(from
               .getFirstHeaderOrNull(AtmosStorageHeaders.META), AtmosStorageHeaders.META));

      Set<String> keys = Sets.difference(metaMap.keySet(), sysKeys);
      for (String key : keys) {
         md.getMetadata().put(key, metaMap.get(key));
      }
      if (from.getFirstHeaderOrNull(AtmosStorageHeaders.LISTABLE_META) != null)
         md.getListableMetadata().putAll(
                  getMetaMap(from.getFirstHeaderOrNull(AtmosStorageHeaders.LISTABLE_META)));

      if (from.getFirstHeaderOrNull(AtmosStorageHeaders.TAGS) != null)
         md.getTags().addAll(getTags(from.getFirstHeaderOrNull(AtmosStorageHeaders.TAGS)));
      if (from.getFirstHeaderOrNull(AtmosStorageHeaders.LISTABLE_TAGS) != null)
         md.getTags().addAll(getTags(from.getFirstHeaderOrNull(AtmosStorageHeaders.LISTABLE_TAGS)));
      return md;
   }

   private Set<String> getTags(String meta) {
      Set<String> tags = Sets.newTreeSet();
      String[] metas = meta.split(", ");
      for (String entry : metas) {
         tags.add(entry);
      }
      return tags;
   }

   private Map<String, String> getMetaMap(String meta) {
      Map<String, String> metaMap = Maps.newHashMap();
      String[] metas = meta.split(", ");
      for (String entry : metas) {
         String[] entrySplit = entry.split("=");
         metaMap.put(entrySplit[0], entrySplit[1]);
      }
      return metaMap;
   }
}