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

package org.jclouds.libvirt.compute.domain;

import static com.google.common.base.Predicates.notNull;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static org.jclouds.libvirt.LibvirtConstants.PROPERTY_LIBVIRT_DOMAIN_DIR;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URI;

import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.jclouds.Constants;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.config.ComputeServiceAdapterContextModule;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.domain.Location;
import org.jclouds.functions.IdentityFunction;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.libvirt.Image;
import org.jclouds.libvirt.compute.functions.DomainToHardware;
import org.jclouds.libvirt.compute.functions.DomainToNodeMetadata;
import org.jclouds.libvirt.compute.functions.LibvirtImageToImage;
import org.jclouds.libvirt.compute.strategy.LibvirtComputeServiceAdapter;
import org.jclouds.location.Provider;
import org.jclouds.location.suppliers.OnlyLocationOrFirstZone;
import org.libvirt.Connect;
import org.libvirt.Domain;
import org.libvirt.LibvirtException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

/**
 * 
 * @author Adrian Cole
 */
public class LibvirtComputeServiceContextModule extends
         ComputeServiceAdapterContextModule<Connect, Connect, Domain, Domain, Image, Location> {

   public LibvirtComputeServiceContextModule() {
      super(Connect.class, Connect.class);
   }

   @SuppressWarnings("unchecked")
   @Override
   protected void configure() {
      super.configure();
      bind(new TypeLiteral<ComputeServiceAdapter<Domain, Domain, Image, Location>>() {
      }).to(LibvirtComputeServiceAdapter.class);
      bind(new TypeLiteral<Supplier<Location>>() {
      }).to(OnlyLocationOrFirstZone.class);
      bind(new TypeLiteral<Function<Domain, NodeMetadata>>() {
      }).to(DomainToNodeMetadata.class);
      bind(new TypeLiteral<Function<Image, org.jclouds.compute.domain.Image>>() {
      }).to(LibvirtImageToImage.class);
      bind(new TypeLiteral<Function<Domain, Hardware>>() {
      }).to(DomainToHardware.class);
      bind(new TypeLiteral<Function<Location, Location>>() {
      }).to((Class) IdentityFunction.class);

      // bind(ComputeService.class).to(LibvirtComputeService.class);
   }

   @Provides
   @Singleton
   protected Connect createConnection(@Provider URI endpoint, @Named(Constants.PROPERTY_IDENTITY) String identity,
            @Named(Constants.PROPERTY_CREDENTIAL) String credential) throws LibvirtException {
      // ConnectAuth connectAuth = null;
      return new Connect(endpoint.toASCIIString());
   }

   @Override
   protected TemplateBuilder provideTemplate(Injector injector, TemplateBuilder template) {
      String domainDir = injector.getInstance(Key.get(String.class, Names.named(PROPERTY_LIBVIRT_DOMAIN_DIR)));
      String hardwareId = searchForHardwareIdInDomainDir(domainDir, injector.getInstance(ParseSax.Factory.class),
               injector.getProvider(UUIDHandler.class));
      String image = searchForImageIdInDomainDir(domainDir);
      return template.hardwareId(hardwareId).imageId(image);
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