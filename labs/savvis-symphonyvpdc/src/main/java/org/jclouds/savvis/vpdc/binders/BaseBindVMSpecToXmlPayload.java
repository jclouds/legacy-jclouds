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
package org.jclouds.savvis.vpdc.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.ws.rs.core.MediaType;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.jclouds.cim.ResourceAllocationSettingData.ResourceType;
import org.jclouds.compute.domain.CIMOperatingSystem;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.MapBinder;
import org.jclouds.rest.binders.BindToStringPayload;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.savvis.vpdc.domain.VMSpec;

import com.google.common.base.Throwables;
import com.jamesmurty.utils.XMLBuilder;

/**
 * 
 * @author Adrian Cole
 * 
 */
public abstract class BaseBindVMSpecToXmlPayload<T> extends BindToStringPayload implements MapBinder {
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object toBind) {
      throw new IllegalStateException("BindVMSpecToXmlPayload needs parameters");
   }

   protected abstract T findSpecInArgsOrNull(GeneratedHttpRequest gRequest);

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      checkArgument(checkNotNull(request, "request") instanceof GeneratedHttpRequest,
               "this binder is only valid for GeneratedHttpRequests!");
      GeneratedHttpRequest gRequest = (GeneratedHttpRequest) request;
      request = super.bindToRequest(request, generateXml(findSpecInArgsOrNull(gRequest)));
      request.getPayload().getContentMetadata().setContentType(MediaType.APPLICATION_XML);
      return request;
   }

   public String generateXml(T spec) {
      try {
         XMLBuilder rootBuilder = buildRoot();
         bindSpec(spec, rootBuilder);
         Properties outputProperties = new Properties();
         outputProperties.put(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");
         return rootBuilder.asString(outputProperties);
      } catch (Exception e) {
         throw Throwables.propagate(e);
      }
   }

   protected abstract void bindSpec(T spec, XMLBuilder rootBuilder) throws ParserConfigurationException,
            FactoryConfigurationError;

   protected void checkSpec(VMSpec spec) {
      checkNotNull(spec, "VMSpec");
      checkNotNull(spec.getName(), "name");
      checkNotNull(spec.getNetworkTierName(), "networkTierName");
   }

   protected void addOperatingSystemAndVirtualHardware(VMSpec spec, XMLBuilder vAppBuilder) {
      addOperatingSystemSection(vAppBuilder, spec.getOperatingSystem());
      // TODO: Savvis returns network names with a - instead of space on getNetworkInVDC
      // call,
      // fix this once savvis api starts returning correctly
      addVirtualHardwareSection(vAppBuilder, spec.getName(), spec.getNetworkTierName().replace("-", " "), spec);
   }

   void addVirtualHardwareSection(XMLBuilder rootBuilder, String name, String networkName, VMSpec spec) {
      XMLBuilder virtualHardwareSectionBuilder = rootBuilder.e("ovf:VirtualHardwareSection");
      virtualHardwareSectionBuilder.e("ovf:Info").t("Virtual Hardware");
      addSystem(virtualHardwareSectionBuilder, name);
      addItems(virtualHardwareSectionBuilder, spec, networkName);
   }

   void addItems(XMLBuilder virtualHardwareSectionBuilder, VMSpec spec, String networkName) {
      // todo make this work with fractional, which I think means setting speed to half
      addCPU(virtualHardwareSectionBuilder, (int) spec.getProcessorCount());
      addMemory(virtualHardwareSectionBuilder, spec.getMemoryInGig());
      addNetwork(virtualHardwareSectionBuilder, networkName);
      addDisks(virtualHardwareSectionBuilder, spec);
   }

   private void addSystem(XMLBuilder virtualHardwareSectionBuilder, String name) {
      XMLBuilder systemBuilder = virtualHardwareSectionBuilder.e("ovf:System");
      systemBuilder.e("vssd:Description").t("Virtual Hardware Family");
      systemBuilder.e("vssd:ElementName").t(name);
      systemBuilder.e("vssd:InstanceID").t("1");
      systemBuilder.e("vssd:VirtualSystemIdentifier").t(name);
   }

   private void addOperatingSystemSection(XMLBuilder rootBuilder, CIMOperatingSystem operatingSystem) {
      XMLBuilder sectionBuilder = rootBuilder.e("ovf:OperatingSystemSection").a("ovf:id",
               operatingSystem.getOsType().getCode() + "");
      sectionBuilder.e("ovf:Info").t("Specifies the operating system installed");
      sectionBuilder.e("ovf:Description").t(operatingSystem.getDescription());
   }

   private void addCPU(XMLBuilder sectionBuilder, int processorCount) {
      XMLBuilder cpuBuilder = sectionBuilder.e("ovf:Item");
      cpuBuilder.e("rasd:AllocationUnits").t("3 GHz");
      cpuBuilder.e("rasd:Description").t("Number of Virtual CPUs");
      cpuBuilder.e("rasd:ElementName").t(processorCount + " CPU");
      cpuBuilder.e("rasd:InstanceID").t("1");
      cpuBuilder.e("rasd:ResourceType").t(ResourceType.PROCESSOR.value());
      cpuBuilder.e("rasd:VirtualQuantity").t(processorCount + "");
   }

   private void addMemory(XMLBuilder sectionBuilder, int memoryInGig) {
      XMLBuilder memoryBuilder = sectionBuilder.e("ovf:Item");
      memoryBuilder.e("rasd:AllocationUnits").t("Gigabytes");
      memoryBuilder.e("rasd:Description").t("Memory Size");
      memoryBuilder.e("rasd:ElementName").t("Memory");
      memoryBuilder.e("rasd:InstanceID").t("2");
      memoryBuilder.e("rasd:ResourceType").t(ResourceType.MEMORY.value());
      memoryBuilder.e("rasd:VirtualQuantity").t(memoryInGig + "");
   }

   private void addNetwork(XMLBuilder sectionBuilder, String networkName) {
      XMLBuilder networkBuilder = sectionBuilder.e("ovf:Item");
      networkBuilder.e("rasd:Caption").t("false");
      networkBuilder.e("rasd:Connection").t(networkName);
      networkBuilder.e("rasd:ElementName").t("Network");
      networkBuilder.e("rasd:InstanceID").t("3");
      networkBuilder.e("rasd:ResourceType").t(ResourceType.ETHERNET_ADAPTER.value());
      networkBuilder.e("rasd:VirtualQuantity").t("1");
   }

   private void addDisks(XMLBuilder sectionBuilder, VMSpec spec) {
      XMLBuilder bootDiskBuilder = sectionBuilder.e("ovf:Item");
      bootDiskBuilder.e("rasd:AllocationUnits").t("Gigabytes");
      bootDiskBuilder.e("rasd:Caption").t("");
      bootDiskBuilder.e("rasd:Description").t("Hard Disk");
      bootDiskBuilder.e("rasd:ElementName").t(spec.getBootDeviceName());
      bootDiskBuilder.e("rasd:HostResource").t("boot");
      bootDiskBuilder.e("rasd:InstanceID").t("4");
      bootDiskBuilder.e("rasd:ResourceType").t(ResourceType.BASE_PARTITIONABLE_UNIT.value());
      bootDiskBuilder.e("rasd:VirtualQuantity").t(spec.getBootDiskSize() + "");

      int instanceId = 5;
      for (Entry<String, Integer> dataDisk : spec.getDataDiskDeviceNameToSizeInGig().entrySet()) {
         XMLBuilder dataDiskBuilder = sectionBuilder.e("ovf:Item");
         dataDiskBuilder.e("rasd:AllocationUnits").t("Gigabytes");
         dataDiskBuilder.e("rasd:Caption").t("");
         dataDiskBuilder.e("rasd:Description").t("Hard Disk");
         dataDiskBuilder.e("rasd:ElementName").t(dataDisk.getKey());
         dataDiskBuilder.e("rasd:HostResource").t("data");
         dataDiskBuilder.e("rasd:InstanceID").t("" + instanceId++);
         dataDiskBuilder.e("rasd:ResourceType").t(ResourceType.PARTITIONABLE_UNIT.value());
         dataDiskBuilder.e("rasd:VirtualQuantity").t(dataDisk.getValue() + "");
      }
   }

   protected XMLBuilder buildRoot() throws ParserConfigurationException, FactoryConfigurationError {
      XMLBuilder rootBuilder = XMLBuilder.create("vApp:VApp").a("xmlns:vApp", "http://www.vmware.com/vcloud/v0.8").a(
               "xmlns:ovf", "http://schemas.dmtf.org/ovf/envelope/1").a("xmlns:vssd",
               "http://schemas.dmtf.org/wbem/wscim/1/cim-schema/2/CIM_VirtualSystemSettingData").a("xmlns:common",
               "http://schemas.dmtf.org/wbem/wscim/1/common").a("xmlns:rasd",
               "http://schemas.dmtf.org/wbem/wscim/1/cim-schema/2/CIM_ResourceAllocationSettingData");
      return rootBuilder;
   }

   protected XMLBuilder buildChildren(XMLBuilder rootBuilder) throws ParserConfigurationException,
            FactoryConfigurationError {
      XMLBuilder vAppChildrenBuilder = rootBuilder.e("vApp:Children");
      return vAppChildrenBuilder;
   }

   protected XMLBuilder buildRootForName(XMLBuilder rootBuilder, String name) throws ParserConfigurationException,
            FactoryConfigurationError {
      XMLBuilder vAppBuilder = rootBuilder.e("vApp:VApp").a("name", name).a("type",
               "application/vnd.vmware.vcloud.vApp+xml");
      return vAppBuilder;
   }

   protected String ifNullDefaultTo(String value, String defaultValue) {
      return value != null ? value : checkNotNull(defaultValue, "defaultValue");
   }

}
