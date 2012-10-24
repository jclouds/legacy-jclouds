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

import static org.jclouds.abiquo.domain.DomainUtils.link;

import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.infrastructure.DatacenterDto;
import com.abiquo.server.core.infrastructure.LogicServerDto;
import com.abiquo.server.core.infrastructure.MachineDto;
import com.abiquo.server.core.infrastructure.OrganizationDto;
import com.abiquo.server.core.infrastructure.RackDto;
import com.abiquo.server.core.infrastructure.RemoteServiceDto;
import com.abiquo.server.core.infrastructure.UcsRackDto;
import com.abiquo.server.core.infrastructure.storage.StorageDeviceDto;
import com.abiquo.server.core.infrastructure.storage.StoragePoolDto;
import com.abiquo.server.core.infrastructure.storage.TierDto;

/**
 * Infrastructure domain utilities.
 * 
 * @author Ignasi Barrera
 */
public class InfrastructureResources {
   public static DatacenterDto datacenterPost() {
      DatacenterDto datacenter = new DatacenterDto();
      datacenter.setName("DC");
      datacenter.setLocation("Honolulu");
      return datacenter;
   }

   public static RackDto rackPost() {
      RackDto rack = new RackDto();
      rack.setName("Aloha");
      rack.setShortDescription("A hawaian rack");
      rack.setHaEnabled(false);
      rack.setVlanIdMin(6);
      rack.setVlanIdMax(3024);
      rack.setVlanPerVdcReserved(6);
      rack.setNrsq(80);
      return rack;
   }

   public static UcsRackDto managedRackPost() {
      UcsRackDto rack = new UcsRackDto();
      rack.setName("Aloha");
      rack.setShortDescription("A hawaian rack");
      rack.setHaEnabled(false);
      rack.setVlanIdMin(6);
      rack.setVlanIdMax(3024);
      rack.setVlanPerVdcReserved(6);
      rack.setNrsq(80);
      return rack;
   }

   public static MachineDto machinePost() {
      MachineDto machine = new MachineDto();
      machine.setName("Kamehameha");
      machine.setVirtualCpuCores(3);
      machine.setDescription("A hawaian machine");
      machine.setVirtualRamInMb(512);
      machine.setVirtualSwitch("192.168.1.10");
      return machine;
   }

   public static RemoteServiceDto remoteServicePost() {
      RemoteServiceDto remoteService = new RemoteServiceDto();
      remoteService.setType(RemoteServiceType.NODE_COLLECTOR);
      remoteService.setUri("http://localhost:80/nodecollector");
      remoteService.setStatus(0);
      return remoteService;
   }

   public static StorageDeviceDto storageDevicePost() {
      StorageDeviceDto storage = new StorageDeviceDto();
      storage.setName("Aloha aloha");
      storage.setIscsiIp("10.10.10.10");
      storage.setIscsiPort(99);
      storage.setManagementPort(90);

      return storage;
   }

   public static StoragePoolDto storagePoolPost() {
      StoragePoolDto storagePool = new StoragePoolDto();
      storagePool.setName("Hawaian Storage Pool");
      return storagePool;
   }

