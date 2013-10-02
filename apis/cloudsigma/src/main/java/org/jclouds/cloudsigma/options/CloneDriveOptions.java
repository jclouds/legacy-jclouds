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
package org.jclouds.cloudsigma.options;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.jclouds.cloudsigma.domain.AffinityType;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Contains options supported for clone drive operations. <h2>
 * Usage</h2> The recommended way to instantiate a CloneDriveOptions object is to statically import
 * CloneDriveOptions.Builder.* and invoke a static creation method followed by an instance mutator
 * (if needed):
 * <p/>
 * <code>
 * import static org.jclouds.cloudsigma.options.CloneDriveOptions.Builder.*;
 * 
 * 
 * Payload payload = client.cloneDrive("drive-uuid","newName", size(2*1024*1024l));
 * <code>
 * 
 * @author Adrian Cole
 * 
 */
public class CloneDriveOptions {
   private static final String SSD_AFFINITY_TAG = "affinity:ssd";
   private final Map<String, String> options = Maps.newLinkedHashMap();

   /**
    * adjust to new size in bytes
    */
   public CloneDriveOptions size(long size) {
      checkArgument(size >= 0, "size must be >= 0");
      options.put("size", size + "");
      return this;
   }

   public CloneDriveOptions tags(String... tags) {
      // Affinity is conveyed using regular tags; make sure to preserve any already-set affinity tag.
      String currentTagsString = options.remove("tags");
      Set<String> currentTags = (currentTagsString == null) ? new HashSet<String>() :
         Sets.newLinkedHashSet(Splitter.on(' ').split(currentTagsString));

      Set<String> newTags = Sets.newLinkedHashSet();
      for (String tag : tags)
          newTags.add(tag);
      
      if (currentTags.contains(SSD_AFFINITY_TAG))
          newTags.add(SSD_AFFINITY_TAG);
      
      options.put("tags", Joiner.on(' ').join(newTags));
      return this;
   }
   
   /**
    * Specifies whether the new drive has 'HDD' affinity (the default) or 'SSD' (for solid-state drives).
    * Affinity is conveyed via a special value among the drive's tags. 
    */
   public CloneDriveOptions affinity(AffinityType affinity) {
      // Affinity is conveyed using regular tags; make sure to avoid multiple affinity tags in the options.
      String currentTagsString = options.remove("tags");
      Set<String> tags = (currentTagsString == null) ? new LinkedHashSet<String>() :
         Sets.newLinkedHashSet(Splitter.on(' ').split(currentTagsString));
      
      switch (affinity) {
      // SSD affinity is conveyed as a special tag: "affinity:ssd".
      case SSD:
         tags.add(SSD_AFFINITY_TAG);
         break;
      
      // HDD affinity (the default) is conveyed by the *absence* of the "affinity:ssd" tag.
      case HDD:
         tags.remove(SSD_AFFINITY_TAG);
         break;
      }
      
      if (!tags.isEmpty())
          options.put("tags", Joiner.on(' ').join(tags));
      
      return this;
   }
   
   public static class Builder {

      /**
       * @see CloneDriveOptions#size
       */
      public static CloneDriveOptions size(long size) {
         CloneDriveOptions options = new CloneDriveOptions();
         return options.size(size);
      }
      
      /**
       * @see CloneDriveOptions#tags
       */
      public static CloneDriveOptions tags(String... tags) {
          CloneDriveOptions options = new CloneDriveOptions();
          return options.tags(tags);
      }

      /**
       * @see CloneDriveOptions#affinity
       */
      public static CloneDriveOptions affinity(AffinityType affinity) {
          CloneDriveOptions options = new CloneDriveOptions();
          return options.affinity(affinity);
      }
      
   }

   public Map<String, String> getOptions() {
      return ImmutableMap.copyOf(options);
   }
}
