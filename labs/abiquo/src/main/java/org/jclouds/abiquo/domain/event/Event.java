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

package org.jclouds.abiquo.domain.event;

import java.util.Date;

import org.jclouds.abiquo.AbiquoAsyncApi;
import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.domain.DomainWrapper;
import org.jclouds.rest.RestContext;

import com.abiquo.model.enumerator.SeverityType;
import com.abiquo.server.core.event.EventDto;

/**
 * @author Vivien Mahé
 */
public class Event extends DomainWrapper<EventDto> {
   /**
    * Constructor to be used only by the builder.
    */
   protected Event(final RestContext<AbiquoApi, AbiquoAsyncApi> context, final EventDto target) {
      super(context, target);
   }

   // Delegate methods

   public Integer getId() {
      return target.getId();
   }

   public String getUser() {
      return target.getUser();
   }

   public void setUser(final String user) {
      target.setUser(user);
   }

   public String getStacktrace() {
      return target.getStacktrace();
   }

   public void setStacktrace(final String stacktrace) {
      target.setStacktrace(stacktrace);
   }

   public String getComponent() {
      return target.getComponent();
   }

   public void setComponent(final String component) {
      target.setComponent(component);
   }

   public String getPerformedBy() {
      return target.getPerformedBy();
   }

   public void setPerformedBy(final String performedBy) {
      target.setPerformedBy(performedBy);
   }

   public Integer getIdNetwork() {
      return target.getIdNetwork();
   }

   public void setIdNetwork(final Integer idNetwork) {
      target.setIdNetwork(idNetwork);
   }

   public String getIdVolume() {
      return target.getIdVolume();
   }

   public void setIdVolume(final String idVolume) {
      target.setIdVolume(idVolume);
   }

   public String getStoragePool() {
      return target.getStoragePool();
   }

   public void setStoragePool(final String storagePool) {
      target.setStoragePool(storagePool);
   }

   public Date getTimestamp() {
      return target.getTimestamp();
   }

   public void setTimestamp(final Date timestamp) {
      target.setTimestamp(timestamp);
   }

   public String getVirtualApp() {
      return target.getVirtualApp();
   }

   public void setVirtualApp(final String virtualApp) {
      target.setVirtualApp(virtualApp);
   }

   public String getDatacenter() {
      return target.getDatacenter();
   }

   public void setDatacenter(final String datacenter) {
      target.setDatacenter(datacenter);
   }

   public String getActionPerformed() {
      return target.getActionPerformed();
   }

   public void setActionPerformed(final String actionPerformed) {
      target.setActionPerformed(actionPerformed);
   }

   public Integer getIdVirtualMachine() {
      return target.getIdVirtualMachine();
   }

   public void setIdVirtualMachine(final Integer idVirtualMachine) {
      target.setIdVirtualMachine(idVirtualMachine);
   }

   public String getVirtualDatacenter() {
      return target.getVirtualDatacenter();
   }

   public void setVirtualDatacenter(final String virtualDatacenter) {
      target.setVirtualDatacenter(virtualDatacenter);
   }

   public String getEnterprise() {
      return target.getEnterprise();
   }

   public void setEnterprise(final String enterprise) {
      target.setEnterprise(enterprise);
   }

   public String getStorageSystem() {
      return target.getStorageSystem();
   }

   public void setStorageSystem(final String storageSystem) {
      target.setStorageSystem(storageSystem);
   }

   public Integer getIdPhysicalMachine() {
      return target.getIdPhysicalMachine();
   }

   public void setIdPhysicalMachine(final Integer idPhysicalMachine) {
      target.setIdPhysicalMachine(idPhysicalMachine);
   }

   public SeverityType getSeverity() {
      return target.getSeverity();
   }

   public void setSeverity(final SeverityType severity) {
      target.setSeverity(severity);
   }

   public Integer getIdStorageSystem() {
      return target.getIdStorageSystem();
   }

   public void setIdStorageSystem(final Integer idStorageSystem) {
      target.setIdStorageSystem(idStorageSystem);
   }

   public Integer getIdDatacenter() {
      return target.getIdDatacenter();
   }

