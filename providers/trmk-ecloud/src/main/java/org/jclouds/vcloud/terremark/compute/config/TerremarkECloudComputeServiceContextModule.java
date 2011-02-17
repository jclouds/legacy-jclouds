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

package org.jclouds.vcloud.terremark.compute.config;

import static org.jclouds.compute.domain.OsFamily.CENTOS;

import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.vcloud.compute.functions.ParseOsFromVAppTemplateName;
import org.jclouds.vcloud.terremark.compute.functions.TerremarkECloudParseOsFromVAppTemplateName;
import org.jclouds.vcloud.terremark.suppliers.InternetServiceAndPublicIpAddressSupplier;
import org.jclouds.vcloud.terremark.suppliers.TerremarkECloudInternetServiceAndPublicIpAddressSupplier;

import com.google.inject.Injector;

/**
 * @author Adrian Cole
 */
public class TerremarkECloudComputeServiceContextModule extends TerremarkVCloudComputeServiceContextModule {

   @Override
   protected void configure() {
      bind(InternetServiceAndPublicIpAddressSupplier.class).to(
               TerremarkECloudInternetServiceAndPublicIpAddressSupplier.class);
      bind(ParseOsFromVAppTemplateName.class).to(TerremarkECloudParseOsFromVAppTemplateName.class);
      super.configure();
   }

   // as of 6-nov-2010 only centos has ssh key injection in the images.
   // ssh key injection in ubuntu is targeted for dec-2010 or sooner
   @Override
   protected TemplateBuilder provideTemplate(Injector injector, TemplateBuilder template) {
      return template.osFamily(CENTOS).os64Bit(true);
   }

}
