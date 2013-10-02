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
package org.jclouds.compute.internal;

import static org.jclouds.compute.util.ComputeServiceUtils.getCores;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.config.BaseComputeServiceContextModule;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.OsFamilyVersion64Bit;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.io.CopyInputStreamInputSupplierMap;
import org.jclouds.json.Json;
import org.jclouds.json.config.GsonModule;
import org.jclouds.rest.config.CredentialStoreModule;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.io.InputSupplier;
import com.google.inject.Guice;
import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "integration,live")
public abstract class BaseTemplateBuilderLiveTest extends BaseComputeServiceContextLiveTest {

   public void testCompareSizes() throws Exception {
      Hardware defaultSize = view.getComputeService().templateBuilder().build().getHardware();

      Hardware smallest = view.getComputeService().templateBuilder().smallest().build().getHardware();
      Hardware fastest = view.getComputeService().templateBuilder().fastest().build().getHardware();
      Hardware biggest = view.getComputeService().templateBuilder().biggest().build().getHardware();

      System.out.printf("smallest %s%n", smallest);
      System.out.printf("fastest %s%n", fastest);
      System.out.printf("biggest %s%n", biggest);

      assertEquals(defaultSize, smallest);

      assert getCores(smallest) <= getCores(fastest) : String.format("%s ! <= %s", smallest, fastest);
      assert getCores(biggest) <= getCores(fastest) : String.format("%s ! <= %s", biggest, fastest);

      assert biggest.getRam() >= fastest.getRam() : String.format("%s ! >= %s", biggest, fastest);
      assert biggest.getRam() >= smallest.getRam() : String.format("%s ! >= %s", biggest, smallest);

      assert getCores(fastest) >= getCores(biggest) : String.format("%s ! >= %s", fastest, biggest);
      assert getCores(fastest) >= getCores(smallest) : String.format("%s ! >= %s", fastest, smallest);
   }

   public void testFromTemplate() {
      Template defaultTemplate = view.getComputeService().templateBuilder().build();
      assertEquals(view.getComputeService().templateBuilder().fromTemplate(defaultTemplate).build().toString(),
            defaultTemplate.toString());
   }

   @DataProvider(name = "osSupported")
   public Object[][] osSupported() {
      return convertToArray(Sets.filter(provideAllOperatingSystems(),
            Predicates.not(defineUnsupportedOperatingSystems())));
   }

   protected Object[][] convertToArray(Set<OsFamilyVersion64Bit> supportedOperatingSystems) {
      Object[][] returnVal = new Object[supportedOperatingSystems.size()][1];
      int i = 0;
      for (OsFamilyVersion64Bit config : supportedOperatingSystems)
         returnVal[i++][0] = config;
      return returnVal;
   }

   protected Predicate<OsFamilyVersion64Bit> defineUnsupportedOperatingSystems() {
      return Predicates.alwaysFalse();
   }

   @DataProvider(name = "osNotSupported")
   public Object[][] osNotSupported() {
      return convertToArray(Sets.filter(provideAllOperatingSystems(), defineUnsupportedOperatingSystems()));
   }

   protected Set<OsFamilyVersion64Bit> provideAllOperatingSystems() {
      Map<OsFamily, Map<String, String>> map = new BaseComputeServiceContextModule() {
      }.provideOsVersionMap(new ComputeServiceConstants.ReferenceData(), Guice.createInjector(new GsonModule())
            .getInstance(Json.class));

      Set<OsFamilyVersion64Bit> supportedOperatingSystems = Sets.newHashSet();
      for (Entry<OsFamily, Map<String, String>> osVersions : map.entrySet()) {
         for (String version : Sets.newHashSet(osVersions.getValue().values())) {
            supportedOperatingSystems.add(new OsFamilyVersion64Bit(osVersions.getKey(), version, false));
            supportedOperatingSystems.add(new OsFamilyVersion64Bit(osVersions.getKey(), version, true));
         }
      }
      return supportedOperatingSystems;
   }

   @Test(dataProvider = "osSupported")
   public void testTemplateBuilderCanFind(OsFamilyVersion64Bit matrix) throws InterruptedException {
      TemplateBuilder builder = view.getComputeService().templateBuilder().osFamily(matrix.family)
            .os64Bit(matrix.is64Bit);
      if (!matrix.version.equals(""))
         builder.osVersionMatches("^" + matrix.version + "$");
      Template template = builder.build();
      if (!matrix.version.equals(""))
         assertEquals(template.getImage().getOperatingSystem().getVersion(), matrix.version);
      assertEquals(template.getImage().getOperatingSystem().is64Bit(), matrix.is64Bit);
      assertEquals(template.getImage().getOperatingSystem().getFamily(), matrix.family);
   }

   @Test(dataProvider = "osNotSupported", expectedExceptions = NoSuchElementException.class)
   public void testTemplateBuilderCannotFind(OsFamilyVersion64Bit matrix) throws InterruptedException {
      TemplateBuilder builder = view.getComputeService().templateBuilder().osFamily(matrix.family)
            .os64Bit(matrix.is64Bit);
      if (!matrix.version.equals(""))
         builder.osVersionMatches("^" + matrix.version + "$");
      builder.build();
   }

