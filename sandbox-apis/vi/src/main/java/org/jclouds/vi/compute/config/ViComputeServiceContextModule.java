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

import static com.google.common.base.Predicates.notNull;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static org.jclouds.vi.reference.ViConstants.PROPERTY_VI_XML_NAMESPACE;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.RemoteException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.core.HttpHeaders;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.jclouds.Constants;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.config.ComputeServiceAdapterContextModule;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.domain.Location;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.io.Payloads;
import org.jclouds.location.Provider;
import org.jclouds.location.suppliers.OnlyLocationOrFirstZone;
import org.jclouds.rest.HttpClient;
import org.jclouds.vi.Image;
import org.jclouds.vi.compute.functions.DatacenterToLocation;
import org.jclouds.vi.compute.functions.ViImageToImage;
import org.jclouds.vi.compute.functions.VirtualMachineToHardware;
import org.jclouds.vi.compute.functions.VirtualMachineToNodeMetadata;
import org.jclouds.vi.compute.strategy.ViComputeServiceAdapter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.vmware.vim25.mo.Datacenter;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.VirtualMachine;
import com.vmware.vim25.ws.WSClient;

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
      }).to(OnlyLocationOrFirstZone.class);
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
   protected ServiceInstance createConnection(JcloudsWSClient client,
            @Named(Constants.PROPERTY_IDENTITY) String identity,
            @Named(Constants.PROPERTY_CREDENTIAL) String credential,
            @Named(Constants.PROPERTY_TRUST_ALL_CERTS) boolean ignoreCertificate) throws RemoteException,
            MalformedURLException {
      // uncomment this to use jclouds http commands
      // return new ServiceInstance(client, identity, credential, ignoreCertificate);
      return new ServiceInstance(new WSClient(client.getBaseUrl().toString(), ignoreCertificate), identity, credential,
               ignoreCertificate + "");
   }

   @Singleton
   public static class JcloudsWSClient extends WSClient {

      private final HttpClient client;

      @Inject
      public JcloudsWSClient(HttpClient client, @Provider URI baseUrl,
               @Named(PROPERTY_VI_XML_NAMESPACE) String vimNameSpace,
               @Named(Constants.PROPERTY_TRUST_ALL_CERTS) boolean ignoreCert) throws MalformedURLException {
         super(baseUrl.toASCIIString(), ignoreCert);
         this.setVimNameSpace(vimNameSpace);
         this.client = client;
      }

      @Override
      public InputStream post(String soapMsg) throws IOException {

         Builder<String, String> headers = ImmutableMultimap.<String, String> builder();
         headers.put("SOAPAction", "urn:vim25/4.0");
         if (getCookie() != null)
            headers.put(HttpHeaders.COOKIE, getCookie());

         HttpRequest request;
         try {
            request = HttpRequest.builder().endpoint(getBaseUrl().toURI()).method("POST").headers(headers.build())
                     .payload(Payloads.newStringPayload(soapMsg)).build();
         } catch (URISyntaxException e) {
            Throwables.propagate(e);
            return null;// unreachable as the above line will throw the exception.
         }
         HttpResponse response = client.invoke(request);

         if (getCookie() != null && response.getFirstHeaderOrNull(HttpHeaders.SET_COOKIE) != null) {
            setCookie(response.getFirstHeaderOrNull(HttpHeaders.SET_COOKIE));
         }

         return response.getPayload().getInput();
      }

   }

   @Override
   protected TemplateBuilder provideTemplate(Injector injector, TemplateBuilder template) {
      // String domainDir = injector.getInstance(Key.get(String.class,
      // Names.named(PROPERTY_LIBVIRT_DOMAIN_DIR)));
      // String domainDir = "";
      // String hardwareId = searchForHardwareIdInDomainDir(domainDir);
      // String image = searchForImageIdInDomainDir(domainDir);
      return template.hardwareId("vm-1221").imageId("winNetEnterprise64Guest");
   }

   private String searchForImageIdInDomainDir(String domainDir) {
      // TODO
      return "1";
   }

   @SuppressWarnings("unchecked")
   private String searchForHardwareIdInDomainDir(String domainDir, final ParseSax.Factory factory,
            final javax.inject.Provider<UUIDHandler> provider) {

      // TODO: remove commons-io dependency
      return Iterables.<String> getLast(filter(transform(FileUtils.listFiles(new File(domainDir),
               new WildcardFileFilter("*.xml"), null), new Function<File, String>() {

         @Override
         public String apply(File input) {
            try {
               return factory.create(provider.get()).parse(new FileInputStream(input));
            } catch (FileNotFoundException e) {
               // log error.
               return null;
            }
         }

      }), notNull()));
   }

   public static class UUIDHandler extends ParseSax.HandlerWithResult<String> {
      private StringBuilder currentText = new StringBuilder();

      private boolean inDomain;
      private String uuid;

      public String getResult() {
         return uuid;
      }

      @Override
      public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
         if (qName.equals("domain")) {
            inDomain = true;
         }
      }

      @Override
      public void endElement(String uri, String localName, String qName) {
         if (qName.equalsIgnoreCase("uuid") && inDomain) {
            this.uuid = currentText.toString();
         } else if (qName.equalsIgnoreCase("domain")) {
            inDomain = false;
         }
         currentText = new StringBuilder();
      }

      public void characters(char ch[], int start, int length) {
         currentText.append(ch, start, length);
      }
   }

   /*
    * Map<String, URI> regions = newLinkedHashMap(); for (String region :
    * Splitter.on(',').split(regionString)) { regions.put( region,
    * URI.create(injector.getInstance(Key.get(String.class, Names.named(Constants.PROPERTY_ENDPOINT
    * + "." + region))))); } return regions;
    */

}