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

package org.jclouds.abiquo.domain;

import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.AbiquoAsyncApi;
import org.jclouds.rest.RestContext;

import com.abiquo.model.transport.SingleResourceWithLimitsDto;

/**
 * This class is used to decorate transport objects that have limits with high
 * level functionality.
 * 
 * @author Ignasi Barrera
 */
public abstract class DomainWithLimitsWrapper<T extends SingleResourceWithLimitsDto> extends DomainWrapper<T> {

   protected DomainWithLimitsWrapper(final RestContext<AbiquoApi, AbiquoAsyncApi> context, final T target) {
      super(context, target);
   }

   // Delegate methods

   public int getCpuCountHardLimit() {
      return target.getCpuCountHardLimit();
   }

   public int getCpuCountSoftLimit() {
      return target.getCpuCountSoftLimit();
   }

   public long getHdHardLimitInMb() {
      return target.getHdHardLimitInMb();
   }

   public long getHdSoftLimitInMb() {
      return target.getHdSoftLimitInMb();
   }

   public long getPublicIpsHard() {
      return target.getPublicIpsHard();
   }

   public long getPublicIpsSoft() {
      return target.getPublicIpsSoft();
   }

   public int getRamHardLimitInMb() {
      return target.getRamHardLimitInMb();
   }

   public int getRamSoftLimitInMb() {
      return target.getRamSoftLimitInMb();
   }

   public long getStorageHard() {
      return target.getStorageHard();
   }

   public long getStorageSoft() {
      return target.getStorageSoft();
   }

   public long getVlansHard() {
      return target.getVlansHard();
   }

   public long getVlansSoft() {
      return target.getVlansSoft();
   }

   public void setCpuCountHardLimit(final int cpuCountHardLimit) {
      target.setCpuCountHardLimit(cpuCountHardLimit);
   }

   public void setCpuCountLimits(final int softLimit, final int hardLimit) {
      target.setCpuCountLimits(softLimit, hardLimit);
   }

   public void setCpuCountSoftLimit(final int cpuCountSoftLimit) {
      target.setCpuCountSoftLimit(cpuCountSoftLimit);
   }

   public void setHdHardLimitInMb(final long hdHardLimitInMb) {
      target.setHdHardLimitInMb(hdHardLimitInMb);
   }

   public void setHdLimitsInMb(final long softLimit, final long hardLimit) {
      target.setHdLimitsInMb(softLimit, hardLimit);
   }

   public void setHdSoftLimitInMb(final long hdSoftLimitInMb) {
      target.setHdSoftLimitInMb(hdSoftLimitInMb);
   }

   public void setPublicIPLimits(final long softLimit, final long hardLimit) {
      target.setPublicIPLimits(softLimit, hardLimit);
   }

   public void setPublicIpsHard(final long publicIpsHard) {
      target.setPublicIpsHard(publicIpsHard);
   }

   public void setPublicIpsSoft(final long publicIpsSoft) {
      target.setPublicIpsSoft(publicIpsSoft);
   }

   public void setRamHardLimitInMb(final int ramHardLimitInMb) {
      target.setRamHardLimitInMb(ramHardLimitInMb);
   }

   public void setRamLimitsInMb(final int softLimit, final int hardLimit) {
      target.setRamLimitsInMb(softLimit, hardLimit);
   }

   public void setRamSoftLimitInMb(final int ramSoftLimitInMb) {
      target.setRamSoftLimitInMb(ramSoftLimitInMb);
   }

   public void setStorageHard(final long storageHard) {
      target.setStorageHard(storageHard);
   }

   public void setStorageLimits(final long softLimit, final long hardLimit) {
      target.setStorageLimits(softLimit, hardLimit);
   }

   public void setStorageSoft(final long storageSoft) {
      target.setStorageSoft(storageSoft);
   }

   public void setVlansHard(final long vlansHard) {
      target.setVlansHard(vlansHard);
   }

   public void setVlansLimits(final long softLimit, final long hardLimit) {
      target.setVlansLimits(softLimit, hardLimit);
   }

   public void setVlansSoft(final long vlansSoft) {
      target.setVlansSoft(vlansSoft);
   }
}
