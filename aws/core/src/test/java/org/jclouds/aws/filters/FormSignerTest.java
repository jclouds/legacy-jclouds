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
package org.jclouds.aws.filters;

import static com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor;
import static org.testng.Assert.assertEquals;

import java.util.Date;

import javax.inject.Named;

import org.jclouds.Constants;
import org.jclouds.aws.reference.AWSConstants;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.date.DateService;
import org.jclouds.date.TimeStamp;
import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.util.Jsr330;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;

@Test(groups = "unit", testName = "aws.FormSignerTest")
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
      injector = Guice.createInjector(new ParserModule(), new ExecutorServiceModule(
               sameThreadExecutor(), sameThreadExecutor()), new AbstractModule() {

         protected void configure() {
            bindConstant().annotatedWith(Jsr330.named(AWSConstants.PROPERTY_AWS_ACCESSKEYID)).to(
                     "foo");
            bindConstant().annotatedWith(Jsr330.named(AWSConstants.PROPERTY_AWS_SECRETACCESSKEY))
                     .to("bar");
            bindConstant().annotatedWith(Jsr330.named(AWSConstants.PROPERTY_AWS_EXPIREINTERVAL))
                     .to(30);
            bindConstant().annotatedWith(Jsr330.named(Constants.PROPERTY_IO_WORKER_THREADS))
                     .to("1");
            bindConstant().annotatedWith(Jsr330.named(Constants.PROPERTY_USER_THREADS)).to("1");
         }

         @SuppressWarnings("unused")
         @Provides
         @TimeStamp
         protected String provideTimeStamp(final DateService dateService,
                  @Named(AWSConstants.PROPERTY_AWS_EXPIREINTERVAL) final int expiration) {
            return dateService.iso8601DateFormat(new Date(System.currentTimeMillis()
                     + (expiration * 1000)));
         }
      });
      filter = injector.getInstance(FormSigner.class);
   }

}