   public void setIdDatacenter(final Integer idDatacenter) {
      target.setIdDatacenter(idDatacenter);
   }

   public String getNetwork() {
      return target.getNetwork();
   }

   public void setNetwork(final String network) {
      target.setNetwork(network);
   }

   public String getPhysicalMachine() {
      return target.getPhysicalMachine();
   }

   public void setPhysicalMachine(final String physicalMachine) {
      target.setPhysicalMachine(physicalMachine);
   }

   public String getRack() {
      return target.getRack();
   }

   public void setRack(final String rack) {
      target.setRack(rack);
   }

   public Integer getIdVirtualDatacenter() {
      return target.getIdVirtualDatacenter();
   }

   public void setIdVirtualDatacenter(final Integer idVirtualDatacenter) {
      target.setIdVirtualDatacenter(idVirtualDatacenter);
   }

   public Integer getIdSubnet() {
      return target.getIdSubnet();
   }

   public void setIdSubnet(final Integer idSubnet) {
      target.setIdSubnet(idSubnet);
   }

   public String getVolume() {
      return target.getVolume();
   }

   public void setVolume(final String volume) {
      target.setVolume(volume);
   }

   public String getSubnet() {
      return target.getSubnet();
   }

   public void setSubnet(final String subnet) {
      target.setSubnet(subnet);
   }

   public Integer getIdUser() {
      return target.getIdUser();
   }

   public void setIdUser(final Integer idUser) {
      target.setIdUser(idUser);
   }

   public String getIdStoragePool() {
      return target.getIdStoragePool();
   }

   public void setIdStoragePool(final String idStoragePool) {
      target.setIdStoragePool(idStoragePool);
   }

   public Integer getIdRack() {
      return target.getIdRack();
   }

   public void setIdRack(final Integer idRack) {
      target.setIdRack(idRack);
   }

   public String getVirtualMachine() {
      return target.getVirtualMachine();
   }

   public void setVirtualMachine(final String virtualMachine) {
      target.setVirtualMachine(virtualMachine);
   }

   public Integer getIdVirtualApp() {
      return target.getIdVirtualApp();
   }

   public void setIdVirtualApp(final Integer idVirtualApp) {
      target.setIdVirtualApp(idVirtualApp);
   }

   public Integer getIdEnterprise() {
      return target.getIdEnterprise();
   }

   public void setIdEnterprise(final Integer idEnterprise) {
      target.setIdEnterprise(idEnterprise);
   }

   @Override
   public String toString() {
      return "Event [id=" + getId() + ", idUser=" + getIdUser() + ", user=" + getUser() + ", idEnterprise="
            + getIdEnterprise() + ", enterprise=" + getEnterprise() + ", actionPerformed=" + getActionPerformed()
            + ", component=" + getComponent() + ", idDatacenter=" + getIdDatacenter() + ", datacenter="
            + getDatacenter() + ", idStoragePool=" + getIdStoragePool() + ", storagePool=" + getStoragePool()
            + ", idVolume=" + getIdVolume() + ", volume=" + getVolume() + ", idNetwork=" + getIdNetwork()
            + ", network=" + getNetwork() + ", idPhysicalMachine=" + getIdPhysicalMachine() + ", physicalMachine="
            + getPhysicalMachine() + ", idRack=" + getIdRack() + ", rack=" + getRack() + ", idStorageSystem="
            + getIdStorageSystem() + ", storageSystem=" + getStorageSystem() + ", idSubnet=" + getIdSubnet()
            + ", subnet=" + getSubnet() + ", idVirtualApp=" + getIdVirtualApp() + ", virtualApp=" + getVirtualApp()
            + ", idVirtualDatacenter=" + getIdVirtualDatacenter() + ", virtualDatacenter=" + getVirtualDatacenter()
            + ", idVirtualMachine=" + getIdVirtualMachine() + ", virtualMachine=" + getVirtualMachine()
            + ", stackstrace=" + getStacktrace() + ", performedBy=" + getPerformedBy() + ", severity=" + getSeverity()
            + "]";
   }
}