   public static DatacenterDto datacenterPut() {
      DatacenterDto datacenter = datacenterPost();
      datacenter.setId(1);
      datacenter.addLink(new RESTLink("checkmachinestate",
            "http://localhost/api/admin/datacenters/1/action/checkmachinestate"));
      datacenter.addLink(new RESTLink("checkmachineipmistate",
            "http://localhost/api/admin/datacenters/1/action/checkmachineipmistate"));
      datacenter.addLink(new RESTLink("checkremoteservice",
            "http://localhost/api/admin/datacenters/1/action/checkremoteservice"));
      datacenter.addLink(new RESTLink("devices", "http://localhost/api/admin/datacenters/1/storage/devices"));
      datacenter.addLink(new RESTLink("discovermultiple",
            "http://localhost/api/admin/datacenters/1/action/discovermultiple"));
      datacenter.addLink(new RESTLink("discoversingle",
            "http://localhost/api/admin/datacenters/1/action/discoversingle"));
      datacenter.addLink(new RESTLink("edit", "http://localhost/api/admin/datacenters/1"));
      datacenter.addLink(new RESTLink("getLimits", "http://localhost/api/admin/datacenters/1/action/getLimits"));
      datacenter.addLink(new RESTLink("racks", "http://localhost/api/admin/datacenters/1/racks"));
      datacenter.addLink(new RESTLink("remoteservices", "http://localhost/api/admin/datacenters/1/remoteservices"));
      datacenter.addLink(new RESTLink("tiers", "http://localhost/api/admin/datacenters/1/storage/tiers"));
      datacenter.addLink(new RESTLink("network", "http://localhost/api/admin/datacenters/1/network"));
      datacenter.addLink(new RESTLink("enterprises", "http://localhost/api/admin/datacenters/1/action/enterprises"));
      datacenter.addLink(new RESTLink("hypervisor", "http://localhost/api/admin/datacenters/1/action/hypervisor"));
      datacenter.addLink(new RESTLink("hypervisors", "http://localhost/api/admin/datacenters/1/hypervisors"));
      return datacenter;
   }

   public static RackDto rackPut() {
      RackDto rack = rackPost();
      rack.setId(1);
      rack.addLink(new RESTLink("datacenter", "http://localhost/api/admin/datacenters/1"));
      rack.addLink(new RESTLink("edit", "http://localhost/api/admin/datacenters/1/racks/1"));
      rack.addLink(new RESTLink("machines", "http://localhost/api/admin/datacenters/1/racks/1/machines"));
      return rack;
   }

   public static UcsRackDto managedRackPut() {
      UcsRackDto rack = managedRackPost();
      rack.setId(1);
      rack.addLink(new RESTLink("datacenter", "http://localhost/api/admin/datacenters/1"));
      rack.addLink(new RESTLink("edit", "http://localhost/api/admin/datacenters/1/racks/1"));
      rack.addLink(new RESTLink("fsm", "http://localhost/api/admin/datacenters/1/racks/1/fsm"));
      rack.addLink(new RESTLink("logicservers", "http://localhost/api/admin/datacenters/1/racks/1/logicservers"));
      rack.addLink(new RESTLink("ls-templates", "http://localhost/api/admin/datacenters/1/racks/1/lstemplates"));
      rack.addLink(new RESTLink("organizations", "http://localhost/api/admin/datacenters/1/racks/1/organizations"));
      rack.addLink(new RESTLink("ls-associate",
            "http://localhost/api/admin/datacenters/1/racks/1/logicservers/associate"));
      rack.addLink(new RESTLink("ls-associateclone",
            "http://localhost/api/admin/datacenters/1/racks/1/logicservers/assocclone"));
      rack.addLink(new RESTLink("ls-associatetemplate",
            "http://localhost/api/admin/datacenters/1/racks/1/logicservers/associatetemplate"));
      rack.addLink(new RESTLink("ls-clone", "http://localhost/api/admin/datacenters/1/racks/1/logicservers/clone"));
      rack.addLink(new RESTLink("ls-delete", "http://localhost/api/admin/datacenters/1/racks/1/logicservers/delete"));
      rack.addLink(new RESTLink("ls-dissociate",
            "http://localhost/api/admin/datacenters/1/racks/1/logicservers/dissociate"));
      return rack;
   }

   public static LogicServerDto logicServerPut() {
      LogicServerDto logicServer = new LogicServerDto();
      logicServer.setName("server");
      logicServer.setAssociated("associated");
      logicServer.setType("instance");

      return logicServer;
   }

   public static OrganizationDto organizationPut() {
      OrganizationDto org = new OrganizationDto();
      org.setName("org");
      org.setDn("org-root/org-Finance");
      org.setLevel("1");

      return org;
   }

   public static TierDto tierPut() {
      TierDto tier = new TierDto();
      tier.setId(1);
      tier.setEnabled(true);
      tier.setName("Tier");
      tier.addLink(new RESTLink("edit", "http://localhost/api/admin/datacenters/1/storage/tiers/1"));
      tier.addLink(new RESTLink("datacenter", "http://localhost/api/admin/datacenters/1"));
      tier.addLink(new RESTLink("pools", "http://localhost/api/admin/datacenters/1/storage/tiers/1/pools"));

      return tier;
   }

