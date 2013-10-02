/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.vcloud.compute.functions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.InputStream;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.inject.Singleton;

import org.jclouds.cim.xml.ResourceAllocationSettingDataHandler;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseSax.Factory;
import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.vcloud.VCloudApiMetadata;
import org.jclouds.vcloud.compute.config.VCloudComputeServiceDependenciesModule;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.Status;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.internal.VAppImpl;
import org.jclouds.vcloud.xml.VAppHandler;
import org.jclouds.vcloud.xml.ovf.VCloudResourceAllocationSettingDataHandler;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

/**
 * Tests behavior of {@code VAppToNodeMetadata}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class VAppToNodeMetadataTest {

   public Injector createInjectorWithLocation(final Location location) {
      return Guice.createInjector(new SaxParserModule(), new AbstractModule() {

         @Override
         protected void configure() {
            Names.bindProperties(binder(), new VCloudApiMetadata().getDefaultProperties());
            bind(new TypeLiteral<Function<ReferenceType, Location>>() {
            }).to(new TypeLiteral<FindLocationForResource>() {
            });
            bind(new TypeLiteral<Function<VApp, Hardware>>() {
            }).to(new TypeLiteral<HardwareForVApp>() {
            });
            bind(ResourceAllocationSettingDataHandler.class).to(VCloudResourceAllocationSettingDataHandler.class);
         }

         @Memoized
         @Singleton
         @Provides
         Supplier<Set<? extends Location>> supplyLocations() {
            return Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet.<Location> of(location));
         }

         @Singleton
         @Provides
         Map<String, Credentials> supplyCreds() {
            return Maps.newConcurrentMap();
         }

         @Singleton
         @Provides
         protected Map<Status, NodeMetadata.Status> provideVAppStatusToNodeStatus() {
            return VCloudComputeServiceDependenciesModule.toPortableNodeStatus;
         }

      });
   }

   public void testWhenVDCIsLocation() {
      Location location = new LocationBuilder().id("https://1.1.1.1/api/v1.0/vdc/1").description("description")
            .scope(LocationScope.PROVIDER).build();
      Injector injector = createInjectorWithLocation(location);
      InputStream is = getClass().getResourceAsStream("/vapp-pool.xml");
      Factory factory = injector.getInstance(ParseSax.Factory.class);
      VApp result = factory.create(injector.getInstance(VAppHandler.class)).parse(is);
      VAppToNodeMetadata converter = injector.getInstance(VAppToNodeMetadata.class);
      NodeMetadata node = converter.apply(result);
      assertNotNull(node);
      assertEquals(node.getUserMetadata(), ImmutableMap.<String, String>of());
      assertEquals(node.getTags(), ImmutableSet.<String>of());
      assertEquals(node.getLocation(), location);
      assertEquals(node.getPrivateAddresses(), ImmutableSet.of("172.16.7.230"));
      assertEquals(node.getPublicAddresses(), ImmutableSet.of());
   }

   public void testWithMetadataParseException() {
      Location location = new LocationBuilder().id("https://1.1.1.1/api/v1.0/vdc/1").description("description")
         .scope(LocationScope.PROVIDER).build();
      Injector injector = createInjectorWithLocation(location);
      InputStream is = getClass().getResourceAsStream("/vapp-pool.xml");
      Factory factory = injector.getInstance(ParseSax.Factory.class);
      VApp result = factory.create(injector.getInstance(VAppHandler.class)).parse(is);
      VAppToNodeMetadata converter = injector.getInstance(VAppToNodeMetadata.class);
      ImmutableMap<String, String> metadata = ImmutableMap.<String, String>of();
      ImmutableSet<String> tags = ImmutableSet.<String>of();

      String description = " user=user_ssoid_1\nuid=3b7bb605-bb30-4e62-a3de-9076b052dee7 label='foo-DEVELOPMENT' date=2013-01-22 17:39:28.252";

      result = new VAppImpl(result.getName(), result.getType(), result.getHref(), result.getStatus(), result.getVDC(),
         description, result.getTasks(), result.isOvfDescriptorUploaded(), result.getChildren(),
         result.getNetworkSection());

      NodeMetadata node = converter.apply(result);
      assertNotNull(node);
      assertEquals(node.getUserMetadata(), metadata);
      assertEquals(node.getTags(), tags);
   }

   public void testWithMetadataNoNewLines() {
      Location location = new LocationBuilder().id("https://1.1.1.1/api/v1.0/vdc/1").description("description")
         .scope(LocationScope.PROVIDER).build();
      Injector injector = createInjectorWithLocation(location);
      InputStream is = getClass().getResourceAsStream("/vapp-pool.xml");
      Factory factory = injector.getInstance(ParseSax.Factory.class);
      VApp result = factory.create(injector.getInstance(VAppHandler.class)).parse(is);
      VAppToNodeMetadata converter = injector.getInstance(VAppToNodeMetadata.class);
      ImmutableMap<String, String> metadata = ImmutableMap.<String, String>of();
      ImmutableSet<String> tags = ImmutableSet.<String>of();

      String description = " user=user_ssoid_1 uid=3b7bb605-bb30-4e62-a3de-9076b052dee7 label='foo-DEVELOPMENT' date=2013-01-22 17:39:28.252";

      result = new VAppImpl(result.getName(), result.getType(), result.getHref(), result.getStatus(), result.getVDC(),
         description, result.getTasks(), result.isOvfDescriptorUploaded(), result.getChildren(),
         result.getNetworkSection());

      NodeMetadata node = converter.apply(result);
      assertNotNull(node);
      assertEquals(node.getUserMetadata(), metadata);
      assertEquals(node.getTags(), tags);
   }

   public void testWithEncodedMetadata() {
      Location location = new LocationBuilder().id("https://1.1.1.1/api/v1.0/vdc/1").description("description")
            .scope(LocationScope.PROVIDER).build();
      Injector injector = createInjectorWithLocation(location);
      InputStream is = getClass().getResourceAsStream("/vapp-pool.xml");
      Factory factory = injector.getInstance(ParseSax.Factory.class);
      VApp result = factory.create(injector.getInstance(VAppHandler.class)).parse(is);
      VAppToNodeMetadata converter = injector.getInstance(VAppToNodeMetadata.class);
      ImmutableMap<String, String> metadata = ImmutableMap.<String, String>of("foo", "bar");
      ImmutableSet<String> tags = ImmutableSet.<String>of("tag1", "tag2");
      
      String description = Joiner
               .on('\n')
               .withKeyValueSeparator("=")
               .join(ImmutableMap.<String, String> builder().putAll(metadata)
                        .put("jclouds_tags", Joiner.on(',').join(tags)).build());
      
      result = new VAppImpl(result.getName(), result.getType(), result.getHref(), result.getStatus(), result.getVDC(),
               description, result.getTasks(), result.isOvfDescriptorUploaded(), result.getChildren(),
               result.getNetworkSection());
      
      NodeMetadata node = converter.apply(result);
      assertNotNull(node);
      assertEquals(node.getUserMetadata(), metadata);
      assertEquals(node.getTags(), tags);

   }

   public void testGracefulWhenNoIPs() {
      Location location = new LocationBuilder().id("https://1.1.1.1/api/v1.0/vdc/1").description("description")
            .scope(LocationScope.PROVIDER).build();
      Injector injector = createInjectorWithLocation(location);
      InputStream is = getClass().getResourceAsStream("/vapp-none.xml");
      Factory factory = injector.getInstance(ParseSax.Factory.class);
      VApp result = factory.create(injector.getInstance(VAppHandler.class)).parse(is);
      VAppToNodeMetadata converter = injector.getInstance(VAppToNodeMetadata.class);
      NodeMetadata node = converter.apply(result);
      assertNotNull(node);
      assertEquals(node.getLocation(), location);
      assertEquals(node.getPrivateAddresses(), ImmutableSet.of());
      assertEquals(node.getPublicAddresses(), ImmutableSet.of());
   }

   @Test(expectedExceptions = NoSuchElementException.class)
   public void testGracefulWhenVDCIsNotLocation() {
      Location location = new LocationBuilder().id("https://1.1.1.1/api/v1.0/vdc/11111").description("description")
            .scope(LocationScope.PROVIDER).build();
      Injector injector = createInjectorWithLocation(location);
      InputStream is = getClass().getResourceAsStream("/vapp-pool.xml");
      Factory factory = injector.getInstance(ParseSax.Factory.class);
      VApp result = factory.create(injector.getInstance(VAppHandler.class)).parse(is);
      VAppToNodeMetadata converter = injector.getInstance(VAppToNodeMetadata.class);
      NodeMetadata node = converter.apply(result);
      assertNotNull(node);
      assertEquals(node.getLocation(), location);
   }
}
