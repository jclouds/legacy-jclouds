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
package org.jclouds.cloudsigma.options;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.HashSet;
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
   private final Map<String, String> options = Maps.newLinkedHashMap();

   /**
    * adjust to new size in bytes
    */
   public CloneDriveOptions size(long size) {
      checkArgument(size >= 0, "size must be >= 0");
      options.put("size", size + "");
      return this;
   }

   /**
    * specifies whether the new drive has 'HDD' affinity (the default) or 'SSD' (for solid-state drives)
    */
   public CloneDriveOptions affinity(AffinityType affinity) {
      final String SSD_AFFINITY_TAG = "affinity:ssd";
      
      // Affinity is conveyed using regular tags; make sure to avoid multiple affinity tags in the options.
      String currentTagsString = options.remove("tags");
      Set<String> tags = (currentTagsString == null) ? new HashSet<String>() :
         Sets.newHashSet(Splitter.on(' ').split(currentTagsString));
      
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
