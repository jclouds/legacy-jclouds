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
package org.jclouds.openstack.nova.v2_0.compute.strategy;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.crypto.SshKeys.fingerprintPrivateKey;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.compute.config.CustomizationResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.compute.strategy.CreateNodeWithGroupEncodedIntoName;
import org.jclouds.compute.strategy.CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.impl.CreateNodesWithGroupEncodedIntoNameThenAddToSet;
import org.jclouds.concurrent.Futures;
import org.jclouds.openstack.nova.v2_0.NovaClient;
import org.jclouds.openstack.nova.v2_0.compute.functions.AllocateAndAddFloatingIpToNode;
import org.jclouds.openstack.nova.v2_0.compute.options.NovaTemplateOptions;
import org.jclouds.openstack.nova.v2_0.domain.KeyPair;
import org.jclouds.openstack.nova.v2_0.domain.zonescoped.SecurityGroupInZone;
import org.jclouds.openstack.nova.v2_0.domain.zonescoped.ZoneAndName;
import org.jclouds.openstack.nova.v2_0.domain.zonescoped.ZoneSecurityGroupNameAndPorts;

import com.google.common.base.Throwables;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Multimap;
import com.google.common.primitives.Ints;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ApplyNovaTemplateOptionsCreateNodesWithGroupEncodedIntoNameThenAddToSet extends
         CreateNodesWithGroupEncodedIntoNameThenAddToSet {

   private final AllocateAndAddFloatingIpToNode allocateAndAddFloatingIpToNode;
   private final LoadingCache<ZoneAndName, SecurityGroupInZone> securityGroupCache;
   private final LoadingCache<ZoneAndName, KeyPair> keyPairCache;
   private final NovaClient novaClient;

   @Inject
   protected ApplyNovaTemplateOptionsCreateNodesWithGroupEncodedIntoNameThenAddToSet(
            CreateNodeWithGroupEncodedIntoName addNodeWithTagStrategy,
            ListNodesStrategy listNodesStrategy,
            GroupNamingConvention.Factory namingConvention,
            CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap.Factory customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor,
            AllocateAndAddFloatingIpToNode allocateAndAddFloatingIpToNode,
            LoadingCache<ZoneAndName, SecurityGroupInZone> securityGroupCache,
            LoadingCache<ZoneAndName, KeyPair> keyPairCache, NovaClient novaClient) {
      super(addNodeWithTagStrategy, listNodesStrategy, namingConvention, executor,
               customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory);
      this.securityGroupCache = checkNotNull(securityGroupCache, "securityGroupCache");
      this.keyPairCache = checkNotNull(keyPairCache, "keyPairCache");
      this.allocateAndAddFloatingIpToNode = checkNotNull(allocateAndAddFloatingIpToNode,
               "allocateAndAddFloatingIpToNode");
      this.novaClient = checkNotNull(novaClient, "novaClient");
   }

   @Override
   public Map<?, Future<Void>> execute(String group, int count, Template template, Set<NodeMetadata> goodNodes,
            Map<NodeMetadata, Exception> badNodes, Multimap<NodeMetadata, CustomizationResponse> customizationResponses) {

      Template mutableTemplate = template.clone();

      NovaTemplateOptions templateOptions = NovaTemplateOptions.class.cast(mutableTemplate.getOptions());

      assert template.getOptions().equals(templateOptions) : "options didn't clone properly";

      String zone = mutableTemplate.getLocation().getId();

      if (templateOptions.shouldAutoAssignFloatingIp()) {
         checkArgument(novaClient.getFloatingIPExtensionForZone(zone).isPresent(),
                  "Floating IPs are required by options, but the extension is not available! options: %s",
                  templateOptions);
      }

      boolean keyPairExensionPresent = novaClient.getKeyPairExtensionForZone(zone).isPresent();
      if (templateOptions.shouldGenerateKeyPair()) {
         checkArgument(keyPairExensionPresent,
                  "Key Pairs are required by options, but the extension is not available! options: %s", templateOptions);
         KeyPair keyPair = keyPairCache.getUnchecked(ZoneAndName.fromZoneAndName(zone, namingConvention.create()
                  .sharedNameForGroup(group)));
         keyPairCache.asMap().put(ZoneAndName.fromZoneAndName(zone, keyPair.getName()), keyPair);
         templateOptions.keyPairName(keyPair.getName());
      } else if (templateOptions.getKeyPairName() != null) {
         checkArgument(keyPairExensionPresent,
                  "Key Pairs are required by options, but the extension is not available! options: %s", templateOptions);
         if (templateOptions.getLoginPrivateKey() != null) {
            String pem = templateOptions.getLoginPrivateKey();
            KeyPair keyPair = KeyPair.builder().name(templateOptions.getKeyPairName())
                     .fingerprint(fingerprintPrivateKey(pem)).privateKey(pem).build();
            keyPairCache.asMap().put(ZoneAndName.fromZoneAndName(zone, keyPair.getName()), keyPair);
         }
      }

      boolean securityGroupExensionPresent = novaClient.getSecurityGroupExtensionForZone(zone).isPresent();
      List<Integer> inboundPorts = Ints.asList(templateOptions.getInboundPorts());
      if (templateOptions.getSecurityGroupNames().size() > 0) {
         checkArgument(novaClient.getSecurityGroupExtensionForZone(zone).isPresent(),
                  "Security groups are required by options, but the extension is not available! options: %s",
                  templateOptions);
      } else if (securityGroupExensionPresent && inboundPorts.size() > 0) {
         String securityGroupName = namingConvention.create().sharedNameForGroup(group);
         try {
            securityGroupCache.get(new ZoneSecurityGroupNameAndPorts(zone, securityGroupName, inboundPorts));
         } catch (ExecutionException e) {
            throw Throwables.propagate(e.getCause());
         }
         templateOptions.securityGroupNames(securityGroupName);
      }

      return super.execute(group, count, mutableTemplate, goodNodes, badNodes, customizationResponses);
   }

   @Override
   protected Future<AtomicReference<NodeMetadata>> createNodeInGroupWithNameAndTemplate(String group,
            final String name, Template template) {

      Future<AtomicReference<NodeMetadata>> future = super.createNodeInGroupWithNameAndTemplate(group, name, template);
      NovaTemplateOptions templateOptions = NovaTemplateOptions.class.cast(template.getOptions());

      if (templateOptions.shouldAutoAssignFloatingIp()) {
         return Futures.compose(future, allocateAndAddFloatingIpToNode, executor);
      } else {
         return future;
      }
   }

}