   public static StorageDeviceDto storageDevicePut() {
      StorageDeviceDto storageDevice = storageDevicePost();
      storageDevice.setId(1);
      storageDevice.addLink(new RESTLink("datacenter", "http://localhost/api/admin/datacenters/1"));
      storageDevice.addLink(new RESTLink("edit", "http://localhost/api/admin/datacenters/1/storage/devices/1"));
      storageDevice.addLink(new RESTLink("pools", "http://localhost/api/admin/datacenters/1/storage/devices/1/pools"));

      return storageDevice;
   }

   public static StoragePoolDto storagePoolPut() {
      StoragePoolDto storagePool = storagePoolPost();
      storagePool.setIdStorage("tururututu");
      storagePool.addLink(new RESTLink("device", "http://localhost/api/admin/datacenters/1/storage/devices/1"));
      storagePool.addLink(new RESTLink("edit",
            "http://localhost/api/admin/datacenters/1/storage/devices/1/pools/tururututu"));

      return storagePool;
   }

   public static RemoteServiceDto remoteServicePut() {
      RemoteServiceDto remoteService = remoteServicePost();
      remoteService.setId(1);
      remoteService.addLink(new RESTLink("check",
            "http://localhost/api/admin/datacenters/1/remoteservices/nodecollector/action/check"));
      remoteService.addLink(new RESTLink("datacenter", "http://localhost/api/admin/datacenters/1"));
      remoteService.addLink(new RESTLink("edit",
            "http://localhost/api/admin/datacenters/1/remoteservices/nodecollector"));
      return remoteService;
   }

   public static MachineDto machinePut() {
      MachineDto machine = machinePost();
      machine.setId(1);
      machine.addLink(new RESTLink("edit", "http://localhost/api/admin/datacenters/1/racks/1/machines/1"));
      machine.addLink(new RESTLink("rack", "http://localhost/api/admin/datacenters/1/racks/1"));
      machine.addLink(new RESTLink("checkstate",
            "http://localhost/api/admin/datacenters/1/racks/1/machines/1/action/checkstate"));
      machine.addLink(new RESTLink("checkipmistate",
            "http://localhost/api/admin/datacenters/1/racks/1/machines/1/action/checkipmistate"));
      machine.addLink(new RESTLink("led", "http://localhost/api/admin/datacenters/1/racks/1/machines/1/led"));
      machine.addLink(new RESTLink("ledoff",
            "http://localhost/api/admin/datacenters/1/racks/1/machines/1/action/ledoff"));
      machine
            .addLink(new RESTLink("ledon", "http://localhost/api/admin/datacenters/1/racks/1/machines/1/action/ledon"));
      machine.addLink(new RESTLink("logicserver",
            "http://localhost/api/admin/datacenters/1/racks/1/machines/1/logicserver"));
      machine.addLink(new RESTLink("poweroff",
            "http://localhost/api/admin/datacenters/1/racks/1/machines/1/action/poweroff"));
      machine.addLink(new RESTLink("poweron",
            "http://localhost/api/admin/datacenters/1/racks/1/machines/1/action/poweron"));
      machine.addLink(new RESTLink("virtualmachines",
            "http://localhost/api/admin/datacenters/1/racks/1/machines/1/virtualmachines"));
      machine.setVirtualCpuCores(5);

      return machine;
   }

   public static String datacenterPostPayload() {
      StringBuffer buffer = new StringBuffer();
      buffer.append("<datacenter>");
      buffer.append("<location>Honolulu</location>");
      buffer.append("<name>DC</name>");
      buffer.append("</datacenter>");
      return buffer.toString();
   }

