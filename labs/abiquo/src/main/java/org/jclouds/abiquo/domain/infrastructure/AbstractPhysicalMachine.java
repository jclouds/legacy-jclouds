/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.domain.infrastructure;

import static com.google.common.collect.Iterables.find;

import java.util.List;

import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.AbiquoAsyncApi;
import org.jclouds.abiquo.domain.DomainWrapper;
import org.jclouds.abiquo.predicates.infrastructure.DatastorePredicates;
import org.jclouds.abiquo.predicates.infrastructure.NetworkInterfacePredicates;
import org.jclouds.rest.RestContext;

import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.model.enumerator.MachineIpmiState;
import com.abiquo.model.enumerator.MachineState;
import com.abiquo.server.core.infrastructure.DatastoresDto;
import com.abiquo.server.core.infrastructure.MachineDto;
import com.abiquo.server.core.infrastructure.MachineIpmiStateDto;
import com.abiquo.server.core.infrastructure.MachineStateDto;

/**
 * Adds high level functionality to {@link MachineDto}. This class defines
 * common methods for unmanaged {@link Machine} and managed {@link Blade}
 * physical machines.
 * 
 * @author Ignasi Barrera
 * @author Francesc Montserrat
 * @see API: <a
 *      href="http://community.abiquo.com/display/ABI20/MachineResource">
 *      http://community.abiquo.com/display/ABI20/MachineResource</a>
 */
public abstract class AbstractPhysicalMachine extends DomainWrapper<MachineDto> {
   /** The default virtual ram used in MB. */
   protected static final int DEFAULT_VRAM_USED = 1;

   /** The default virtual cpu used in MB. */
   protected static final int DEFAULT_VCPU_USED = 1;

   /**
    * Constructor to be used only by the builder.
    */
   protected AbstractPhysicalMachine(final RestContext<AbiquoApi, AbiquoAsyncApi> context, final MachineDto target) {
      super(context, target);
   }

   public void delete() {
      context.getApi().getInfrastructureApi().deleteMachine(target);
      target = null;
   }

   public void update() {
      target = context.getApi().getInfrastructureApi().updateMachine(target);
   }

   public MachineState check() {
      MachineStateDto dto = context.getApi().getInfrastructureApi().checkMachineState(target, true);
      MachineState state = dto.getState();
      target.setState(state);
      return state;
   }

   public MachineIpmiState checkIpmi() {
      MachineIpmiStateDto dto = context.getApi().getInfrastructureApi().checkMachineIpmiState(target);
      return dto.getState();
   }

   // Children access

   public List<Datastore> getDatastores() {
      return wrap(context, Datastore.class, target.getDatastores().getCollection());
   }

   public Datastore findDatastore(final String name) {
      return find(getDatastores(), DatastorePredicates.name(name), null);
   }

   public List<NetworkInterface> getNetworkInterfaces() {
      return wrap(context, NetworkInterface.class, target.getNetworkInterfaces().getCollection());
   }

   public NetworkInterface findNetworkInterface(final String name) {
      return find(getNetworkInterfaces(), NetworkInterfacePredicates.name(name), null);
   }

   // Delegate methods

   public Integer getId() {
      return target.getId();
   }

   public String getIp() {
      return target.getIp();
   }

   public String getIpmiIp() {
      return target.getIpmiIP();
   }

   public String getIpmiPassword() {
      return target.getIpmiPassword();
   }

   public Integer getIpmiPort() {
      return target.getIpmiPort();
   }

   public String getIpmiUser() {
      return target.getIpmiUser();
   }

   public String getIpService() {
      return target.getIpService();
   }

   public String getName() {
      return target.getName();
   }

   public String getPassword() {
      return target.getPassword();
   }

   public Integer getPort() {
      return target.getPort();
   }

   public MachineState getState() {
      return target.getState();
   }

   public HypervisorType getType() {
      return target.getType();
   }

   public String getUser() {
      return target.getUser();
   }

   public Integer getVirtualCpuCores() {
      return target.getVirtualCpuCores();
   }

   public Integer getVirtualCpusUsed() {
      return target.getVirtualCpusUsed();
   }

   public Integer getVirtualRamInMb() {
      return target.getVirtualRamInMb();
   }

   public Integer getVirtualRamUsedInMb() {
      return target.getVirtualRamUsedInMb();
   }

   public void setDatastores(final List<Datastore> datastores) {
      DatastoresDto datastoresDto = new DatastoresDto();
      datastoresDto.getCollection().addAll(DomainWrapper.unwrap(datastores));
      target.setDatastores(datastoresDto);
   }

   public void setDescription(final String description) {
      target.setDescription(description);
   }

   public void setIp(final String ip) {
      target.setIp(ip);
   }

   public void setIpmiIp(final String ipmiIp) {
      target.setIpmiIP(ipmiIp);
   }

   public void setIpmiPassword(final String ipmiPassword) {
      target.setIpmiPassword(ipmiPassword);
   }

   public void setIpmiPort(final Integer ipmiPort) {
      target.setIpmiPort(ipmiPort);
   }

   public void setIpmiUser(final String ipmiUser) {
      target.setIpmiUser(ipmiUser);
   }

   public void setIpService(final String ipService) {
      target.setIpService(ipService);
   }

   public void setName(final String name) {
      target.setName(name);
   }

   public void setPassword(final String password) {
      target.setPassword(password);
   }

   public void setPort(final Integer port) {
      target.setPort(port);
   }

   public void setState(final MachineState state) {
      target.setState(state);
   }

   public void setType(final HypervisorType type) {
      target.setType(type);
   }

   public void setUser(final String user) {
      target.setUser(user);
   }

   public void setVirtualCpuCores(final Integer virtualCpuCores) {
      target.setVirtualCpuCores(virtualCpuCores);
   }

   public void setVirtualCpusUsed(final Integer virtualCpusUsed) {
      target.setVirtualCpusUsed(virtualCpusUsed);
   }

   public void setVirtualRamInMb(final Integer virtualRamInMb) {
      target.setVirtualRamInMb(virtualRamInMb);
   }

   public void setVirtualRamUsedInMb(final Integer virtualRamUsedInMb) {
      target.setVirtualRamUsedInMb(virtualRamUsedInMb);
   }

   public String getDescription() {
      return target.getDescription();
   }

   // Aux operations

   public NetworkInterface findAvailableVirtualSwitch(final String virtualswitch) {
      return find(getNetworkInterfaces(), NetworkInterfacePredicates.name(virtualswitch));
   }

   @Override
   public String toString() {
      return "Machine [id=" + getId() + ", ip=" + getIp() + ", ipmiIp=" + getIpmiIp() + ", ipmiPassword="
            + getIpmiPassword() + ", ipmiPort=" + getIpmiPort() + ", ipmiUser=" + getIpmiUser() + ", ipService="
            + getIpService() + ", name=" + getName() + ", password=" + getPassword() + ", port=" + getPort()
            + ", state=" + getState() + ", type=" + getType() + ", user=" + getUser() + ", virtualCpuCores="
            + getVirtualCpuCores() + ", virtualCpusUsed=" + getVirtualCpusUsed() + ", getVirtualRamInMb()="
            + getVirtualRamInMb() + ", virtualRamUsedInMb=" + getVirtualRamUsedInMb() + ", description="
            + getDescription() + "]";
   }

}
