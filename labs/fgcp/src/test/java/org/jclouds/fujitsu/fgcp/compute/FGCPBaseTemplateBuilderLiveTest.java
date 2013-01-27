package org.jclouds.fujitsu.fgcp.compute;

import static org.jclouds.compute.util.ComputeServiceUtils.getCores;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Properties;

import org.jclouds.Constants;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.OsFamilyVersion64Bit;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.internal.BaseTemplateBuilderLiveTest;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

public abstract class FGCPBaseTemplateBuilderLiveTest extends BaseTemplateBuilderLiveTest {

   @Override
   protected Properties setupProperties() {
      Properties overrides = super.setupProperties();

      String proxy = System.getenv("http_proxy");
      if (proxy != null) {

         String[] parts = proxy.split("http://|:|@");

         overrides.setProperty(Constants.PROPERTY_PROXY_HOST, parts[parts.length - 2]);
         overrides.setProperty(Constants.PROPERTY_PROXY_PORT, parts[parts.length - 1]);

         if (parts.length >= 4) {
            overrides.setProperty(Constants.PROPERTY_PROXY_USER, parts[parts.length - 4]);
            overrides.setProperty(Constants.PROPERTY_PROXY_PASSWORD, parts[parts.length - 3]);
         }
      }

      // enables peer verification using the CAs bundled with the JRE (or
      // value of javax.net.ssl.trustStore if set)
      overrides.setProperty(Constants.PROPERTY_TRUST_ALL_CERTS, "false");

      return overrides;
   }

   @Override
   protected Predicate<OsFamilyVersion64Bit> defineUnsupportedOperatingSystems() {
      return Predicates.not(new Predicate<OsFamilyVersion64Bit>() {

         @Override
         public boolean apply(OsFamilyVersion64Bit input) {
            switch (input.family) {
            case CENTOS:
               return ("".equals(input.version) && input.is64Bit) || (input.version.matches("5.[46]") && input.is64Bit)
                     || input.version.equals("6.[2]");
            case WINDOWS:
               return ("".equals(input.version) && input.is64Bit)
                     || (input.version.equals("2008 R2 SE") || input.version.equals("2008 R2 EE")) && input.is64Bit;
            default:
               return false;
            }
         }

      });
   }

   public void testDefaultTemplateBuilder() throws IOException {
      Template defaultTemplate = view.getComputeService().templateBuilder().build();
      assert defaultTemplate.getImage().getOperatingSystem().getVersion().matches("6.2") : defaultTemplate.getImage()
            .getOperatingSystem().getVersion();
      assertEquals(defaultTemplate.getImage().getOperatingSystem().is64Bit(), true);
      assertEquals(defaultTemplate.getImage().getOperatingSystem().getFamily(), OsFamily.CENTOS);
      assertEquals(getCores(defaultTemplate.getHardware()), 1.0d);
   }

}
