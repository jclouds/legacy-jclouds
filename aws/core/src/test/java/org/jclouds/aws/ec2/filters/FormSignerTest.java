package org.jclouds.aws.ec2.filters;

import static org.testng.Assert.assertEquals;

import org.jclouds.aws.ec2.config.EC2RestClientModule;
import org.jclouds.aws.ec2.reference.EC2Constants;
import org.jclouds.concurrent.WithinThreadExecutorService;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.util.Jsr330;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

@Test(groups = "unit", testName = "ec2.FormSignerTest")
public class FormSignerTest {

   private Injector injector;
   private FormSigner filter;

   @Test
   void testBuildCanonicalizedString() {
      assertEquals(
               filter.buildCanonicalizedString(new ImmutableMultimap.Builder<String, String>().put(
                        "AWSAccessKeyId", "foo").put( "Action","DescribeImages").put(
                        "Expires","2008-02-10T12:00:00Z").put("ImageId.1", "ami-2bb65342").put(
                        "SignatureMethod", "HmacSHA256").put("SignatureVersion", "2").put("Version",
                        "2009-08-15").build()),
               "AWSAccessKeyId=foo&Action=DescribeImages&Expires=2008-02-10T12%3A00%3A00Z&ImageId.1=ami-2bb65342&SignatureMethod=HmacSHA256&SignatureVersion=2&Version=2009-08-15");
   }

   /**
    * before class, as we need to ensure that the filter is threadsafe.
    * 
    */
   @BeforeClass
   protected void createFilter() {
      injector = Guice.createInjector(new EC2RestClientModule(), new ExecutorServiceModule(
               new WithinThreadExecutorService()), new ParserModule(), new AbstractModule() {

         protected void configure() {
            bindConstant().annotatedWith(Jsr330.named(EC2Constants.PROPERTY_AWS_ACCESSKEYID)).to(
                     "foo");
            bindConstant().annotatedWith(Jsr330.named(EC2Constants.PROPERTY_AWS_SECRETACCESSKEY))
                     .to("bar");
            bindConstant().annotatedWith(Jsr330.named(EC2Constants.PROPERTY_EC2_ENDPOINT)).to(
                     "https://ec2.amazonaws.com");
            bindConstant().annotatedWith(Jsr330.named(EC2Constants.PROPERTY_EC2_EXPIREINTERVAL))
                     .to(30);
         }
      });
      filter = injector.getInstance(FormSigner.class);
   }

}