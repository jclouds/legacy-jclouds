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

package org.jclouds.vcloud.compute.internal;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Size;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.domain.internal.TemplateBuilderImpl;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.Location;
import org.jclouds.vcloud.compute.options.VCloudTemplateOptions;

import com.google.common.base.Supplier;

/**
 * 
 * @author Adrian Cole
 */
public class VCloudTemplateBuilderImpl extends TemplateBuilderImpl {

   @Inject
   protected VCloudTemplateBuilderImpl(Supplier<Set<? extends Location>> locations,
            Supplier<Set<? extends Image>> images, Supplier<Set<? extends Size>> sizes,
            Supplier<Location> defaultLocation, Provider<TemplateOptions> optionsProvider,
            @Named("DEFAULT") Provider<TemplateBuilder> defaultTemplateProvider) {
      super(locations, images, sizes, defaultLocation, optionsProvider, defaultTemplateProvider);
   }

   @Override
   protected void copyTemplateOptions(TemplateOptions from, TemplateOptions to) {
      super.copyTemplateOptions(from, to);
      if (from instanceof VCloudTemplateOptions) {
         VCloudTemplateOptions eFrom = VCloudTemplateOptions.class.cast(from);
         VCloudTemplateOptions eTo = VCloudTemplateOptions.class.cast(to);
         if (eFrom.getCustomizationScript() != null)
            eTo.customizationScript(eFrom.getCustomizationScript());
      }
   }

}
