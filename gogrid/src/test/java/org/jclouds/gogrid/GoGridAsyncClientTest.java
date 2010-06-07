package org.jclouds.gogrid;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.jclouds.date.TimeStamp;
import org.jclouds.gogrid.config.GoGridRestClientModule;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import com.google.inject.name.Names;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code GoGridClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ec2.GoGridClientTest")
public class GoGridAsyncClientTest extends RestClientTest<GoGridAsyncClient> {

   private GoGridAsyncClient asyncClient;
   private GoGridClient syncClient;

   public void testSync() throws SecurityException, NoSuchMethodException, InterruptedException,
            ExecutionException {
      assert syncClient.getImageServices() != null;
      assert syncClient.getIpServices() != null;
      assert syncClient.getJobServices() != null;
      assert syncClient.getLoadBalancerServices() != null;
      assert syncClient.getServerServices() != null;
   }

   public void testAsync() throws SecurityException, NoSuchMethodException, InterruptedException,
            ExecutionException {
      assert asyncClient.getImageServices() != null;
      assert asyncClient.getIpServices() != null;
      assert asyncClient.getJobServices() != null;
      assert asyncClient.getLoadBalancerServices() != null;
      assert asyncClient.getServerServices() != null;
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<GoGridAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<GoGridAsyncClient>>() {
      };
   }

   @BeforeClass
   @Override
   protected void setupFactory() {
      super.setupFactory();
      asyncClient = injector.getInstance(GoGridAsyncClient.class);
      syncClient = injector.getInstance(GoGridClient.class);
   }

   @Override
   protected Module createModule() {
      return new GoGridRestClientModule() {
         @Override
         protected void configure() {
            Names.bindProperties(binder(), new GoGridPropertiesBuilder(new Properties())
                     .withCredentials("user", "key").build());
            install(new NullLoggingModule());
            super.configure();
         }

         @Override
         protected Long provideTimeStamp(@TimeStamp Supplier<Long> cache) {
            return 11111111l;
         }
      };
   }

   @Override
   protected void checkFilters(GeneratedHttpRequest<GoGridAsyncClient> httpMethod) {

   }
}