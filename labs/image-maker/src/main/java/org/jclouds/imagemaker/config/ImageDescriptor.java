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

package org.jclouds.imagemaker.config;

import java.util.List;
import java.util.Map;

import org.jclouds.imagemaker.PackageProcessor.Type;

import com.google.common.collect.ImmutableList;

/**
 * Loaded from YAML describes what each package processor should do.
 * 
 * @author David Alves
 * 
 */
public class ImageDescriptor {

   public String id;
   public Map<String, List<String>> cached_packages;
   public Map<String, List<String>> installed_packages;

   public List<String> getPackagesFor(String system, Type type) {
      System.out.println(system);
      System.out.println(type);
      System.out.println("cached: "+cached_packages);
      System.out.println("installed: "+installed_packages);
      switch (type) {
         case CACHER:
            return cached_packages.get(system) != null ? cached_packages.get(system) : ImmutableList.<String> of();
         case INSTALLER:
            return installed_packages.get(system) != null ? installed_packages.get(system) : ImmutableList
                     .<String> of();
         default:
            throw new UnsupportedOperationException("unknown packageprocessor type");
      }
   }

}
