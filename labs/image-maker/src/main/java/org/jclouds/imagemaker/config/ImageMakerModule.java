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

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.imagemaker.PackageProcessor;
import org.jclouds.imagemaker.internal.AptCacher;
import org.jclouds.imagemaker.internal.AptInstaller;
import org.testng.collections.Maps;
import org.yaml.snakeyaml.Loader;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import com.google.common.base.Charsets;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.base.Throwables;
import com.google.common.io.Files;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.multibindings.Multibinder;

public class ImageMakerModule extends AbstractModule {

   public static final String IMAGE_DESCRIPTOR_FILE = "org.jclouds.image-descriptor-file";

   @Override
   protected void configure() {
      Multibinder<PackageProcessor> packageBinder = Multibinder.newSetBinder(binder(), PackageProcessor.class);
      packageBinder.addBinding().to(AptCacher.class);
      packageBinder.addBinding().to(AptInstaller.class);
   }

   @Provides
   @Singleton
   public Supplier<Map<String, ImageDescriptor>> imageDescriptors() {
      return Suppliers.ofInstance(new ImageDescriptors().load().asMap());
   }

   public static class ImageDescriptors {

      public List<ImageDescriptor> images;

      @Inject(optional = true)
      @Named(IMAGE_DESCRIPTOR_FILE)
      String imageDescriptorFileLocation;

      public Map<String, ImageDescriptor> asMap() {
         Map<String, ImageDescriptor> imagesMap = Maps.newHashMap();
         for (ImageDescriptor descriptor : this.images) {
            imagesMap.put(descriptor.id, descriptor);
         }
         return imagesMap;
      }

      public ImageDescriptors load() {
         Constructor constructor = new Constructor(ImageDescriptors.class);

         TypeDescription packagesDesc = new TypeDescription(ImageDescriptor.class);
         packagesDesc.putMapPropertyType("cached_packages", String.class, List.class);
         packagesDesc.putMapPropertyType("installed_packages", String.class, List.class);
         constructor.addTypeDescription(packagesDesc);

         // Issue 855: testng is rev-locking us to snakeyaml 1.6
         // we have to use old constructor until this is fixed
         Yaml yaml = new Yaml(new Loader(constructor));

         try {
            File imageDescriptorFile;
            if (imageDescriptorFileLocation != null) {
               imageDescriptorFile = new File(imageDescriptorFileLocation);
            } else {
               imageDescriptorFile = new File(getClass().getResource("/image-maker.yaml").toURI());
            }
            this.images = ((ImageDescriptors) yaml.load(Files.toString(imageDescriptorFile, Charsets.UTF_8))).images;
         } catch (Exception e) {
            Throwables.propagate(e);
         }
         checkState(this.images != null, "did not load images");
         return this;
      }
   }

}
