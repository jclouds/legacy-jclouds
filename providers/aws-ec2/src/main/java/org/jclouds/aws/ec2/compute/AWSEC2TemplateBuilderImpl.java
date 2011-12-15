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
package org.jclouds.aws.ec2.compute;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.Location;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.compute.internal.EC2TemplateBuilderImpl;

import com.google.common.base.Supplier;
import com.google.common.cache.LoadingCache;

/**
 * 
 * @author Adrian Cole
 */
public class AWSEC2TemplateBuilderImpl extends EC2TemplateBuilderImpl {

   @Inject
   protected AWSEC2TemplateBuilderImpl(@Memoized Supplier<Set<? extends Location>> locations,
         @Memoized Supplier<Set<? extends Image>> images, @Memoized Supplier<Set<? extends Hardware>> sizes,
         Supplier<Location> defaultLocation, @Named("DEFAULT") Provider<TemplateOptions> optionsProvider,
         @Named("DEFAULT") Provider<TemplateBuilder> defaultTemplateProvider, Supplier<LoadingCache<RegionAndName, ? extends Image>> imageMap) {
      super(locations, images, sizes, defaultLocation, optionsProvider, defaultTemplateProvider, imageMap);
   }

}
