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

package org.jclouds.epc.config;

import static org.jclouds.compute.domain.OsFamily.CENTOS;

import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.ec2.compute.config.EC2ComputeServiceContextModule;
import org.jclouds.ec2.compute.strategy.ReviseParsedImage;
import org.jclouds.epc.strategy.EucalyptusPartnerCloudReviseParsedImage;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;

/**
 * 
 * @author Adrian Cole
 */
public class EucalyptusPartnerCloudComputeServiceContextModule extends EC2ComputeServiceContextModule {

   @Override
   protected TemplateBuilder provideTemplate(Injector injector, TemplateBuilder template) {
      String virt = injector.getInstance(Key.get(String.class, Names
               .named("eucalyptus-partnercloud-ec2.virtualization-type")));
      return template.osFamily(CENTOS).locationId(virt + "-cluster").osDescriptionMatches(virt);
   }

   @Override
   protected void configure() {
      super.configure();
      bind(ReviseParsedImage.class).to(EucalyptusPartnerCloudReviseParsedImage.class);
   }

}
