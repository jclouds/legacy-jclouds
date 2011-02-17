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

package org.jclouds.aws.ec2.compute.config;

import static org.jclouds.compute.domain.OsFamily.AMZN_LINUX;

import org.jclouds.aws.ec2.compute.AWSEC2TemplateBuilderImpl;
import org.jclouds.aws.ec2.compute.strategy.AWSEC2ReviseParsedImage;
import org.jclouds.aws.ec2.compute.strategy.CreateKeyPairPlacementAndSecurityGroupsAsNeededAndReturnRunOptions;
import org.jclouds.aws.ec2.compute.suppliers.AWSEC2HardwareSupplier;
import org.jclouds.aws.ec2.compute.suppliers.AWSRegionAndNameToImageSupplier;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.ec2.compute.config.EC2ComputeServiceContextModule;
import org.jclouds.ec2.compute.internal.EC2TemplateBuilderImpl;
import org.jclouds.ec2.compute.strategy.CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions;
import org.jclouds.ec2.compute.strategy.ReviseParsedImage;
import org.jclouds.ec2.compute.suppliers.EC2HardwareSupplier;
import org.jclouds.ec2.compute.suppliers.RegionAndNameToImageSupplier;

import com.google.inject.Injector;

/**
 * 
 * @author Adrian Cole
 */
public class AWSEC2ComputeServiceContextModule extends EC2ComputeServiceContextModule {

   @Override
   protected TemplateBuilder provideTemplate(Injector injector, TemplateBuilder template) {
      return template.osFamily(AMZN_LINUX).os64Bit(true);
   }

   @Override
   protected void configure() {
      super.configure();
      bind(ReviseParsedImage.class).to(AWSEC2ReviseParsedImage.class);
      bind(CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions.class).to(
               CreateKeyPairPlacementAndSecurityGroupsAsNeededAndReturnRunOptions.class);
      bind(EC2HardwareSupplier.class).to(AWSEC2HardwareSupplier.class);
      bind(RegionAndNameToImageSupplier.class).to(AWSRegionAndNameToImageSupplier.class);
      bind(EC2TemplateBuilderImpl.class).to(AWSEC2TemplateBuilderImpl.class);
   }

   @Override
   protected void installDependencies() {
      install(new AWSEC2ComputeServiceDependenciesModule());
   }

}
