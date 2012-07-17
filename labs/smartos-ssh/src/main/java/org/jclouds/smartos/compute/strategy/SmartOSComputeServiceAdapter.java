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

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.smartos.compute.domain.*;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Template;
import org.jclouds.domain.LoginCredentials;

import com.google.common.collect.ImmutableSet;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * defines the connection between the {@link org.jclouds.smartos.compute.domain.SmartOSHost} implementation and the jclouds
 * {@link ComputeService}
 * 
 */
@Singleton
public class SmartOSComputeServiceAdapter implements ComputeServiceAdapter<VM, VmSpecification, DataSet, SmartOSHost> {
   private final SmartOSHost host;

   @Inject
   public SmartOSComputeServiceAdapter(SmartOSHost host) {
      this.host = checkNotNull(host, "host");
   }

   private SmartOSHost getHost() {
       return host;
   }

   @Override
   public NodeAndInitialCredentials<VM> createNodeWithGroupEncodedIntoName(String tag, String name, Template template) {
       VmSpecification specification = VmSpecification.builder()
               .alias(name)
               .dataset(getHost().getDataSet(UUID.fromString(template.getImage().getProviderId())))
               .nic(VmNIC.builder().simpleDCHPNic().build())
               .build();

       VM from = getHost().createVM(specification);

       return new NodeAndInitialCredentials<VM>(from, from.getUuid() + "", LoginCredentials.builder().user("smartos")
               .password("smartos").build());
   }

   @Override
   public Iterable<VmSpecification> listHardwareProfiles() {
       List<VmSpecification> specificationList = new ArrayList<VmSpecification>();

       VmSpecification vs = VmSpecification.builder()
               .alias("Standard Joyent VM")
               .nic(VmNIC.builder().simpleDCHPNic().build())
               .build();

       specificationList.add(vs);

       return specificationList;
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
   public Iterable<SmartOSHost> listLocations() {
       return ImmutableSet.of();
   }

   @Override
   public VM getNode(String id) {
      return getHost().getVM(UUID.fromString(id));
   }

   @Override
   public void destroyNode(String id) {
       getHost().getVM(UUID.fromString(id)).destroy();
   }

   @Override
   public void rebootNode(String id) {
       getHost().getVM(UUID.fromString(id)).reboot();
   }

   @Override
   public void resumeNode(String id) {
       getHost().getVM(UUID.fromString(id)).start();
   }

   @Override
   public void suspendNode(String id) {
       getHost().getVM(UUID.fromString(id)).stop();
   }
}