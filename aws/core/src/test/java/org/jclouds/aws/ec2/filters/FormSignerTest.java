/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.aws.ec2.filters;

import static com.google.common.util.concurrent.Executors.sameThreadExecutor;
import static org.testng.Assert.assertEquals;

import org.jclouds.aws.ec2.config.EC2RestClientModule;
import org.jclouds.aws.ec2.reference.EC2Constants;
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
                        "AWSAccessKeyId", "foo").put("Action", "DescribeImages").put("Expires",
                        "2008-02-10T12:00:00Z").put("ImageId.1", "ami-2bb65342").put(
                        "SignatureMethod", "HmacSHA256").put("SignatureVersion", "2").put(
                        "Version", "2009-11-30").build()),
               "AWSAccessKeyId=foo&Action=DescribeImages&Expires=2008-02-10T12%3A00%3A00Z&ImageId.1=ami-2bb65342&SignatureMethod=HmacSHA256&SignatureVersion=2&Version=2009-11-30");
   }

   /**
    * before class, as we need to ensure that the filter is threadsafe.
    * 
    */
   @BeforeClass
   protected void createFilter() {
      injector = Guice.createInjector(new EC2RestClientModule(), new ExecutorServiceModule(
               sameThreadExecutor()), new ParserModule(), new AbstractModule() {

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