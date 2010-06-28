package org.jclouds.vcloud.bluelock;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.vcloud.VCloudClient;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Tests session refresh works
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "vcloud.VCloudSessionRefreshLiveTest")
public class VCloudSessionRefreshLiveTest {

   private final static int timeOut = 40;
   protected VCloudClient connection;
   protected String account;
   protected ComputeServiceContext context;

   @Test
   public void testSessionRefresh() throws Exception {
      connection.getDefaultOrganization();
      Thread.sleep(timeOut * 1000);
      connection.getDefaultOrganization();
   }

   @BeforeGroups(groups = { "live" })
   public void setupClient() throws IOException {
      account = checkNotNull(System.getProperty("jclouds.test.user"), "jclouds.test.user");
      String key = checkNotNull(System.getProperty("jclouds.test.key"), "jclouds.test.key");

      Properties props = new Properties();
      props.setProperty(PROPERTY_SESSION_INTERVAL, 40 + "");

      context = new ComputeServiceContextFactory().createContext("bluelock", account, key,
               ImmutableSet.<Module> of(new Log4JLoggingModule()), props);

      connection = VCloudClient.class.cast(context.getProviderSpecificContext().getApi());
   }

   @AfterTest
   protected void cleanup() throws InterruptedException, ExecutionException, TimeoutException {
      context.close();
   }

}
