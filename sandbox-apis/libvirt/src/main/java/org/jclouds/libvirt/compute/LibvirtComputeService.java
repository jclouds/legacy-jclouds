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

package org.jclouds.libvirt.compute;

import java.io.StringReader;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.internal.BaseComputeService;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.reference.ComputeServiceConstants.Timeouts;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.strategy.InitializeRunScriptOnNodeOrPlaceInBadMap;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.RebootNodeStrategy;
import org.jclouds.compute.strategy.ResumeNodeStrategy;
import org.jclouds.compute.strategy.CreateNodesInGroupThenAddToSet;
import org.jclouds.compute.strategy.SuspendNodeStrategy;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.libvirt.Connect;
import org.libvirt.StorageVol;
import org.xml.sax.InputSource;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.jamesmurty.utils.XMLBuilder;

/**
 * 
 * @author andrea turli
 * 
 */
@Singleton
public class LibvirtComputeService extends BaseComputeService {

   private final Connect client;

   @Inject
   protected LibvirtComputeService(ComputeServiceContext context, Map<String, Credentials> credentialStore,
            @Memoized Supplier<Set<? extends Image>> images,
            @Memoized Supplier<Set<? extends Hardware>> hardwareProfiles,
            @Memoized Supplier<Set<? extends Location>> locations, ListNodesStrategy listNodesStrategy,
            GetNodeMetadataStrategy getNodeMetadataStrategy, CreateNodesInGroupThenAddToSet runNodesAndAddToSetStrategy,
            RebootNodeStrategy rebootNodeStrategy, DestroyNodeStrategy destroyNodeStrategy,
            ResumeNodeStrategy resumeNodeStrategy, SuspendNodeStrategy suspendNodeStrategy,
            Provider<TemplateBuilder> templateBuilderProvider, Provider<TemplateOptions> templateOptionsProvider,
            @Named("NODE_RUNNING") Predicate<NodeMetadata> nodeRunning,
            @Named("NODE_TERMINATED") Predicate<NodeMetadata> nodeTerminated,
            @Named("NODE_SUSPENDED") Predicate<NodeMetadata> nodeSuspended,
            InitializeRunScriptOnNodeOrPlaceInBadMap.Factory initScriptRunnerFactory, Timeouts timeouts,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor, Connect client) {
      super(context, credentialStore, images, hardwareProfiles, locations, listNodesStrategy, getNodeMetadataStrategy,
               runNodesAndAddToSetStrategy, rebootNodeStrategy, destroyNodeStrategy, resumeNodeStrategy,
               suspendNodeStrategy, templateBuilderProvider, templateOptionsProvider, nodeRunning, nodeTerminated,
               nodeSuspended, initScriptRunnerFactory, timeouts, executor);

      this.client = client;

   }

   @Override
   public void destroyNode(String id) {
      super.destroyNode(id);
      // eliminateDomain(id);
   }

   private void eliminateDomain(String id) {
      try {
         XMLBuilder builder = XMLBuilder.parse(new InputSource(new StringReader(client.domainLookupByUUIDString(id)
                  .getXMLDesc(0))));
         String diskFileName = builder.xpathFind("//devices/disk[@device='disk']/source").getElement().getAttribute(
                  "file");
         StorageVol storageVol = client.storageVolLookupByPath(diskFileName);
         storageVol.delete(0);
         client.domainLookupByUUIDString(id).undefine();
      } catch (Exception e) {
         propogate(e);
      }
   }

   protected <T> T propogate(Exception e) {
      Throwables.propagate(e);
      assert false;
      return null;
   }

}
