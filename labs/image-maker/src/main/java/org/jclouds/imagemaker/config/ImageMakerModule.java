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

import org.jclouds.imagemaker.PackageProcessor;
import org.jclouds.imagemaker.internal.AptCacher;
import org.jclouds.imagemaker.internal.AptInstaller;

import com.google.common.base.Supplier;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;

public class ImageMakerModule extends AbstractModule {

   @Override
   protected void configure() {
      bind(new TypeLiteral<Supplier<List<ImageDescriptor>>>() {
      }).to(ImageDescriptor.ImageDescriptorLoader.class);
      Multibinder<PackageProcessor> packageBinder = Multibinder.newSetBinder(binder(), PackageProcessor.class);
      packageBinder.addBinding().to(AptCacher.class);
      packageBinder.addBinding().to(AptInstaller.class);
   }
}
