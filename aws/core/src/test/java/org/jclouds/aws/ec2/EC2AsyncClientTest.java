package org.jclouds.aws.ec2;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.jclouds.date.DateService;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import com.google.inject.name.Names;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code EC2Client}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ec2.EC2ClientTest")
public class EC2AsyncClientTest extends RestClientTest<EC2AsyncClient> {

   private EC2AsyncClient asyncClient;
   private EC2Client syncClient;

   public void testSync() throws SecurityException, NoSuchMethodException, InterruptedException,
            ExecutionException {
      assert syncClient.getAMIServices() != null;
      assert syncClient.getAvailabilityZoneAndRegionServices() != null;
      assert syncClient.getElasticBlockStoreServices() != null;
      assert syncClient.getElasticIPAddressServices() != null;
      assert syncClient.getElasticLoadBalancerServices() != null;
      assert syncClient.getInstanceServices() != null;
      assert syncClient.getKeyPairServices() != null;
      assert syncClient.getMonitoringServices() != null;
      assert syncClient.getSecurityGroupServices() != null;
   }

   public void testAsync() throws SecurityException, NoSuchMethodException, InterruptedException,
            ExecutionException {
      assert asyncClient.getAMIServices() != null;
      assert asyncClient.getAvailabilityZoneAndRegionServices() != null;
      assert asyncClient.getElasticBlockStoreServices() != null;
      assert asyncClient.getElasticIPAddressServices() != null;
      assert asyncClient.getElasticLoadBalancerServices() != null;
      assert asyncClient.getInstanceServices() != null;
      assert asyncClient.getKeyPairServices() != null;
      assert asyncClient.getMonitoringServices() != null;
      assert asyncClient.getSecurityGroupServices() != null;
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<EC2AsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<EC2AsyncClient>>() {
      };
   }

   @BeforeClass
   @Override
   protected void setupFactory() {
      super.setupFactory();
      asyncClient = injector.getInstance(EC2AsyncClient.class);
      syncClient = injector.getInstance(EC2Client.class);
   }

   @Override
   protected Module createModule() {
      return new org.jclouds.aws.ec2.config.EC2RestClientModule() {
         @Override
         protected void configure() {
            Names.bindProperties(binder(), new EC2PropertiesBuilder(new Properties())
                     .withCredentials("user", "key").build());
            install(new NullLoggingModule());
            super.configure();
         }

         @Override
         protected String provideTimeStamp(DateService dateService, int expiration) {
            return "2009-11-08T15:54:08.897Z";
         }
      };
   }

   @Override
   protected void checkFilters(GeneratedHttpRequest<EC2AsyncClient> httpMethod) {

   }
}