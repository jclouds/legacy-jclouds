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

package org.jclouds.vcloud.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Image;
import org.jclouds.vcloud.domain.VCloudExpressVAppTemplate;
import org.jclouds.vcloud.domain.VDC;
import org.jclouds.vcloud.functions.VCloudExpressVAppTemplatesForResourceEntities;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Singleton
public class ImagesInVCloudExpressVDC implements Function<VDC, Iterable<? extends Image>> {
   private final VCloudExpressVAppTemplatesForResourceEntities vAppTemplatesForResourceEntities;
   private final ImageForVCloudExpressVAppTemplate imageForVAppTemplateProvider;

   @Inject
   public ImagesInVCloudExpressVDC(VCloudExpressVAppTemplatesForResourceEntities vAppTemplatesForResourceEntities,
            ImageForVCloudExpressVAppTemplate imageForVAppTemplateProvider) {
      this.vAppTemplatesForResourceEntities = checkNotNull(vAppTemplatesForResourceEntities, "vAppTemplatesForResourceEntities");
      this.imageForVAppTemplateProvider = checkNotNull(imageForVAppTemplateProvider, "imageForVAppTemplateProvider");
   }

   @Override
   public Iterable<? extends Image> apply(VDC from) {
      Iterable<? extends VCloudExpressVAppTemplate> vAppTemplates = vAppTemplatesForResourceEntities.apply(checkNotNull(from, "vdc")
               .getResourceEntities().values());
      return Iterables.transform(vAppTemplates, imageForVAppTemplateProvider.withParent(from));
   }

}