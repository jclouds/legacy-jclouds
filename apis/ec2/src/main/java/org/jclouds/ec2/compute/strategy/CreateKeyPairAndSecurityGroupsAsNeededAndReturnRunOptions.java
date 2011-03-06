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
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Template;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.compute.domain.RegionNameAndIngressRules;
import org.jclouds.ec2.compute.options.EC2TemplateOptions;
import org.jclouds.ec2.domain.BlockDeviceMapping;
import org.jclouds.ec2.domain.KeyPair;
import org.jclouds.ec2.options.RunInstancesOptions;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

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
   public final Function<RegionAndName, KeyPair> createUniqueKeyPair;
   @VisibleForTesting
   public final Function<RegionNameAndIngressRules, String> createSecurityGroupIfNeeded;
   protected final Provider<RunInstancesOptions> optionsProvider;

   @Inject
   public CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions(Map<RegionAndName, KeyPair> credentialsMap,
         @Named("SECURITY") Map<RegionAndName, String> securityGroupMap, Function<RegionAndName, KeyPair> createUniqueKeyPair,
         Function<RegionNameAndIngressRules, String> createSecurityGroupIfNeeded,
         Provider<RunInstancesOptions> optionsProvider) {
      this.credentialsMap = credentialsMap;
      this.securityGroupMap = securityGroupMap;
      this.createUniqueKeyPair = createUniqueKeyPair;
      this.createSecurityGroupIfNeeded = createSecurityGroupIfNeeded;
      this.optionsProvider = optionsProvider;
   }

   public RunInstancesOptions execute(String region, String group, Template template) {

      RunInstancesOptions instanceOptions = getOptionsProvider().get().asType(template.getHardware().getId());

      String keyPairName = createNewKeyPairUnlessUserSpecifiedOtherwise(region, group, template.getOptions());

      addSecurityGroups(region, group, template, instanceOptions);
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

   protected void addSecurityGroups(String region, String group, Template template, RunInstancesOptions instanceOptions) {
      Set<String> groups = getSecurityGroupsForTagAndOptions(region, group, template.getOptions());
      instanceOptions.withSecurityGroups(groups);
   }

   @VisibleForTesting
   public String createNewKeyPairUnlessUserSpecifiedOtherwise(String region, String group, TemplateOptions options) {
      String keyPairName = null;
      boolean shouldAutomaticallyCreateKeyPair = true;
      if (options instanceof EC2TemplateOptions) {
         keyPairName = EC2TemplateOptions.class.cast(options).getKeyPair();
         if (keyPairName == null)
            shouldAutomaticallyCreateKeyPair = EC2TemplateOptions.class.cast(options)
                  .shouldAutomaticallyCreateKeyPair();
      }
      if (keyPairName == null && shouldAutomaticallyCreateKeyPair) {
         keyPairName = createOrImportKeyPair(region, group, options);
      }
      return keyPairName;
   }

   // base EC2 driver currently does not support key import
   protected String createOrImportKeyPair(String region, String group, TemplateOptions options) {
      return createUniqueKeyPairAndPutIntoMap(region, group);
   }

   protected String createUniqueKeyPairAndPutIntoMap(String region, String group) {
      String keyPairName;
      RegionAndName regionAndName = new RegionAndName(region, group);
      KeyPair keyPair = createUniqueKeyPair.apply(regionAndName);
      keyPairName = keyPair.getKeyName();
      // get or create incidental resources
      // TODO race condition. we were using MapMaker, but it doesn't seem to
      // refresh properly
      // when
      // another thread
      // deletes a key
      credentialsMap.put(new RegionAndName(regionAndName.getRegion(), keyPairName), keyPair);
      return keyPairName;
   }

   @VisibleForTesting
   public Set<String> getSecurityGroupsForTagAndOptions(String region, @Nullable String group, TemplateOptions options) {
      Builder<String> groups = ImmutableSet.<String> builder();

      if (group != null) {
         String markerGroup = String.format("jclouds#%s#%s", group, region);
         groups.add(markerGroup);

         RegionNameAndIngressRules regionNameAndIngessRulesForMarkerGroup;

         if (options instanceof EC2TemplateOptions && EC2TemplateOptions.class.cast(options).getGroupIds().size() > 0) {
            regionNameAndIngessRulesForMarkerGroup = new RegionNameAndIngressRules(region, markerGroup, new int[] {},
                  false);
            groups.addAll(EC2TemplateOptions.class.cast(options).getGroupIds());

         } else {
            regionNameAndIngessRulesForMarkerGroup = new RegionNameAndIngressRules(region, markerGroup,
                  options.getInboundPorts(), true);
         }

         if (!securityGroupMap.containsKey(regionNameAndIngessRulesForMarkerGroup)) {
            securityGroupMap.put(regionNameAndIngessRulesForMarkerGroup,
                  createSecurityGroupIfNeeded.apply(regionNameAndIngessRulesForMarkerGroup));
         }
      }
      return groups.build();
   }

   // allows us to mock this method
   @VisibleForTesting
   public javax.inject.Provider<RunInstancesOptions> getOptionsProvider() {
      return optionsProvider;
   }
}