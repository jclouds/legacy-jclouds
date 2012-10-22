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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.filter;

import java.util.List;

import org.jclouds.abiquo.AbiquoAsyncApi;
import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.domain.DomainWrapper;
import org.jclouds.abiquo.domain.infrastructure.options.StoragePoolOptions;
import org.jclouds.abiquo.reference.ValidationErrors;
import org.jclouds.abiquo.reference.annotations.EnterpriseEdition;
import org.jclouds.abiquo.reference.rest.ParentLinkName;
import org.jclouds.rest.RestContext;

import com.abiquo.server.core.infrastructure.DatacenterDto;
import com.abiquo.server.core.infrastructure.storage.StorageDeviceDto;
import com.abiquo.server.core.infrastructure.storage.StoragePoolDto;
import com.abiquo.server.core.infrastructure.storage.StoragePoolsDto;
import com.abiquo.server.core.infrastructure.storage.TiersDto;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Adds high level functionality to {@link StorageDeviceDto}. The Storage Device
 * Resource offers the functionality of managing the external storage.
 * 
 * @author Ignasi Barrera
 * @author Francesc Montserrat
 * @see API: <a
 *      href="http://community.abiquo.com/display/ABI20/StorageDeviceResource">
 *      http://community.abiquo.com/display/ABI20/StorageDeviceResource</a>
 */
@EnterpriseEdition
public class StorageDevice extends DomainWrapper<StorageDeviceDto> {
   /** The datacenter where the storage device is. */
   private Datacenter datacenter;

   /**
    * Constructor to be used only by the builder.
    */
   protected StorageDevice(final RestContext<AbiquoApi, AbiquoAsyncApi> context, final StorageDeviceDto target) {
      super(context, target);
   }

   /**
    * Delete the storage device.
    * 
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/StorageDeviceResource#StorageDeviceResource-Deleteastoragedevice"
    *      > http://community.abiquo.com/display/ABI20/StorageDeviceResource#
    *      StorageDeviceResource- Deleteastoragedevice</a>
    */
   public void delete() {
      context.getApi().getInfrastructureApi().deleteStorageDevice(target);
      target = null;
   }

   /**
    * Create a new storage device.
    * 
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/StorageDeviceResource#StorageDeviceResource-Createastoragedevice"
    *      > http://community.abiquo.com/display/ABI20/StorageDeviceResource#
    *      StorageDeviceResource- Createastoragedevice</a>
    */
   public void save() {
      target = context.getApi().getInfrastructureApi().createStorageDevice(datacenter.unwrap(), target);
   }

   /**
    * Update storage device information in the server with the data from this
    * device.
    * 
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/StorageDeviceResource#StorageDeviceResource-Updateastoragedevice"
    *      > http://community.abiquo.com/display/ABI20/StorageDeviceResource#
    *      StorageDeviceResource- Updateastoragedevice</a>
    */
   public void update() {
      target = context.getApi().getInfrastructureApi().updateStorageDevice(target);
   }

   // Parent access

   /**
    * Retrieve the datacenter where this storage device is.
    * 
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/DatacenterResource#DatacenterResource-Retrieveadatacenter"
    *      > http://community.abiquo.com/display/ABI20/DatacenterResource#
    *      DatacenterResource- Retrieveadatacenter</a>
    */
   public Datacenter getDatacenter() {
      Integer datacenterId = target.getIdFromLink(ParentLinkName.DATACENTER);
      DatacenterDto dto = context.getApi().getInfrastructureApi().getDatacenter(datacenterId);
      datacenter = wrap(context, Datacenter.class, dto);
      return datacenter;
   }

   // Children access

   /**
    * Retrieve the list of storage pools in this device (synchronized with the
    * device).
    * 
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/StoragePoolResource#StoragePoolResource-Retrievestoragepools"
    *      > http://community.abiquo.com/display/ABI20/StoragePoolResource#
    *      StoragePoolResource- Retrievestoragepools</a>
    * @return Synchronized list of storage pools in this device.
    */
   public List<StoragePool> listRemoteStoragePools() {
      StoragePoolsDto storagePools = context.getApi().getInfrastructureApi()
            .listStoragePools(target, StoragePoolOptions.builder().sync(true).build());

      List<StoragePool> storagePoolList = wrap(context, StoragePool.class, storagePools.getCollection());

      for (StoragePool storagePool : storagePoolList) {
         storagePool.storageDevice = this;
      }

      return storagePoolList;
   }

   /**
    * Retrieve a filtered list of storage pools in this device (synchronized
    * with the device).
    * 
    * @param filter
    *           Filter to be applied to the list.
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/StoragePoolResource#StoragePoolResource-Retrievestoragepools"
    *      > http://community.abiquo.com/display/ABI20/StoragePoolResource#
    *      StoragePoolResource- Retrievestoragepools</a>
    * @return Filtered synchronized list of storage pools in this device.
    */
   public List<StoragePool> listRemoteStoragePools(final Predicate<StoragePool> filter) {
      return Lists.newLinkedList(filter(listRemoteStoragePools(), filter));
   }

