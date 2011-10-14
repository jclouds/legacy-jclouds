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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Template;
import org.jclouds.compute.options.TemplateOptions;
import static org.jclouds.crypto.SshKeys.*;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.compute.domain.RegionNameAndIngressRules;
import org.jclouds.ec2.compute.options.EC2TemplateOptions;
import org.jclouds.ec2.domain.BlockDeviceMapping;
import org.jclouds.ec2.domain.KeyPair;
import org.jclouds.ec2.options.RunInstancesOptions;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.cache.Cache;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.util.concurrent.UncheckedExecutionException;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions {
   @VisibleForTesting
   public final Cache<RegionAndName, KeyPair> credentialsMap;
   @VisibleForTesting
   public final Cache<RegionAndName, String> securityGroupMap;
   protected final Provider<RunInstancesOptions> optionsProvider;

   @Inject
   public CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions(Cache<RegionAndName, KeyPair> credentialsMap,
            @Named("SECURITY") Cache<RegionAndName, String> securityGroupMap,
            Provider<RunInstancesOptions> optionsProvider) {
      this.credentialsMap = checkNotNull(credentialsMap, "credentialsMap");
      this.securityGroupMap = checkNotNull(securityGroupMap, "securityGroupMap");
      this.optionsProvider = checkNotNull(optionsProvider, "optionsProvider");
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
         if (options.getOverridingCredentials() != null && options.getOverridingCredentials().credential != null) {
            String pem = options.getOverridingCredentials().credential;
            KeyPair keyPair = KeyPair.builder().region(region).keyName(keyPairName).fingerprint(
                     fingerprintPrivateKey(pem)).sha1OfPrivateKey(sha1PrivateKey(pem)).keyMaterial(pem).build();
            RegionAndName key = new RegionAndName(region, keyPairName);
            credentialsMap.asMap().put(key, keyPair);
         }
      }

      if (options.getRunScript() != null) {
         RegionAndName regionAndName = new RegionAndName(region, keyPairName);
         String message = String
                  .format(
                           "no private key configured for: %s; please use options.overrideLoginCredentialWith(rsa_private_text)",
                           regionAndName);
         // test to see if this is in cache.
         try {
            credentialsMap.getUnchecked(regionAndName);
         } catch (NullPointerException nex) {
            throw new IllegalArgumentException(message, nex);
         } catch (UncheckedExecutionException nex) {
            throw new IllegalArgumentException(message, nex);
         }
      }
      return keyPairName;
   }

   // base EC2 driver currently does not support key import
   protected String createOrImportKeyPair(String region, String group, TemplateOptions options) {
      return credentialsMap.getUnchecked(new RegionAndName(region, group)).getKeyName();
   }

   @VisibleForTesting
   public Set<String> getSecurityGroupsForTagAndOptions(String region, @Nullable String group, TemplateOptions options) {
      Builder<String> groups = ImmutableSet.<String> builder();

      if (group != null) {
         String markerGroup = String.format("jclouds#%s#%s", group, region);
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