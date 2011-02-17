/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import static org.jclouds.aws.reference.AWSConstants.PROPERTY_HEADER_TAG;
import static org.testng.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.jclouds.PropertiesBuilder;
import org.jclouds.date.TimeStamp;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.rest.RequestSigner;
import org.jclouds.rest.RestContextBuilder;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.rest.RestContextSpec;
import org.jclouds.rest.BaseRestClientTest.MockModule;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.name.Names;

/**
 * Tests behavior of {@code FormSigner}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "FormSignerTest")
public class FormSignerTest {
   @SuppressWarnings("unchecked")
   public static final RestContextSpec<Map, List> DUMMY_SPEC = new RestContextSpec<Map, List>("provider", "endpoint",
            "apiVersion", "", "identity", "credential", Map.class, List.class, PropertiesBuilder.class,
            (Class) RestContextBuilder.class, ImmutableList.<Module> of(new MockModule(), new NullLoggingModule(),
                     new AbstractModule() {
                        @Override
                        protected void configure() {
                           bind(RequestSigner.class).to(FormSigner.class);
                           bind(String.class).annotatedWith(Names.named(PROPERTY_HEADER_TAG)).toInstance("amz");
                           bind(String.class).annotatedWith(TimeStamp.class).toInstance("2009-11-08T15:54:08.897Z");
                        }

                     }));

   @Test
   void testBuildCanonicalizedString() {
      FormSigner filter = RestContextFactory.createContextBuilder(DUMMY_SPEC).buildInjector().getInstance(
               FormSigner.class);

      assertEquals(
               filter.buildCanonicalizedString(new ImmutableMultimap.Builder<String, String>().put("AWSAccessKeyId",
                        "foo").put("Action", "DescribeImages").put("Expires", "2008-02-10T12:00:00Z").put("ImageId.1",
                        "ami-2bb65342").put("SignatureMethod", "HmacSHA256").put("SignatureVersion", "2").put(
                        "Version", "2010-06-15").build()),
               "AWSAccessKeyId=foo&Action=DescribeImages&Expires=2008-02-10T12%3A00%3A00Z&ImageId.1=ami-2bb65342&SignatureMethod=HmacSHA256&SignatureVersion=2&Version=2010-06-15");
   }

}