   /**
    * Retrieve the first storage pool matching the filter within the list of
    * storage pools in this device (synchronized with the device).
    * 
    * @param filter
    *           Filter to be applied to the list.
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/StoragePoolResource#StoragePoolResource-Retrievestoragepools"
    *      > http://community.abiquo.com/display/ABI20/StoragePoolResource#
    *      StoragePoolResource- Retrievestoragepools</a>
    * @return First storage pool (synchronized) matching the filter or
    *         <code>null</code> if there is none.
    */
   public StoragePool findRemoteStoragePool(final Predicate<StoragePool> filter) {
      return Iterables.getFirst(filter(listRemoteStoragePools(), filter), null);
   }

   /**
    * Retrieve the list of storage pools in this device from Abiquo database
    * (may not be synchronized with the device).
    * 
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/StoragePoolResource#StoragePoolResource-Retrievestoragepools"
    *      > http://community.abiquo.com/display/ABI20/StoragePoolResource#
    *      StoragePoolResource- Retrievestoragepools</a>
    * @return Unsynchronized list of storage pools in this device.
    */
   public List<StoragePool> listStoragePools() {
      StoragePoolsDto storagePools = context.getApi().getInfrastructureApi()
            .listStoragePools(target, StoragePoolOptions.builder().sync(false).build());
      return wrap(context, StoragePool.class, storagePools.getCollection());
   }

   /**
    * Retrieve a filtered list of storage pools in this device from Abiquo
    * database (may not be synchronized with the device).
    * 
    * @param filter
    *           Filter to be applied to the list.
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/StoragePoolResource#StoragePoolResource-Retrievestoragepools"
    *      > http://community.abiquo.com/display/ABI20/StoragePoolResource#
    *      StoragePoolResource- Retrievestoragepools</a>
    * @return Filtered unsynchronized list of storage pools in this device.
    */
   public List<StoragePool> listStoragePools(final Predicate<StoragePool> filter) {
      return Lists.newLinkedList(filter(listStoragePools(), filter));
   }

   /**
    * Retrieve the first storage pool matching the filter within the list of
    * storage pools in this device (unsynchronized with the device).
    * 
    * @param filter
    *           Filter to be applied to the list.
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/StoragePoolResource#StoragePoolResource-Retrievestoragepools"
    *      > http://community.abiquo.com/display/ABI20/StoragePoolResource#
    *      StoragePoolResource- Retrievestoragepools</a>
    * @return First storage pool (unsynchronized) matching the filter or
    *         <code>null</code> if there is none.
    */
   public StoragePool findStoragePool(final Predicate<StoragePool> filter) {
      return Iterables.getFirst(filter(listStoragePools(), filter), null);
   }

   /**
    * Retrieve a single storage pool in this device from Abiquo database.
    * 
    * @param id
    *           Unique ID of the storage device in this datacenter.
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/StoragePoolResource#StoragePoolResource-Retrievearegisteredpool"
    *      > http://community.abiquo.com/display/ABI20/StoragePoolResource#
    *      StoragePoolResource- Retrievearegisteredpool</a>
    * @return Storage pool with the given id or <code>null</code> if it does not
    *         exist.
    */
   public StoragePool getStoragePool(final String id) {
      StoragePoolDto storagePool = context.getApi().getInfrastructureApi().getStoragePool(target, id);
      return wrap(context, StoragePool.class, storagePool);
   }

   /**
    * Retrieve the list of tiers in the datacenter using this device.
    * 
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/TierResource#TierResource-Retrievethelistoftiers"
    *      >
    *      http://community.abiquo.com/display/ABI20/TierResource#TierResource-
    *      Retrievethelistoftiers </a>
    * @return List of tiers in the datacenter using this device.
    */
   public List<Tier> listTiersFromDatacenter() {
      DatacenterDto datacenter;

      if (this.datacenter == null) {
         datacenter = new DatacenterDto();
         datacenter.setId(target.getIdFromLink(ParentLinkName.DATACENTER));
      } else {
         datacenter = this.getDatacenter().unwrap();
      }

      TiersDto dto = context.getApi().getInfrastructureApi().listTiers(datacenter);
      return DomainWrapper.wrap(context, Tier.class, dto.getCollection());
   }

   /**
    * Retrieve a filtered list of tiers in the datacenter using this device.
    * 
    * @param filter
    *           Filter to be applied to the list.
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/TierResource#TierResource-Retrievethelistoftiers"
    *      >
    *      http://community.abiquo.com/display/ABI20/TierResource#TierResource-
    *      Retrievethelistoftiers </a>
    * @return Filtered list of tiers in the datacenter using this device.
    */
   public List<Tier> listTiersFromDatacenter(final Predicate<Tier> filter) {
      return Lists.newLinkedList(filter(listTiersFromDatacenter(), filter));
   }

