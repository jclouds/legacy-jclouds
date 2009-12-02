package org.jclouds.vcloud.config;

import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_ENDPOINT;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_KEY;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_SESSIONINTERVAL;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_USER;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_VERSION;
import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.util.Jsr330;
import org.jclouds.util.Utils;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "vcloud.VCloudRestClientModuleTest")
public class VCloudRestClientModuleTest {

   protected Injector createInjector() {
      return Guice.createInjector(new VCloudRestClientModule(),
               new VCloudDiscoveryRestClientModule(), new ParserModule(), new AbstractModule() {
                  @Override
                  protected void configure() {
                     bindConstant().annotatedWith(Jsr330.named(PROPERTY_VCLOUD_VERSION)).to("0.8");
                     bindConstant().annotatedWith(Jsr330.named(PROPERTY_VCLOUD_USER)).to("user");
                     bindConstant().annotatedWith(Jsr330.named(PROPERTY_VCLOUD_KEY)).to("secret");
                     bindConstant().annotatedWith(Jsr330.named(PROPERTY_VCLOUD_ENDPOINT)).to(
                              "http://localhost");
                     bindConstant().annotatedWith(Jsr330.named(PROPERTY_VCLOUD_SESSIONINTERVAL))
                              .to("2");
                  }
               });
   }

   @Test
   void postStrings() throws IOException {
      assertEquals(createInjector().getInstance(
               Key.get(String.class, Jsr330.named("InstantiateVAppTemplateParams"))), Utils
               .toStringAndClose(getClass().getResourceAsStream(
                        "/InstantiateVAppTemplateParams.xml")));

   }

}