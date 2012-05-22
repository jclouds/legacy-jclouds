/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.ec2.compute.strategy;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.crypto.SshKeys.fingerprintPrivateKey;
import static org.jclouds.crypto.SshKeys.sha1PrivateKey;

import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Template;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.compute.functions.GroupNamingConvention.Factory;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.compute.domain.RegionNameAndIngressRules;
import org.jclouds.ec2.compute.options.EC2TemplateOptions;
import org.jclouds.ec2.domain.BlockDeviceMapping;
import org.jclouds.ec2.domain.KeyPair;
import org.jclouds.ec2.options.RunInstancesOptions;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.inject.Inject;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions {
   @VisibleForTesting
   public Function<RegionAndName, KeyPair> makeKeyPair;
   @VisibleForTesting
   public final ConcurrentMap<RegionAndName, KeyPair> credentialsMap;
   @VisibleForTesting
   public final LoadingCache<RegionAndName, String> securityGroupMap;
   @VisibleForTesting
   public final Provider<RunInstancesOptions> optionsProvider;
   private final Factory namingConvention;

   @Inject
   public CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions(Function<RegionAndName, KeyPair> makeKeyPair,
            ConcurrentMap<RegionAndName, KeyPair> credentialsMap,
            @Named("SECURITY") LoadingCache<RegionAndName, String> securityGroupMap,
            Provider<RunInstancesOptions> optionsProvider,
            GroupNamingConvention.Factory namingConvention) {
      this.makeKeyPair = checkNotNull(makeKeyPair, "makeKeyPair");
      this.credentialsMap = checkNotNull(credentialsMap, "credentialsMap");
      this.securityGroupMap = checkNotNull(securityGroupMap, "securityGroupMap");
      this.optionsProvider = checkNotNull(optionsProvider, "optionsProvider");
      this.namingConvention = checkNotNull(namingConvention, "namingConvention");
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
      } else if (keyPairName != null) {
         if (options.getLoginPrivateKey() != null) {
            String pem = options.getLoginPrivateKey();
            KeyPair keyPair = KeyPair.builder().region(region).keyName(keyPairName).fingerprint(
                     fingerprintPrivateKey(pem)).sha1OfPrivateKey(sha1PrivateKey(pem)).keyMaterial(pem).build();
            RegionAndName key = new RegionAndName(region, keyPairName);
            credentialsMap.put(key, keyPair);
         }
      }

      if (options.getRunScript() != null) {
         RegionAndName regionAndName = new RegionAndName(region, keyPairName);
         checkArgument(
                  credentialsMap.containsKey(regionAndName),
                  "no private key configured for: %s; please use options.overrideLoginCredentialWith(rsa_private_text)",
                  regionAndName);
      }
      return keyPairName;
   }

   // base EC2 driver currently does not support key import
   protected String createOrImportKeyPair(String region, String group, TemplateOptions options) {
      RegionAndName regionAndGroup = new RegionAndName(region, group);
      KeyPair keyPair;
      // make sure that we don't request multiple keys simultaneously
      synchronized (credentialsMap) {
         // if there is already a keypair for the group specified, use it
         if (credentialsMap.containsKey(regionAndGroup))
            return credentialsMap.get(regionAndGroup).getKeyName();

         // otherwise create a new keypair and key it under the group and also the regular keyname
         keyPair = makeKeyPair.apply(new RegionAndName(region, group));
         credentialsMap.put(regionAndGroup, keyPair);
      }
      credentialsMap.put(new RegionAndName(region, keyPair.getKeyName()), keyPair);
      return keyPair.getKeyName();
   }

   @VisibleForTesting
   public Set<String> getSecurityGroupsForTagAndOptions(String region, @Nullable String group, TemplateOptions options) {
      Builder<String> groups = ImmutableSet.builder();

      if (group != null) {
         String markerGroup = namingConvention.create().sharedNameForGroup(group);

         groups.add(markerGroup);

         RegionNameAndIngressRules regionNameAndIngessRulesForMarkerGroup;

         if (userSpecifiedTheirOwnGroups(options)) {
            regionNameAndIngessRulesForMarkerGroup = new RegionNameAndIngressRules(region, markerGroup, new int[] {},
                     false);
            groups.addAll(EC2TemplateOptions.class.cast(options).getGroups());
         } else {
            regionNameAndIngessRulesForMarkerGroup = new RegionNameAndIngressRules(region, markerGroup, options
                     .getInboundPorts(), true);
         }
         // this will create if not yet exists.
         securityGroupMap.getUnchecked(regionNameAndIngessRulesForMarkerGroup);
      }
      return groups.build();
   }

   protected boolean userSpecifiedTheirOwnGroups(TemplateOptions options) {
      return options instanceof EC2TemplateOptions && EC2TemplateOptions.class.cast(options).getGroups().size() > 0;
   }

   // allows us to mock this method
   @VisibleForTesting
   public javax.inject.Provider<RunInstancesOptions> getOptionsProvider() {
      return optionsProvider;
   }
}