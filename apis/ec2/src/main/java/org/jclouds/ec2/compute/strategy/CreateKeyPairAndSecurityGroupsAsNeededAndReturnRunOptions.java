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

package org.jclouds.ec2.compute.strategy;

import static com.google.common.base.Preconditions.checkState;

import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Template;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.compute.domain.RegionNameAndIngressRules;
import org.jclouds.ec2.compute.functions.CreateSecurityGroupIfNeeded;
import org.jclouds.ec2.compute.functions.CreateUniqueKeyPair;
import org.jclouds.ec2.compute.options.EC2TemplateOptions;
import org.jclouds.ec2.domain.BlockDeviceMapping;
import org.jclouds.ec2.domain.KeyPair;
import org.jclouds.ec2.options.RunInstancesOptions;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions {

   @VisibleForTesting
   public final Map<RegionAndName, KeyPair> credentialsMap;
   @VisibleForTesting
   public final Map<RegionAndName, String> securityGroupMap;
   @VisibleForTesting
   public final CreateUniqueKeyPair createUniqueKeyPair;
   @VisibleForTesting
   public final CreateSecurityGroupIfNeeded createSecurityGroupIfNeeded;
   private final javax.inject.Provider<RunInstancesOptions> optionsProvider;

   @Inject
   public CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions(Map<RegionAndName, KeyPair> credentialsMap,
            @Named("SECURITY") Map<RegionAndName, String> securityGroupMap, CreateUniqueKeyPair createUniqueKeyPair,
            CreateSecurityGroupIfNeeded createSecurityGroupIfNeeded,
            javax.inject.Provider<RunInstancesOptions> optionsProvider) {
      this.credentialsMap = credentialsMap;
      this.securityGroupMap = securityGroupMap;
      this.createUniqueKeyPair = createUniqueKeyPair;
      this.createSecurityGroupIfNeeded = createSecurityGroupIfNeeded;
      this.optionsProvider = optionsProvider;
   }

   public RunInstancesOptions execute(String region, String tag, Template template) {

      RunInstancesOptions instanceOptions = getOptionsProvider().get().asType(template.getHardware().getId());

      String keyPairName = createNewKeyPairUnlessUserSpecifiedOtherwise(region, tag, template.getOptions());

      addSecurityGroups(region, tag, template, instanceOptions);
      if (template.getOptions() instanceof EC2TemplateOptions) {

         if (keyPairName != null)
            instanceOptions.withKeyName(keyPairName);

         byte[] userData = EC2TemplateOptions.class.cast(template.getOptions()).getUserData();

         if (userData != null)
            instanceOptions.withUserData(userData);

         Set<BlockDeviceMapping> blockDeviceMappings = EC2TemplateOptions.class.cast(template.getOptions())
                  .getBlockDeviceMappings();
         if (blockDeviceMappings.size() > 0) {
            checkState("ebs".equals(template.getImage().getUserMetadata().get("rootDeviceType")),
                     "BlockDeviceMapping only available on ebs boot");
            instanceOptions.withBlockDeviceMappings(blockDeviceMappings);
         }
      }
      return instanceOptions;
   }

   protected void addSecurityGroups(String region, String tag, Template template, RunInstancesOptions instanceOptions) {
      Set<String> groups = getSecurityGroupsForTagAndOptions(region, tag, template.getOptions());
      instanceOptions.withSecurityGroups(groups);
   }

   @VisibleForTesting
   public String createNewKeyPairUnlessUserSpecifiedOtherwise(String region, String tag, TemplateOptions options) {
      String keyPairName = null;
      boolean shouldAutomaticallyCreateKeyPair = true;
      if (options instanceof EC2TemplateOptions) {
         keyPairName = EC2TemplateOptions.class.cast(options).getKeyPair();
         if (keyPairName == null)
            shouldAutomaticallyCreateKeyPair = EC2TemplateOptions.class.cast(options)
                     .shouldAutomaticallyCreateKeyPair();
      }
      if (keyPairName == null && shouldAutomaticallyCreateKeyPair) {
         RegionAndName regionAndName = new RegionAndName(region, tag);
         KeyPair keyPair = createUniqueKeyPair.apply(regionAndName);
         // get or create incidental resources
         // TODO race condition. we were using MapMaker, but it doesn't seem to
         // refresh properly
         // when
         // another thread
         // deletes a key
         credentialsMap.put(new RegionAndName(region, keyPair.getKeyName()), keyPair);
         keyPairName = keyPair.getKeyName();
      }
      return keyPairName;
   }

   @VisibleForTesting
   public Set<String> getSecurityGroupsForTagAndOptions(String region, @Nullable String tag, TemplateOptions options) {
      Set<String> groups = Sets.newLinkedHashSet();

      if (tag != null) {
         String markerGroup = String.format("jclouds#%s#%s", tag, region);
         groups.add(markerGroup);

         RegionNameAndIngressRules regionNameAndIngessRulesForMarkerGroup;

         if (options instanceof EC2TemplateOptions && EC2TemplateOptions.class.cast(options).getGroupIds().size() > 0) {
            regionNameAndIngessRulesForMarkerGroup = new RegionNameAndIngressRules(region, markerGroup, new int[] {},
                     false);
            groups.addAll(EC2TemplateOptions.class.cast(options).getGroupIds());

         } else {
            regionNameAndIngessRulesForMarkerGroup = new RegionNameAndIngressRules(region, markerGroup, options
                     .getInboundPorts(), true);
         }

         if (!securityGroupMap.containsKey(regionNameAndIngessRulesForMarkerGroup)) {
            securityGroupMap.put(regionNameAndIngessRulesForMarkerGroup, createSecurityGroupIfNeeded
                     .apply(regionNameAndIngessRulesForMarkerGroup));
         }
      }
      return groups;
   }

   // allows us to mock this method
   @VisibleForTesting
   public javax.inject.Provider<RunInstancesOptions> getOptionsProvider() {
      return optionsProvider;
   }
}