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

package org.jclouds.vi.compute.config;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.rmi.RemoteException;
import java.util.Collection;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.jclouds.Constants;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.config.ComputeServiceAdapterContextModule;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.suppliers.DefaultLocationSupplier;
import org.jclouds.domain.Location;
import org.jclouds.rest.annotations.Provider;
import org.jclouds.vi.Image;
import org.jclouds.vi.compute.functions.DatacenterToLocation;
import org.jclouds.vi.compute.functions.ViImageToImage;
import org.jclouds.vi.compute.functions.VirtualMachineToHardware;
import org.jclouds.vi.compute.functions.VirtualMachineToNodeMetadata;
import org.jclouds.vi.compute.strategy.ViComputeServiceAdapter;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.common.io.Files;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.jamesmurty.utils.XMLBuilder;
import com.vmware.vim25.mo.Datacenter;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.VirtualMachine;

/**
 * 
 * @author Adrian Cole
 */
public class ViComputeServiceContextModule
      extends
      ComputeServiceAdapterContextModule<ServiceInstance, ServiceInstance, VirtualMachine, VirtualMachine, Image, Datacenter> {
  
   public ViComputeServiceContextModule() {
      super(ServiceInstance.class, ServiceInstance.class);
   }

   @Override
   protected void configure() {
      super.configure();
      bind(new TypeLiteral<ComputeServiceAdapter<VirtualMachine, VirtualMachine, Image, Datacenter>>() {
      }).to(ViComputeServiceAdapter.class);
      bind(new TypeLiteral<Supplier<Location>>() {
      }).to(DefaultLocationSupplier.class);
      bind(new TypeLiteral<Function<VirtualMachine, NodeMetadata>>() {
      }).to(VirtualMachineToNodeMetadata.class);
      bind(new TypeLiteral<Function<Image, org.jclouds.compute.domain.Image>>() {
      }).to(ViImageToImage.class);
      bind(new TypeLiteral<Function<VirtualMachine, Hardware>>() {
      }).to(VirtualMachineToHardware.class);
      bind(new TypeLiteral<Function<Datacenter, Location>>() {
      }).to(DatacenterToLocation.class);
   }

   @Provides
   @Singleton
   protected ServiceInstance createConnection(@Provider URI endpoint,
         @Named(Constants.PROPERTY_IDENTITY) String identity, @Named(Constants.PROPERTY_CREDENTIAL) String credential)
         throws RemoteException, MalformedURLException {
      return new ServiceInstance(endpoint.toURL(), identity, credential, true);
   }

   @Override
   protected TemplateBuilder provideTemplate(Injector injector, TemplateBuilder template) {
      // String domainDir = injector.getInstance(Key.get(String.class,
      // Names.named(PROPERTY_LIBVIRT_DOMAIN_DIR)));
//      String domainDir = "";
//      String hardwareId = searchForHardwareIdInDomainDir(domainDir);
//      String image = searchForImageIdInDomainDir(domainDir);
      return template.hardwareId("vm-1221").imageId("winNetEnterprise64Guest");
   }

   private String searchForImageIdInDomainDir(String domainDir) {
      // TODO
      return "1";
   }

   @SuppressWarnings("unchecked")
   private String searchForHardwareIdInDomainDir(String domainDir) {

      Collection<File> xmlDomains = FileUtils.listFiles(new File(domainDir), new WildcardFileFilter("*.xml"), null);
      String uuid = "";
      try {
         String fromXML = Files.toString(Iterables.get(xmlDomains, 0), Charsets.UTF_8);
         XMLBuilder builder = XMLBuilder.parse(new InputSource(new StringReader(fromXML)));
         uuid = builder.xpathFind("/domain/uuid").getElement().getTextContent();
      } catch (IOException e) {
         e.printStackTrace();
      } catch (ParserConfigurationException e) {
         e.printStackTrace();
      } catch (SAXException e) {
         e.printStackTrace();
      } catch (XPathExpressionException e) {
         e.printStackTrace();
      }
      return uuid;
   }

   /*
    * Map<String, URI> regions = newLinkedHashMap(); for (String region :
    * Splitter.on(',').split(regionString)) { regions.put( region,
    * URI.create(injector.getInstance(Key.get(String.class, Names.named(Constants.PROPERTY_ENDPOINT
    * + "." + region))))); } return regions;
    */

}