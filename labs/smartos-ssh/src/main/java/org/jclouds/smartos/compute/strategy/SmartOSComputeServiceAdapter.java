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
package org.jclouds.smartos.compute.strategy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Template;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.smartos.SmartOSHostController;
import org.jclouds.smartos.compute.domain.DataSet;
import org.jclouds.smartos.compute.domain.VM;
import org.jclouds.smartos.compute.domain.VmNIC;
import org.jclouds.smartos.compute.domain.VmSpecification;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

/**
 * defines the connection between the {@link org.jclouds.smartos.compute.domain.SmartOSHostController}
 * implementation and the jclouds {@link ComputeService}
 * 
 */
@Singleton
public class SmartOSComputeServiceAdapter implements ComputeServiceAdapter<VM, VmSpecification, DataSet, SmartOSHostController> {
   private final SmartOSHostController host;
   private final Map<String, VmSpecification> specificationMap;


   @Inject
   public SmartOSComputeServiceAdapter(SmartOSHostController host) {
      this.host = checkNotNull(host, "host");

      Collection<VmSpecification> specifications = new ArrayList<VmSpecification>();

       specifications.add(VmSpecification.builder().alias("Standard Joyent VM, 1Gb RAM / 2Gb SWAP").ram(1024).maxSwap(2048)
               .nic(VmNIC.builder().simpleDHCPNic().build()).build());

       specifications.add(VmSpecification.builder().alias("Standard Joyent VM, 2Gb RAM / 4Gb SWAP").ram(2048).maxSwap(4096)
               .nic(VmNIC.builder().simpleDHCPNic().build()).build());

       specifications.add(VmSpecification.builder().alias("Standard Joyent VM, 4Gb RAM / 8Gb SWAP").ram(4096).maxSwap(8192)
               .nic(VmNIC.builder().simpleDHCPNic().build()).build());

       specifications.add(VmSpecification.builder().alias("Standard Joyent VM, 8Gb RAM / 16Gb SWAP").ram(8192).maxSwap(16384)
               .nic(VmNIC.builder().simpleDHCPNic().build()).build());

      specificationMap = Maps.uniqueIndex(specifications, new Function<VmSpecification,String>() {
          @Override
          public String apply(VmSpecification input) {
              return input.getAlias();
          }
      });

   }

   private SmartOSHostController getHost() {
      return host;
   }

   @Override
   public NodeAndInitialCredentials<VM> createNodeWithGroupEncodedIntoName(String tag, String name, Template template) {

      VmSpecification.Builder builder = VmSpecification.builder();
      String providerId = template.getHardware().getProviderId();

      if( specificationMap.containsKey(providerId) ) {
          builder.fromVmSpecification( specificationMap.get(providerId) );
      } else {
          builder.nic(VmNIC.builder().simpleDHCPNic().build());
      }

      VmSpecification specification = builder.alias(name)
               .dataset(getHost().getDataSet(UUID.fromString(template.getImage().getProviderId())))
               .build();

      VM from = getHost().createVM(specification);

      return new NodeAndInitialCredentials<VM>(from, from.getUuid() + "", LoginCredentials.builder().user("smartos")
               .password("smartos").build());
   }



   @Override
   public Iterable<VmSpecification> listHardwareProfiles() {
      return specificationMap.values();
   }

   @Override
   public Iterable<DataSet> listImages() {
      return getHost().getLocalDatasets();
   }

   @Override
   public DataSet getImage(String id) {
      return getHost().getDataSet(UUID.fromString(id));
   }

   @Override
   public Iterable<VM> listNodes() {
      return getHost().getVMs();
   }

   @Override
   public Iterable<SmartOSHostController> listLocations() {
      return ImmutableSet.of();
   }

   @Override
   public VM getNode(String id) {
      return getHost().getVM(UUID.fromString(id));
   }

   @Override
   public void destroyNode(String id) {
      getHost().destroyHost(UUID.fromString(id));
   }

   @Override
   public void rebootNode(String id) {
      getHost().rebootHost(UUID.fromString(id));
   }

   @Override
   public void resumeNode(String id) {
      getHost().startHost(UUID.fromString(id));
   }

   @Override
   public void suspendNode(String id) {
      getHost().stopHost(UUID.fromString(id));
   }
}
