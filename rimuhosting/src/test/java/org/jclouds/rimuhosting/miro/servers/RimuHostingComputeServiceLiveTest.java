package org.jclouds.rimuhosting.miro.servers;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeGroups;
import org.jclouds.rimuhosting.miro.*;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.compute.Server;
import com.google.inject.*;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertNotNull;

/**
 * @author Ivan Meredith
 */
@Test(groups = "live", sequential = true, testName = "rimuhosting.RimuHostingServerServiceLiveTest")
public class RimuHostingComputeServiceLiveTest {
   RimuHostingClient rhClient;
   RimuHostingComputeService rhServerService;

   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      String account = "ddd";
      String key = checkNotNull(System.getProperty("jclouds.test.key"), "jclouds.test.key");
      Injector injector = new RimuHostingContextBuilder(new RimuHostingPropertiesBuilder(
               account, key).relaxSSLHostname().build()).withModules(new Log4JLoggingModule()).buildInjector();

      rhClient = injector.getInstance(RimuHostingClient.class);
      rhServerService = injector.getInstance(RimuHostingComputeService.class);
   }

   @Test
   public void testServerCreate(){
      Server server = rhServerService.createServerAndWait("test.com", "MIRO1B", "lenny");
      assertNotNull(rhClient.getInstance(Long.valueOf(server.getId())));
      server.destroyServer();
   }
}