   @Test
   public void testTemplateBuilderCanUseImageId() throws Exception {
      Template defaultTemplate = view.getComputeService().templateBuilder().build();
      view.close();
      setupContext();

      Template template = view.getComputeService().templateBuilder().imageId(defaultTemplate.getImage().getId())
            .locationId(defaultTemplate.getLocation().getId()).build();
      assertEquals(template.getImage(), defaultTemplate.getImage());
   }

   @Test
   public void testDefaultTemplateBuilder() throws IOException {
      Template defaultTemplate = view.getComputeService().templateBuilder().build();
      assert defaultTemplate.getImage().getOperatingSystem().getVersion().matches("1[012].[10][04]") : defaultTemplate
            .getImage().getOperatingSystem().getVersion();
      assertEquals(defaultTemplate.getImage().getOperatingSystem().is64Bit(), true);
      assertEquals(defaultTemplate.getImage().getOperatingSystem().getFamily(), OsFamily.UBUNTU);
      assertEquals(getCores(defaultTemplate.getHardware()), 1.0d);
   }

   protected abstract Set<String> getIso3166Codes();

   @Test(groups = { "integration", "live" })
   public void testGetAssignableLocations() throws Exception {
      assertProvider(view.unwrap());
      for (Location location : view.getComputeService().listAssignableLocations()) {
         System.err.printf("location %s%n", location);
         assert location.getId() != null : location;
         assert location != location.getParent() : location;
         assert location.getScope() != null : location;
         switch (location.getScope()) {
         case PROVIDER:
            assertProvider(location);
            break;
         case REGION:
            assertProvider(location.getParent());
            assert location.getIso3166Codes().size() == 0
                  || location.getParent().getIso3166Codes().containsAll(location.getIso3166Codes()) : location + " ||"
                  + location.getParent();
            break;
         case ZONE:
            Location provider = location.getParent().getParent();
            // zone can be a direct descendant of provider
            if (provider == null)
               provider = location.getParent();
            assertProvider(provider);
            assert location.getIso3166Codes().size() == 0
                  || location.getParent().getIso3166Codes().containsAll(location.getIso3166Codes()) : location + " ||"
                  + location.getParent();
            break;
         case SYSTEM:
            Location systemParent = location.getParent();
            // loop up to root, which must be the provider
            while (systemParent.getParent() != null) {
                systemParent = systemParent.getParent();
            }
            assertProvider(systemParent);
            break;
         case NETWORK:
             Location networkParent = location.getParent();
             // loop up to root, which must be the provider
             while (networkParent.getParent() != null) {
                 networkParent = networkParent.getParent();
             }
             assertProvider(networkParent);
             break;
         case HOST:
            Location provider2 = location.getParent().getParent().getParent();
            // zone can be a direct descendant of provider
            if (provider2 == null)
               provider2 = location.getParent().getParent();
            assertProvider(provider2);
            break;
         }
      }
   }

   @Test
   public void testTemplateBuilderWithImageIdSpecified() throws IOException {
      Template defaultTemplate = view.getComputeService().templateBuilder().build();

      ComputeServiceContext context = null;
      try {
         Properties overrides = setupProperties();
         overrides.setProperty("jclouds.image-id", defaultTemplate.getImage().getId());

         context = createView(overrides, setupModules());

         assertEquals(context.getComputeService().templateBuilder().build().toString(), defaultTemplate.toString());
      } finally {
         if (context != null)
            context.close();
      }

      context = null;
      try {
         Properties overrides = setupProperties();
         overrides.setProperty(provider + ".image-id", defaultTemplate.getImage().getId());

         context = createView(overrides, setupModules());

         assertEquals(context.getComputeService().templateBuilder().build().toString(), defaultTemplate.toString());
      } finally {
         if (context != null)
            context.close();
      }
   }

   @Test
   public void testTemplateBuilderWithLoginUserSpecified() throws IOException {
      tryOverrideUsingPropertyKey("jclouds");
      tryOverrideUsingPropertyKey(provider);
   }

   protected void tryOverrideUsingPropertyKey(String propertyKey) {
      // isolate tests from eachother, as default credentialStore is static
      Module credentialStoreModule = new CredentialStoreModule(new CopyInputStreamInputSupplierMap(
            new ConcurrentHashMap<String, InputSupplier<InputStream>>()));

      ComputeServiceContext context = null;
      try {
         Properties overrides = setupProperties();
         String login = template != null && template.getLoginUser() != null ? template.getLoginUser() : "foo:bar";
         overrides.setProperty(propertyKey + ".image.login-user", login);
         boolean auth = template != null && template.getAuthenticateSudo() != null ? template.getAuthenticateSudo() : true;
         overrides.setProperty(propertyKey + ".image.authenticate-sudo", auth + "");

         context = createView(overrides, ImmutableSet.<Module>of(credentialStoreModule));

         Iterable<String> userPass = Splitter.on(':').split(login);
         String user = Iterables.get(userPass, 0);
         String pass = Iterables.size(userPass) > 1 ? Iterables.get(userPass, 1) : null;
         assertEquals(context.getComputeService().templateBuilder().build().getImage().getDefaultCredentials(),
               LoginCredentials.builder().user(user).password(pass).authenticateSudo(auth).build());
      } finally {
         if (context != null) {
            context.close();
         }
      }
   }

   void assertProvider(Location provider) {
      assertEquals(provider.getScope(), LocationScope.PROVIDER);
      assertEquals(provider.getParent(), null);
      assertEquals(provider.getIso3166Codes(), getIso3166Codes());
   }

}
