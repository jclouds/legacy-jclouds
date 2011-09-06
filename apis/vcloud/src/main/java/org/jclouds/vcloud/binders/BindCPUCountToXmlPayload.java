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
package org.jclouds.vcloud.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_XML_NAMESPACE;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_XML_SCHEMA;

import java.util.Properties;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.cim.ResourceAllocationSettingData.ResourceType;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.binders.BindToStringPayload;

import com.google.common.base.Throwables;
import com.google.inject.Inject;
import com.jamesmurty.utils.XMLBuilder;

/**
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class BindCPUCountToXmlPayload extends BindToStringPayload {
   protected final String ns;
   protected final String schema;

   @Inject
   public BindCPUCountToXmlPayload(BindToStringPayload stringBinder, @Named(PROPERTY_VCLOUD_XML_NAMESPACE) String ns,
         @Named(PROPERTY_VCLOUD_XML_SCHEMA) String schema) {
      this.ns = ns;
      this.schema = schema;
   }

   private static final String RESOURCE_ALLOCATION_NS = "http://schemas.dmtf.org/wbem/wscim/1/cim-schema/2/CIM_ResourceAllocationSettingData";

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object payload) {
      checkArgument(checkNotNull(payload, "cpuCount") instanceof Integer, "this binder is only valid for Integers!");
      Integer cpuCount = Integer.class.cast(payload);
      XMLBuilder cpuItem;
      try {
         cpuItem = XMLBuilder.create("Item").a("xmlns", ns).a("xmlns:rasd", RESOURCE_ALLOCATION_NS);
         cpuItem.e("rasd:AllocationUnits").t("hertz * 10^6");
         cpuItem.e("rasd:Description").t("Number of Virtual CPUs");
         cpuItem.e("rasd:ElementName").t(cpuCount.toString() + " virtual CPU(s)");
         cpuItem.e("rasd:InstanceID").t("4");
         cpuItem.e("rasd:ResourceType").t(ResourceType.PROCESSOR.value());
         cpuItem.e("rasd:VirtualQuantity").t(cpuCount.toString());
         cpuItem.e("rasd:Weight").t("0");
         Properties outputProperties = new Properties();
         outputProperties.put(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");
         request = super.bindToRequest(request, cpuItem.asString(outputProperties));
      } catch (Exception e) {
         Throwables.propagate(e);
      }
      return request;
   }

}