   public static String rackPostPayload() {
      StringBuffer buffer = new StringBuffer();
      buffer.append("<rack>");
      buffer.append("<haEnabled>false</haEnabled>");
      buffer.append("<name>Aloha</name>");
      buffer.append("<nrsq>80</nrsq>");
      buffer.append("<shortDescription>A hawaian rack</shortDescription>");
      buffer.append("<vlanIdMax>3024</vlanIdMax>");
      buffer.append("<vlanIdMin>6</vlanIdMin>");
      buffer.append("<vlanPerVdcReserved>6</vlanPerVdcReserved>");
      buffer.append("</rack>");
      return buffer.toString();
   }

   public static String managedRackPostPayload() {
      StringBuffer buffer = new StringBuffer();
      buffer.append("<ucsrack>");
      buffer.append("<haEnabled>false</haEnabled>");
      buffer.append("<name>Aloha</name>");
      buffer.append("<nrsq>80</nrsq>");
      buffer.append("<shortDescription>A hawaian rack</shortDescription>");
      buffer.append("<vlanIdMax>3024</vlanIdMax>");
      buffer.append("<vlanIdMin>6</vlanIdMin>");
      buffer.append("<vlanPerVdcReserved>6</vlanPerVdcReserved>");
      buffer.append("</ucsrack>");
      return buffer.toString();
   }

   public static String storagePoolPostPayload() {
      StringBuffer buffer = new StringBuffer();
      buffer.append("<storagePool>");
      buffer.append("<availableSizeInMb>0</availableSizeInMb>");
      buffer.append("<enabled>false</enabled>");
      buffer.append("<name>Hawaian Storage Pool</name>");
      buffer.append("<totalSizeInMb>0</totalSizeInMb>");
      buffer.append("<usedSizeInMb>0</usedSizeInMb>");
      buffer.append("</storagePool>");
      return buffer.toString();
   }

   public static String storageDevicePostPayload() {
      StringBuffer buffer = new StringBuffer();
      buffer.append("<device>");
      buffer.append("<iscsiIp>10.10.10.10</iscsiIp>");
      buffer.append("<iscsiPort>99</iscsiPort>");
      buffer.append("<managementPort>90</managementPort>");
      buffer.append("<name>Aloha aloha</name>");
      buffer.append("</device>");
      return buffer.toString();
   }

   public static String machinePostPayload() {
      StringBuffer buffer = new StringBuffer();
      buffer.append("<machine>");
      buffer.append("<datastores/>");
      buffer.append("<description>A hawaian machine</description>");
      buffer.append("<name>Kamehameha</name>");
      buffer.append("<cpu>3</cpu>");
      buffer.append("<cpuUsed>1</cpuUsed>");
      buffer.append("<ram>512</ram>");
      buffer.append("<ramUsed>1</ramUsed>");
      buffer.append("<virtualSwitch>192.168.1.10</virtualSwitch>");
      buffer.append("</machine>");
      return buffer.toString();
   }

   public static String remoteServicePostPayload() {
      StringBuffer buffer = new StringBuffer();
      buffer.append("<remoteService>");
      buffer.append("<status>0</status>");
      buffer.append("<type>NODE_COLLECTOR</type>");
      buffer.append("<uri>http://localhost:80/nodecollector</uri>");
      buffer.append("</remoteService>");
      return buffer.toString();
   }

   public static String datacenterPutPayload() {
      StringBuffer buffer = new StringBuffer();
      buffer.append("<datacenter>");
      buffer.append(link("/admin/datacenters/1/action/checkmachinestate", "checkmachinestate"));
      buffer.append(link("/admin/datacenters/1/action/checkmachineipmistate", "checkmachineipmistate"));
      buffer.append(link("/admin/datacenters/1/action/checkremoteservice", "checkremoteservice"));
      buffer.append(link("/admin/datacenters/1/storage/devices", "devices"));
      buffer.append(link("/admin/datacenters/1/action/discovermultiple", "discovermultiple"));
      buffer.append(link("/admin/datacenters/1/action/discoversingle", "discoversingle"));
      buffer.append(link("/admin/datacenters/1", "edit"));
      buffer.append(link("/admin/datacenters/1/action/getLimits", "getLimits"));
      buffer.append(link("/admin/datacenters/1/racks", "racks"));
      buffer.append(link("/admin/datacenters/1/remoteservices", "remoteservices"));
      buffer.append(link("/admin/datacenters/1/storage/tiers", "tiers"));
      buffer.append(link("/admin/datacenters/1/network", "network"));
      buffer.append(link("/admin/datacenters/1/action/enterprises", "enterprises"));
      buffer.append(link("/admin/datacenters/1/action/hypervisor", "hypervisor"));
      buffer.append(link("/admin/datacenters/1/hypervisors", "hypervisors"));
      buffer.append("<id>1</id>");
      buffer.append("<location>Honolulu</location>");
      buffer.append("<name>DC</name>");
      buffer.append("</datacenter>");
      return buffer.toString();
   }

