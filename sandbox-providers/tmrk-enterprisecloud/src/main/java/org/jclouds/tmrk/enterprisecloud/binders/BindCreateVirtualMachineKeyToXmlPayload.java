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
package org.jclouds.tmrk.enterprisecloud.binders;

import com.jamesmurty.utils.XMLBuilder;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;
import org.jclouds.rest.binders.BindToStringPayload;
import org.jclouds.tmrk.enterprisecloud.domain.NamedResource;
import org.jclouds.tmrk.enterprisecloud.domain.layout.LayoutRequest;
import org.jclouds.tmrk.enterprisecloud.domain.network.*;
import org.jclouds.tmrk.enterprisecloud.domain.vm.CreateVirtualMachine;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.util.Properties;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * For use with {@see VirtualMachineClient#createVirtualMachineFromTemplate}
 * @author Jason King
 */
@Singleton
public class BindCreateVirtualMachineKeyToXmlPayload implements Binder {

   private final BindToStringPayload stringBinder;

   @Inject
   BindCreateVirtualMachineKeyToXmlPayload(BindToStringPayload stringBinder) {
      this.stringBinder = stringBinder;
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object key) {
      checkArgument(checkNotNull(key, "key") instanceof CreateVirtualMachine, "this binder is only valid for CreateOsTemplateVirtualMachineRequest instances!");
      checkNotNull(request, "request");
      CreateVirtualMachine vmData = CreateVirtualMachine.class.cast(key);

      String payload = createXMLPayload(vmData);
      return stringBinder.bindToRequest(request, payload);
   }
   
   private String createXMLPayload(CreateVirtualMachine vmData) {
      try {
         Properties outputProperties = new Properties();
         outputProperties.put(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");
         
         final String name = vmData.getName();
         final String processorCount = Integer.toString(vmData.getProcessorCount());
         final String memoryUnit = vmData.getMemory().getUnit();
         final int memoryValue = ((Double)(vmData.getMemory().getValue())).intValue();
         final Set<String> tags = vmData.getTags();
         final String description = vmData.getDescription();
         final LayoutRequest layout = vmData.getLayout();
         final String poweredOn = Boolean.toString(vmData.isPoweredOn());
         final NamedResource template = vmData.getTemplate();

         XMLBuilder builder = XMLBuilder.create("CreateVirtualMachine").a("name",name)
                                           .e("ProcessorCount").t(processorCount).up()
                                           .e("Memory").e("Unit").t(memoryUnit).up()
                                                       .e("Value").t(Integer.toString(memoryValue)).up().up();
         builder = layout(builder,layout);
         builder.e("Description").t(description).up();
         builder = tags(builder,tags);
         builder = linuxCustomization(builder, vmData);
         builder = windowsCustomization(builder, vmData);
         builder.e("PoweredOn").t(poweredOn).up()
                .e("Template").a("href",template.getHref().toString())
                              .a("type", template.getType()).up();

         return builder.asString(outputProperties);
      } catch (ParserConfigurationException e) {
         throw new RuntimeException(e);
      } catch (TransformerException t) {
         throw new RuntimeException(t);
      }
   }

   private XMLBuilder layout(XMLBuilder in, LayoutRequest layout) {
      in = in.e("Layout");
      if(layout.getGroup()!=null) {
         in = in.e("Group").a("href",layout.getGroup().getHref().toString())
                      .a("type",layout.getGroup().getType()).up();
      } else if (layout.getRow()!=null) {
         checkNotNull(layout.getNewGroup(),"newGroup");
         in = in.e("Row").a("href",layout.getRow().getHref().toString())
                         .a("type", layout.getRow().getType()).up()
                       .e("NewGroup").t(layout.getNewGroup()).up();
      } else {
         checkNotNull(layout.getNewRow(),"newRow");
         checkNotNull(layout.getNewGroup(), "newGroup");
         in = in.e("NewRow").t(layout.getNewRow()).up()
                .e("NewGroup").t(layout.getNewGroup()).up();
      }
      return in.up();
   }
   
   private XMLBuilder tags(XMLBuilder in, Set<String> tags ) {
      checkNotNull(tags,"tags");
      in = in.e("Tags");
      for(String tag: tags) {
         in = in.e("Tag").t(tag).up();
      }
      return in.up();
   }
   
   private XMLBuilder linuxCustomization(XMLBuilder in, CreateVirtualMachine vmData) {
      LinuxCustomization linuxCustomization = vmData.getLinuxCustomization();
      if(linuxCustomization==null) return in;
      if(vmData.getWindowsCustomization()!=null) throw new IllegalStateException("Cannot have linux and windows customizations");

       in = in.e("LinuxCustomization")
               .e("NetworkSettings")
                 .e("NetworkAdapterSettings");

      for(NetworkAdapterSetting setting:linuxCustomization.getNetworkSettings().getNetworkAdapterSettings().getNetworkAdapterSettings()) {
          in = networkAdapterSetting(in,setting);
      }

      in = in.up();
      in = dnsSettings(in, linuxCustomization.getNetworkSettings().getDnsSettings());
      
      String href = linuxCustomization.getSshKey().getHref().toString();
      String type = linuxCustomization.getSshKey().getType();
      return in.up().e("SshKey").a("href",href).a("type",type).up().up();
   }

   private XMLBuilder networkAdapterSetting(XMLBuilder builder, NetworkAdapterSetting setting) {
         String href = setting.getNetwork().getHref().toString();
         String name = setting.getNetwork().getName();
         String type = setting.getNetwork().getType();
         builder.e("NetworkAdapter")
                .e("Network").a("href",href).a("name",name).a("type",type).up()
                .e("IpAddress").t(setting.getIpAddress()).up();
      return builder;
   }
   
   private XMLBuilder dnsSettings(XMLBuilder in, DnsSettings dnsSettings) {
      if(dnsSettings==null)return in;
      final String primary   = dnsSettings.getPrimaryDns();
      final String secondary = dnsSettings.getSecondaryDns();

      in = in.e("DnsSettings").e("PrimaryDns").t(primary).up();
      if(secondary!=null && !secondary.isEmpty()) {
         in = in.e("SecondaryDns").t(secondary).up();
      }
      return in.up();
   }
   
   private XMLBuilder windowsCustomization(XMLBuilder builder, CreateVirtualMachine vmData) {
      WindowsCustomization windowsCustomization = vmData.getWindowsCustomization();
      if(windowsCustomization==null) return builder;
      if(vmData.getLinuxCustomization()!=null) throw new IllegalStateException("Cannot have linux and windows customizations");

      //TODO: Not implemented yet
      throw new UnsupportedOperationException("windowsCustomization has not been implemented yet");
      //return builder;
   }
}
