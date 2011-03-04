package org.jclouds.savvis.vpdc.compute;

import static org.jclouds.compute.util.ComputeServiceUtils.getCores;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.jclouds.compute.BaseTemplateBuilderLiveTest;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.os.OsFamilyVersion64Bit;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.savvis.vpdc.SymphonyVPDCContextBuilder;
import org.jclouds.savvis.vpdc.SymphonyVPDCPropertiesBuilder;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live")
public class SymphonyVPDCTemplateBuilderLiveTest extends BaseTemplateBuilderLiveTest {

   public SymphonyVPDCTemplateBuilderLiveTest() {
      provider = "savvis-symphony-vpdc";
   }

   @Override
   @BeforeClass
   public void setupClient() throws InterruptedException, ExecutionException, TimeoutException, IOException {
      setupCredentials();
      // TODO remove these lines when this is registered under jclouds-core/rest.properties
      Properties restProperties = new Properties();
      restProperties.setProperty("savvis-symphony-vpdc.contextbuilder", SymphonyVPDCContextBuilder.class.getName());
      restProperties.setProperty("savvis-symphony-vpdc.propertiesbuilder",
            SymphonyVPDCPropertiesBuilder.class.getName());

      context = new ComputeServiceContextFactory(restProperties).createContext(provider,
            ImmutableSet.<Module> of(new Log4JLoggingModule()), setupProperties());
   }

   @Override
   protected Predicate<OsFamilyVersion64Bit> defineUnsupportedOperatingSystems() {
      return new Predicate<OsFamilyVersion64Bit>() {

         @Override
         public boolean apply(OsFamilyVersion64Bit input) {
            // TODO it seems there are no base vApp Templates available in Savvis
            return true;
         }
      };
   }

   @Test
   public void testDefaultTemplateBuilder() throws IOException {
      Template defaultTemplate = context.getComputeService().templateBuilder().build();
      assertEquals(defaultTemplate.getImage().getOperatingSystem().getVersion(), "5.5");
      assertEquals(defaultTemplate.getImage().getOperatingSystem().is64Bit(), true);
      assertEquals(defaultTemplate.getImage().getOperatingSystem().getFamily(), OsFamily.CENTOS);
      assertEquals(getCores(defaultTemplate.getHardware()), 1.0d);
   }

   @Override
   protected Set<String> getIso3166Codes() {
      return ImmutableSet.<String> of("US-FL", "NL-NH");
   }
}