   public static String storagePoolPutPayload() {
      StringBuffer buffer = new StringBuffer();
      buffer.append("<storagePool>");
      buffer.append(link("/admin/datacenters/1/storage/devices/1", "device"));
      buffer.append(link("/admin/datacenters/1/storage/devices/1/pools/tururututu", "edit"));
      buffer.append("<availableSizeInMb>0</availableSizeInMb>");
      buffer.append("<enabled>false</enabled>");
      buffer.append("<idStorage>tururututu</idStorage>");
      buffer.append("<name>Hawaian Storage Pool</name>");
      buffer.append("<totalSizeInMb>0</totalSizeInMb>");
      buffer.append("<usedSizeInMb>0</usedSizeInMb>");
      buffer.append("</storagePool>");
      return buffer.toString();
   }

   public static String tierPutPayload() {
      StringBuffer buffer = new StringBuffer();
      buffer.append("<tier>");
      buffer.append(link("/admin/datacenters/1/storage/tiers/1", "edit"));
      buffer.append(link("/admin/datacenters/1", "datacenter"));
      buffer.append(link("/admin/datacenters/1/storage/tiers/1/pools", "pools"));
      buffer.append("<enabled>true</enabled>");
      buffer.append("<id>1</id>");
      buffer.append("<name>Tier</name>");
      buffer.append("</tier>");
      return buffer.toString();
   }

   public static String rackPutPayload() {
      StringBuffer buffer = new StringBuffer();
      buffer.append("<rack>");
      buffer.append(link("/admin/datacenters/1", "datacenter"));
      buffer.append(link("/admin/datacenters/1/racks/1", "edit"));
      buffer.append(link("/admin/datacenters/1/racks/1/machines", "machines"));
      buffer.append("<haEnabled>false</haEnabled>");
      buffer.append("<id>1</id>");
      buffer.append("<name>Aloha</name>");
      buffer.append("<nrsq>80</nrsq>");
      buffer.append("<shortDescription>A hawaian rack</shortDescription>");
      buffer.append("<vlanIdMax>3024</vlanIdMax>");
      buffer.append("<vlanIdMin>6</vlanIdMin>");
      buffer.append("<vlanPerVdcReserved>6</vlanPerVdcReserved>");
      buffer.append("</rack>");
      return buffer.toString();
   }

   public static String managedRackPutPayload() {
      StringBuffer buffer = new StringBuffer();
      buffer.append("<ucsrack>");
      buffer.append(link("/admin/datacenters/1", "datacenter"));
      buffer.append(link("/admin/datacenters/1/racks/1", "edit"));
      buffer.append(link("/admin/datacenters/1/racks/1/fsm", "fsm"));
      buffer.append(link("/admin/datacenters/1/racks/1/logicservers", "logicservers"));
      buffer.append(link("/admin/datacenters/1/racks/1/lstemplates", "ls-templates"));
      buffer.append(link("/admin/datacenters/1/racks/1/organizations", "organizations"));
      buffer.append(link("/admin/datacenters/1/racks/1/logicservers/associate", "ls-associate"));
      buffer.append(link("/admin/datacenters/1/racks/1/logicservers/assocclone", "ls-associateclone"));
      buffer.append(link("/admin/datacenters/1/racks/1/logicservers/associatetemplate", "ls-associatetemplate"));
      buffer.append(link("/admin/datacenters/1/racks/1/logicservers/clone", "ls-clone"));
      buffer.append(link("/admin/datacenters/1/racks/1/logicservers/delete", "ls-delete"));
      buffer.append(link("/admin/datacenters/1/racks/1/logicservers/dissociate", "ls-dissociate"));
      buffer.append("<haEnabled>false</haEnabled>");
      buffer.append("<id>1</id>");
      buffer.append("<name>Aloha</name>");
      buffer.append("<nrsq>80</nrsq>");
      buffer.append("<shortDescription>A hawaian rack</shortDescription>");
      buffer.append("<vlanIdMax>3024</vlanIdMax>");
      buffer.append("<vlanIdMin>6</vlanIdMin>");
      buffer.append("<vlanPerVdcReserved>6</vlanPerVdcReserved>");
      buffer.append("</ucsrack>");
      return buffer.toString();
   }

