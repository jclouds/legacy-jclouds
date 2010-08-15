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

package org.jclouds.vcloud.terremark.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.find;
import static org.jclouds.vcloud.predicates.VCloudPredicates.resourceType;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_XML_NAMESPACE;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_XML_SCHEMA;

import java.net.URI;
import java.util.Map;
import java.util.Properties;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.MapBinder;
import org.jclouds.rest.binders.BindToStringPayload;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.vcloud.domain.ResourceAllocation;
import org.jclouds.vcloud.domain.ResourceType;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.VAppStatus;
import org.jclouds.vcloud.terremark.domain.VAppConfiguration;

import com.google.common.base.Function;
import com.google.inject.Inject;
import com.jamesmurty.utils.XMLBuilder;

/**
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class BindVAppConfigurationToXmlPayload implements MapBinder, Function<Object, URI> {

   private static final String RESOURCE_ALLOCATION_NS = "http://schemas.dmtf.org/wbem/wscim/1/cim-schema/2/CIM_ResourceAllocationSettingData";

   protected final String ns;
   protected final String schema;
   private final BindToStringPayload stringBinder;

   @Inject
   public BindVAppConfigurationToXmlPayload(BindToStringPayload stringBinder,
         @Named(PROPERTY_VCLOUD_XML_NAMESPACE) String ns, @Named(PROPERTY_VCLOUD_XML_SCHEMA) String schema) {
      this.ns = ns;
      this.schema = schema;
      this.stringBinder = stringBinder;
   }

   @SuppressWarnings("unchecked")
   public void bindToRequest(HttpRequest request, Map<String, String> postParams) {
      checkArgument(checkNotNull(request, "request") instanceof GeneratedHttpRequest,
            "this binder is only valid for GeneratedHttpRequests!");
      GeneratedHttpRequest gRequest = (GeneratedHttpRequest) request;
      checkState(gRequest.getArgs() != null, "args should be initialized at this point");

      VApp vApp = checkNotNull(findVAppInArgsOrNull(gRequest), "vApp");
      checkArgument(vApp.getStatus() == VAppStatus.OFF, "vApp must be off!");
      VAppConfiguration configuration = checkNotNull(findConfigInArgsOrNull(gRequest), "config");

      try {
         stringBinder.bindToRequest(request, generateXml(vApp, configuration));
      } catch (ParserConfigurationException e) {
         throw new RuntimeException(e);
      } catch (FactoryConfigurationError e) {
         throw new RuntimeException(e);
      } catch (TransformerException e) {
         throw new RuntimeException(e);
      }

   }

   protected String generateXml(VApp vApp, VAppConfiguration configuration) throws ParserConfigurationException,
         FactoryConfigurationError, TransformerException {
      String name = configuration.getName() != null ? configuration.getName() : vApp.getName();

      XMLBuilder rootBuilder = buildRoot(vApp, name);

      XMLBuilder sectionBuilder = rootBuilder.e("Section").a("xsi:type", "VirtualHardwareSection_Type").a("xmlns",
            "http://schemas.dmtf.org/ovf/envelope/1").a("xmlns:q2", "http://www.vmware.com/vcloud/v1");
      sectionBuilder.e("Info").t("Virtual Hardware");

      addProcessorItem(sectionBuilder, vApp, configuration);
      addMemoryItem(sectionBuilder, vApp, configuration);
      addDiskItems(sectionBuilder, vApp, configuration);

      Properties outputProperties = new Properties();
      outputProperties.put(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");
      return rootBuilder.asString(outputProperties);
   }

   private void addProcessorItem(XMLBuilder sectionBuilder, VApp vApp, VAppConfiguration configuration) {
      ResourceAllocation cpu = find(vApp.getResourceAllocations(), resourceType(ResourceType.PROCESSOR));
      long quantity = configuration.getProcessorCount() != null ? configuration.getProcessorCount() : cpu
            .getVirtualQuantity();
      addResourceWithQuantity(sectionBuilder, cpu, quantity);
   }

   private void addMemoryItem(XMLBuilder sectionBuilder, VApp vApp, VAppConfiguration configuration) {
      ResourceAllocation memory = find(vApp.getResourceAllocations(), resourceType(ResourceType.MEMORY));
      long quantity = configuration.getMemory() != null ? configuration.getMemory() : memory.getVirtualQuantity();
      addResourceWithQuantity(sectionBuilder, memory, quantity);
   }

   private void addDiskItems(XMLBuilder sectionBuilder, VApp vApp, VAppConfiguration configuration) {
      for (ResourceAllocation disk : filter(vApp.getResourceAllocations(), resourceType(ResourceType.DISK_DRIVE))) {
         if (!configuration.getDisksToDelete().contains(disk.getAddressOnParent()))
            addDiskWithQuantity(sectionBuilder, disk);
      }
      for (Long quantity : configuration.getDisks()) {
         ResourceAllocation disk = new ResourceAllocation(9, "n/a", null, ResourceType.DISK_DRIVE, null, "1048576",
               null, -1, null, null, quantity, null);
         addDiskWithQuantity(sectionBuilder, disk);
      }
   }

   private XMLBuilder addResourceWithQuantity(XMLBuilder sectionBuilder, ResourceAllocation resource, long quantity) {
      XMLBuilder itemBuilder = sectionBuilder.e("Item");
      addCommonElements(itemBuilder, resource, quantity);
      return itemBuilder;
   }

   private void addCommonElements(XMLBuilder itemBuilder, ResourceAllocation resource, long quantity) {
      itemBuilder.e("InstanceID").a("xmlns", RESOURCE_ALLOCATION_NS).t(resource.getId() + "");
      itemBuilder.e("ResourceType").a("xmlns", RESOURCE_ALLOCATION_NS).t(resource.getType().value());
      itemBuilder.e("VirtualQuantity").a("xmlns", RESOURCE_ALLOCATION_NS).t(quantity + "");
   }

   private XMLBuilder addDiskWithQuantity(XMLBuilder sectionBuilder, ResourceAllocation disk) {
      XMLBuilder itemBuilder = sectionBuilder.e("Item");
      itemBuilder.e("AddressOnParent").a("xmlns", RESOURCE_ALLOCATION_NS).t(disk.getAddressOnParent() + "");
      itemBuilder.e("HostResource").a("xmlns", RESOURCE_ALLOCATION_NS).t(disk.getHostResource());
      addCommonElements(itemBuilder, disk, disk.getVirtualQuantity());
      return itemBuilder;
   }

   protected XMLBuilder buildRoot(VApp vApp, String name) throws ParserConfigurationException,
         FactoryConfigurationError {
      XMLBuilder rootBuilder = XMLBuilder.create("VApp").a("type", vApp.getType()).a("name", name).a("status",
            vApp.getStatus().value()).a("size", vApp.getSize() + "").a("xmlns", ns).a("xmlns:xsi",
            "http://www.w3.org/2001/XMLSchema-instance").a("xsi:schemaLocation", ns + " " + schema);
      return rootBuilder;
   }

   protected VApp findVAppInArgsOrNull(GeneratedHttpRequest<?> gRequest) {
      for (Object arg : gRequest.getArgs()) {
         if (arg instanceof VApp) {
            return (VApp) arg;
         } else if (arg instanceof VApp[]) {
            VApp[] vapps = (VApp[]) arg;
            return (vapps.length > 0) ? vapps[0] : null;
         }
      }
      return null;
   }

   protected VAppConfiguration findConfigInArgsOrNull(GeneratedHttpRequest<?> gRequest) {
      for (Object arg : gRequest.getArgs()) {
         if (arg instanceof VAppConfiguration) {
            return (VAppConfiguration) arg;
         } else if (arg instanceof VAppConfiguration[]) {
            VAppConfiguration[] configuration = (VAppConfiguration[]) arg;
            return (configuration.length > 0) ? configuration[0] : null;
         }
      }
      return null;
   }

   public void bindToRequest(HttpRequest request, Object input) {
      throw new IllegalStateException("BindVAppConfigurationToXmlPayload needs parameters");
   }

   protected String ifNullDefaultTo(String value, String defaultValue) {
      return value != null ? value : checkNotNull(defaultValue, "defaultValue");
   }

   @Override
   public URI apply(Object from) {
      return VApp.class.cast(checkNotNull(from, "from")).getId();
   }
}
