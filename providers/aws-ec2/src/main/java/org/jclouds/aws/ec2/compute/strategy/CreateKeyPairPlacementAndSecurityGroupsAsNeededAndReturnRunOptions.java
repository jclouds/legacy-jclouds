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

package org.jclouds.aws.ec2.compute.strategy;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.aws.ec2.compute.AWSEC2TemplateOptions;
import org.jclouds.aws.ec2.functions.CreatePlacementGroupIfNeeded;
import org.jclouds.aws.ec2.options.AWSRunInstancesOptions;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.compute.functions.CreateSecurityGroupIfNeeded;
import org.jclouds.ec2.compute.functions.CreateUniqueKeyPair;
import org.jclouds.ec2.compute.options.EC2TemplateOptions;
import org.jclouds.ec2.compute.strategy.CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions;
import org.jclouds.ec2.domain.KeyPair;
import org.jclouds.ec2.options.RunInstancesOptions;

import com.google.common.annotations.VisibleForTesting;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class CreateKeyPairPlacementAndSecurityGroupsAsNeededAndReturnRunOptions extends
         CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions {

   @VisibleForTesting
   final Map<RegionAndName, String> placementGroupMap;
   @VisibleForTesting
   final CreatePlacementGroupIfNeeded createPlacementGroupIfNeeded;

   @Inject
   public CreateKeyPairPlacementAndSecurityGroupsAsNeededAndReturnRunOptions(
            Map<RegionAndName, KeyPair> credentialsMap, @Named("SECURITY") Map<RegionAndName, String> securityGroupMap,
            @Named("PLACEMENT") Map<RegionAndName, String> placementGroupMap, CreateUniqueKeyPair createUniqueKeyPair,
            CreateSecurityGroupIfNeeded createSecurityGroupIfNeeded,
            javax.inject.Provider<RunInstancesOptions> optionsProvider,
            CreatePlacementGroupIfNeeded createPlacementGroupIfNeeded) {
      super(credentialsMap, securityGroupMap, createUniqueKeyPair, createSecurityGroupIfNeeded, optionsProvider);
      this.placementGroupMap = placementGroupMap;
      this.createPlacementGroupIfNeeded = createPlacementGroupIfNeeded;
   }

   public AWSRunInstancesOptions execute(String region, String tag, Template template) {
      AWSRunInstancesOptions instanceOptions = AWSRunInstancesOptions.class.cast(super.execute(region, tag, template));

      String placementGroupName = template.getHardware().getId().startsWith("cc") ? createNewPlacementGroupUnlessUserSpecifiedOtherwise(
               region, tag, template.getOptions())
               : null;

      if (placementGroupName != null)
         instanceOptions.inPlacementGroup(placementGroupName);

      if (AWSEC2TemplateOptions.class.cast(template.getOptions()).isMonitoringEnabled())
         instanceOptions.enableMonitoring();

      return instanceOptions;
   }

   @VisibleForTesting
   String createNewPlacementGroupUnlessUserSpecifiedOtherwise(String region, String tag, TemplateOptions options) {
      String placementGroupName = null;
      boolean shouldAutomaticallyCreatePlacementGroup = true;
      if (options instanceof EC2TemplateOptions) {
         placementGroupName = AWSEC2TemplateOptions.class.cast(options).getPlacementGroup();
         if (placementGroupName == null)
            shouldAutomaticallyCreatePlacementGroup = AWSEC2TemplateOptions.class.cast(options)
                     .shouldAutomaticallyCreatePlacementGroup();
      }
      if (placementGroupName == null && shouldAutomaticallyCreatePlacementGroup) {
         placementGroupName = String.format("jclouds#%s#%s", tag, region);
         RegionAndName regionAndName = new RegionAndName(region, placementGroupName);
         if (!placementGroupMap.containsKey(regionAndName)) {
            placementGroupMap.put(regionAndName, createPlacementGroupIfNeeded.apply(regionAndName));
         }
      }
      return placementGroupName;
   }

   @Override
   protected void addSecurityGroups(String region, String tag, Template template, RunInstancesOptions instanceOptions) {
      String subnetId = AWSEC2TemplateOptions.class.cast(template.getOptions()).getSubnetId();
      if (subnetId != null) {
         AWSRunInstancesOptions.class.cast(instanceOptions).withSubnetId(subnetId);
      } else {
         super.addSecurityGroups(region, tag, template, instanceOptions);
      }
   }
}