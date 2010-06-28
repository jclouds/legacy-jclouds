package org.jclouds.gogrid;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.jclouds.gogrid.services.BaseGoGridAsyncClientTest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code GoGridClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ec2.GoGridClientTest")
public class GoGridAsyncClientTest extends BaseGoGridAsyncClientTest<GoGridAsyncClient> {

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
   protected void setupFactory() throws IOException {
      super.setupFactory();
      asyncClient = injector.getInstance(GoGridAsyncClient.class);
      syncClient = injector.getInstance(GoGridClient.class);
   }
}