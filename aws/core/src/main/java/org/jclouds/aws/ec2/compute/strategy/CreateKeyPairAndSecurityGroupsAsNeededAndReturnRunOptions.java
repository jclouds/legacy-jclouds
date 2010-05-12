/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import static com.google.common.base.Preconditions.checkArgument;
import static org.jclouds.aws.ec2.options.RunInstancesOptions.Builder.*;

import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.aws.ec2.compute.domain.EC2Size;
import org.jclouds.aws.ec2.compute.domain.RegionAndName;
import org.jclouds.aws.ec2.compute.domain.RegionNameAndIngressRules;
import org.jclouds.aws.ec2.compute.functions.CreateSecurityGroupIfNeeded;
import org.jclouds.aws.ec2.compute.functions.CreateUniqueKeyPair;
import org.jclouds.aws.ec2.compute.options.EC2TemplateOptions;
import org.jclouds.aws.ec2.domain.KeyPair;
import org.jclouds.aws.ec2.options.RunInstancesOptions;
import org.jclouds.compute.domain.Template;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions {
   @VisibleForTesting
   final Map<RegionAndName, KeyPair> credentialsMap;
   @VisibleForTesting
   final Map<RegionAndName, String> securityGroupMap;
   @VisibleForTesting
   final CreateUniqueKeyPair createUniqueKeyPair;
   @VisibleForTesting
   final CreateSecurityGroupIfNeeded createSecurityGroupIfNeeded;

   @Inject
   CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions(
            Map<RegionAndName, KeyPair> credentialsMap,
            Map<RegionAndName, String> securityGroupMap, CreateUniqueKeyPair createUniqueKeyPair,
            CreateSecurityGroupIfNeeded createSecurityGroupIfNeeded) {
      this.credentialsMap = credentialsMap;
      this.securityGroupMap = securityGroupMap;
      this.createUniqueKeyPair = createUniqueKeyPair;
      this.createSecurityGroupIfNeeded = createSecurityGroupIfNeeded;
   }

   public RunInstancesOptions execute(String region, String tag, Template template) {
      checkArgument(template.getSize() instanceof EC2Size,
               "unexpected image type. should be EC2Size, was: " + template.getSize().getClass());
      EC2Size ec2Size = EC2Size.class.cast(template.getSize());

      checkArgument(template.getOptions() instanceof EC2TemplateOptions,
               "unexpected options type. should be EC2Options, was: "
                        + template.getOptions().getClass());
      EC2TemplateOptions options = EC2TemplateOptions.class.cast(template.getOptions());

      String keyPairName = createNewKeyPairUnlessUserSpecifiedOtherwise(region, tag, options);
      Set<String> groups = getSecurityGroupsForTagAndOptions(region, tag, options);

      RunInstancesOptions instanceOptions = asType(ec2Size.getInstanceType())//
               .withSecurityGroups(groups)//
               .withAdditionalInfo(tag);
      
      if (keyPairName != null)
         instanceOptions.withKeyName(keyPairName);
      
      return instanceOptions;
   }

   @VisibleForTesting
   String createNewKeyPairUnlessUserSpecifiedOtherwise(String region, String tag,
            EC2TemplateOptions options) {
      String keyPairName = options.getKeyPair();
      if (keyPairName == null && options.shouldAutomaticallyCreateKeyPair()) {
         RegionAndName regionAndName = new RegionAndName(region, tag);
         KeyPair keyPair = createUniqueKeyPair.apply(regionAndName);
         // get or create incidental resources
         // TODO race condition. we were using MapMaker, but it doesn't seem to refresh properly
         // when
         // another thread
         // deletes a key
         credentialsMap.put(new RegionAndName(region, keyPair.getKeyName()), keyPair);
         keyPairName = keyPair.getKeyName();
      }
      return keyPairName;
   }

   @VisibleForTesting
   Set<String> getSecurityGroupsForTagAndOptions(String region, @Nullable String tag,
            EC2TemplateOptions options) {
      Set<String> groups = Sets.newLinkedHashSet();

      if (tag != null) {
         String markerGroup = "jclouds#" + tag;
         groups.add(markerGroup);

         RegionNameAndIngressRules regionNameAndIngessRulesForMarkerGroup;

         if (options.getGroupIds().size() == 0) {
            regionNameAndIngessRulesForMarkerGroup = new RegionNameAndIngressRules(region,
                     markerGroup, options.getInboundPorts(), true);
         } else {
            regionNameAndIngessRulesForMarkerGroup = new RegionNameAndIngressRules(region,
                     markerGroup, new int[] {}, false);
         }

         if (!securityGroupMap.containsKey(regionNameAndIngessRulesForMarkerGroup)) {
            securityGroupMap.put(regionNameAndIngessRulesForMarkerGroup,
                     createSecurityGroupIfNeeded.apply(regionNameAndIngessRulesForMarkerGroup));
         }
      }
      groups.addAll(options.getGroupIds());
      return groups;
   }
}