   /**
    * Retrieve the first tier matching the filter within the list of tiers in
    * the datacenter.
    * 
    * @param filter
    *           Filter to be applied to the list.
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/StorageDeviceResource#StorageDeviceResource-Retrievethelistofstoragedevices"
    *      > http://community.abiquo.com/display/ABI20/StorageDeviceResource#
    *      StorageDeviceResource- Retrievethelistofstoragedevices</a>
    * @return First tier matching the filter or <code>null</code> if there is
    *         none.
    */
   public Tier findTierInDatacenter(final Predicate<Tier> filter) {
      return Iterables.getFirst(filter(listTiersFromDatacenter(), filter), null);
   }

   public static Builder builder(final RestContext<AbiquoApi, AbiquoAsyncApi> context, final Datacenter datacenter) {
      return new Builder(context, datacenter);
   }

   public static class Builder {
      private RestContext<AbiquoApi, AbiquoAsyncApi> context;

      private Datacenter datacenter;

      private String iscsiIp;

      private Integer iscsiPort;

      private String managementIp;

      private Integer managementPort;

      private String name;

      private String password;

      private String type;

      private String username;

      public Builder(final RestContext<AbiquoApi, AbiquoAsyncApi> context, final Datacenter datacenter) {
         super();
         checkNotNull(datacenter, ValidationErrors.NULL_RESOURCE + Datacenter.class);
         this.datacenter = datacenter;
         this.context = context;
      }

      public Builder datacenter(final Datacenter datacenter) {
         checkNotNull(datacenter, ValidationErrors.NULL_RESOURCE + Datacenter.class);
         this.datacenter = datacenter;
         return this;
      }

      public Builder iscsiIp(final String iscsiIp) {
         this.iscsiIp = iscsiIp;
         return this;
      }

      public Builder iscsiPort(final int iscsiPort) {
         this.iscsiPort = iscsiPort;
         return this;
      }

      public Builder password(final String password) {
         this.password = password;
         return this;
      }

      public Builder name(final String name) {
         this.name = name;
         return this;
      }

      public Builder managementPort(final int managementPort) {
         this.managementPort = managementPort;
         return this;
      }

      public Builder managementIp(final String managementIp) {
         this.managementIp = managementIp;
         return this;
      }

      public Builder type(final String type) {
         this.type = type;
         return this;
      }

      public Builder username(final String username) {
         this.username = username;
         return this;
      }

      public StorageDevice build() {
         StorageDeviceDto dto = new StorageDeviceDto();
         dto.setIscsiIp(iscsiIp);
         dto.setIscsiPort(iscsiPort);
         dto.setManagementIp(managementIp);
         dto.setManagementPort(managementPort);
         dto.setName(name);
         dto.setPassword(password);
         dto.setStorageTechnology(type);
         dto.setUsername(username);
         StorageDevice storageDevice = new StorageDevice(context, dto);
         storageDevice.datacenter = datacenter;
         return storageDevice;
      }

      public static Builder fromStorageDevice(final StorageDevice in) {
         Builder builder = StorageDevice.builder(in.context, in.getDatacenter()).iscsiIp(in.getIscsiIp())
               .iscsiPort(in.getIscsiPort()).managementIp(in.getManagementIp()).managementPort(in.getManagementPort())
               .name(in.getName()).password(in.getPassword()).type(in.getType()).username(in.getUsername());

         return builder;
      }
   }

   // Delegate methods

   public Integer getId() {
      return target.getId();
   }

   public String getIscsiIp() {
      return target.getIscsiIp();
   }

   public int getIscsiPort() {
      return target.getIscsiPort();
   }

   public String getManagementIp() {
      return target.getManagementIp();
   }

   public int getManagementPort() {
      return target.getManagementPort();
   }

   public String getName() {
      return target.getName();
   }

   public String getPassword() {
      return target.getPassword();
   }

   public String getType() {
      return target.getStorageTechnology();
   }

   public String getUsername() {
      return target.getUsername();
   }

   public void setIscsiIp(final String iscsiIp) {
      target.setIscsiIp(iscsiIp);
   }

   public void setIscsiPort(final int iscsiPort) {
      target.setIscsiPort(iscsiPort);
   }

   public void setManagementIp(final String managementIp) {
      target.setManagementIp(managementIp);
   }

   public void setManagementPort(final int managementPort) {
      target.setManagementPort(managementPort);
   }

   public void setName(final String name) {
      target.setName(name);
   }

   public void setPassword(final String password) {
      target.setPassword(password);
   }

   public void setType(final String type) {
      target.setStorageTechnology(type);
   }

   public void setUsername(final String username) {
      target.setUsername(username);
   }

   @Override
   public String toString() {
      return "StorageDevice [id=" + getId() + ", iscsiIp=" + getIscsiIp() + ", iscsiPort=" + getIscsiPort()
            + ", managementIp=" + getManagementIp() + ", managementPort=" + getManagementPort() + ", name=" + getName()
            + ", password=" + getPassword() + ", type=" + getType() + ", user=" + getUsername() + "]";
   }

}
