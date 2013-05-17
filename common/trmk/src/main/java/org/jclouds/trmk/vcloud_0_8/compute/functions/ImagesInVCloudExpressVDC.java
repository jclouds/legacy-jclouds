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
package org.jclouds.trmk.vcloud_0_8.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Image;
import org.jclouds.trmk.vcloud_0_8.domain.VAppTemplate;
import org.jclouds.trmk.vcloud_0_8.domain.VDC;
import org.jclouds.trmk.vcloud_0_8.functions.VAppTemplatesForResourceEntities;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Singleton
public class ImagesInVCloudExpressVDC implements Function<VDC, Iterable<? extends Image>> {
   private final VAppTemplatesForResourceEntities vAppTemplatesForResourceEntities;
   private final ImageForVCloudExpressVAppTemplate imageForVAppTemplateProvider;

   @Inject
   public ImagesInVCloudExpressVDC(VAppTemplatesForResourceEntities vAppTemplatesForResourceEntities,
            ImageForVCloudExpressVAppTemplate imageForVAppTemplateProvider) {
      this.vAppTemplatesForResourceEntities = checkNotNull(vAppTemplatesForResourceEntities, "vAppTemplatesForResourceEntities");
      this.imageForVAppTemplateProvider = checkNotNull(imageForVAppTemplateProvider, "imageForVAppTemplateProvider");
   }

   @Override
   public Iterable<? extends Image> apply(VDC from) {
      Iterable<? extends VAppTemplate> vAppTemplates = vAppTemplatesForResourceEntities.apply(checkNotNull(from, "vdc")
               .getResourceEntities().values());
      return Iterables.transform(vAppTemplates, imageForVAppTemplateProvider.withParent(from));
   }

}
