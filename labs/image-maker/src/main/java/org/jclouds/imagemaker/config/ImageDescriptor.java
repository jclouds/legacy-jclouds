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

import static com.google.common.base.Preconditions.checkState;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.jclouds.imagemaker.PackageProcessor.Type;
import org.yaml.snakeyaml.Loader;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import com.google.common.base.Charsets;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;

public class ImageDescriptor {

   public String id;
   public Map<String, List<String>> cached_packages;
   public Map<String, List<String>> installed_packages;

   public List<String> getPackagesFor(String system, Type type) {
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

   public static class ImageDescriptorLoader implements Supplier<List<ImageDescriptor>> {

      public List<ImageDescriptor> images;

      public List<ImageDescriptor> get() {

         Constructor constructor = new Constructor(ImageDescriptorLoader.class);

         TypeDescription packagesDesc = new TypeDescription(ImageDescriptor.class);
         packagesDesc.putMapPropertyType("cached_packages", String.class, List.class);
         packagesDesc.putMapPropertyType("installed_packages", String.class, List.class);
         constructor.addTypeDescription(packagesDesc);

         // Issue 855: testng is rev-locking us to snakeyaml 1.6
         // we have to use old constructor until this is fixed
         Yaml yaml = new Yaml(new Loader(constructor));
         ImageDescriptorLoader config = null;
         try {
            config = (ImageDescriptorLoader) yaml.load(Files.toString(
                     new File(getClass().getResource("/image-maker.yaml").toURI()), Charsets.UTF_8));
         } catch (Exception e) {
            Throwables.propagate(e);
         }
         checkState(config != null, "missing config: class");
         checkState(config.images != null, "missing images: collection");
         return config.images;
      }
   }

}
