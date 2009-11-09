package org.jclouds.aws.ec2.config;

import static org.testng.Assert.assertEquals;

import org.jclouds.aws.ec2.reference.EC2Constants;
import org.jclouds.aws.handlers.AWSClientErrorRetryHandler;
import org.jclouds.aws.handlers.AWSRedirectionRetryHandler;
import org.jclouds.aws.handlers.ParseAWSErrorFromXmlContent;
import org.jclouds.concurrent.WithinThreadExecutorService;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.http.handlers.DelegatingErrorHandler;
import org.jclouds.http.handlers.DelegatingRetryHandler;
import org.jclouds.util.Jsr330;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ec2.EC2RestClientModuleTest")
public class EC2RestClientModuleTest {

   Injector createInjector() {
      return Guice.createInjector(new EC2RestClientModule(), new ExecutorServiceModule(
               new WithinThreadExecutorService()), new ParserModule(), new AbstractModule() {
         @Override
         protected void configure() {
            bindConstant().annotatedWith(Jsr330.named(EC2Constants.PROPERTY_AWS_ACCESSKEYID)).to(
                     "user");
            bindConstant().annotatedWith(Jsr330.named(EC2Constants.PROPERTY_AWS_SECRETACCESSKEY))
                     .to("key");
            bindConstant().annotatedWith(Jsr330.named(EC2Constants.PROPERTY_EC2_ENDPOINT)).to(
                     "http://localhost");
            bindConstant().annotatedWith(Jsr330.named(EC2Constants.PROPERTY_EC2_EXPIREINTERVAL))
                     .to(30);
         }
      });
   }

   @Test
   void testServerErrorHandler() {
      DelegatingErrorHandler handler = createInjector().getInstance(DelegatingErrorHandler.class);
      assertEquals(handler.getServerErrorHandler().getClass(), ParseAWSErrorFromXmlContent.class);
   }

   @Test
   void testClientErrorHandler() {
      DelegatingErrorHandler handler = createInjector().getInstance(DelegatingErrorHandler.class);
      assertEquals(handler.getClientErrorHandler().getClass(), ParseAWSErrorFromXmlContent.class);
   }

   @Test
   void testClientRetryHandler() {
      DelegatingRetryHandler handler = createInjector().getInstance(DelegatingRetryHandler.class);
      assertEquals(handler.getClientErrorRetryHandler().getClass(),
               AWSClientErrorRetryHandler.class);
   }

   @Test
   void testRedirectionRetryHandler() {
      DelegatingRetryHandler handler = createInjector().getInstance(DelegatingRetryHandler.class);
      assertEquals(handler.getRedirectionRetryHandler().getClass(),
               AWSRedirectionRetryHandler.class);
   }

}