   public static String storageDevicePutPayload() {
      StringBuffer buffer = new StringBuffer();
      buffer.append("<device>");
      buffer.append(link("/admin/datacenters/1", "datacenter"));
      buffer.append(link("/admin/datacenters/1/storage/devices/1", "edit"));
      buffer.append(link("/admin/datacenters/1/storage/devices/1/pools", "pools"));
      buffer.append("<id>1</id>");
      buffer.append("<iscsiIp>10.10.10.10</iscsiIp>");
      buffer.append("<iscsiPort>99</iscsiPort>");
      buffer.append("<managementPort>90</managementPort>");
      buffer.append("<name>Aloha aloha</name>");
      buffer.append("</device>");
      return buffer.toString();
   }

   public static String remoteServicePutPayload() {
      StringBuffer buffer = new StringBuffer();
      buffer.append("<remoteService>");
      buffer.append(link("/admin/datacenters/1/remoteservices/nodecollector/action/check", "check"));
      buffer.append(link("/admin/datacenters/1", "datacenter"));
      buffer.append(link("/admin/datacenters/1/remoteservices/nodecollector", "edit"));
      buffer.append("<id>1</id>");
      buffer.append("<status>0</status>");
      buffer.append("<type>NODE_COLLECTOR</type>");
      buffer.append("<uri>http://localhost:80/nodecollector</uri>");
      buffer.append("</remoteService>");
      return buffer.toString();
   }

   public static String machinePutPayload() {
      StringBuffer buffer = new StringBuffer();
      buffer.append("<machine>");
      buffer.append(link("/admin/datacenters/1/racks/1/machines/1", "edit"));
      buffer.append(link("/admin/datacenters/1/racks/1", "rack"));
      buffer.append(link("/admin/datacenters/1/racks/1/machines/1/action/checkstate", "checkstate"));
      buffer.append(link("/admin/datacenters/1/racks/1/machines/1/action/checkipmistate", "checkipmistate"));
      buffer.append(link("/admin/datacenters/1/racks/1/machines/1/led", "led"));
      buffer.append(link("/admin/datacenters/1/racks/1/machines/1/action/ledoff", "ledoff"));
      buffer.append(link("/admin/datacenters/1/racks/1/machines/1/action/ledon", "ledon"));
      buffer.append(link("/admin/datacenters/1/racks/1/machines/1/logicserver", "logicserver"));
      buffer.append(link("/admin/datacenters/1/racks/1/machines/1/action/poweroff", "poweroff"));
      buffer.append(link("/admin/datacenters/1/racks/1/machines/1/action/poweron", "poweron"));
      buffer.append(link("/admin/datacenters/1/racks/1/machines/1/virtualmachines", "virtualmachines"));
      buffer.append("<datastores/>");
      buffer.append("<description>A hawaian machine</description>");
      buffer.append("<id>1</id>");
      buffer.append("<name>Kamehameha</name>");
      buffer.append("<cpu>5</cpu>");
      buffer.append("<cpuUsed>1</cpuUsed>");
      buffer.append("<ram>512</ram>");
      buffer.append("<ramUsed>1</ramUsed>");
      buffer.append("<virtualSwitch>192.168.1.10</virtualSwitch>");
      buffer.append("</machine>");
      return buffer.toString();
   }
}
