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

import static org.testng.Assert.assertEquals;

import org.jclouds.aws.ec2.services.BaseEC2AsyncClientTest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.inject.TypeLiteral;

@Test(groups = "unit", testName = "aws.FormSignerTest")
public class FormSignerTest extends BaseEC2AsyncClientTest<String> {

   @Test
   void testBuildCanonicalizedString() {
      assertEquals(
               filter.buildCanonicalizedString(new ImmutableMultimap.Builder<String, String>().put(
                        "AWSAccessKeyId", "foo").put("Action", "DescribeImages").put("Expires",
                        "2008-02-10T12:00:00Z").put("ImageId.1", "ami-2bb65342").put(
                        "SignatureMethod", "HmacSHA256").put("SignatureVersion", "2").put(
                        "Version", "2010-06-15").build()),
               "AWSAccessKeyId=foo&Action=DescribeImages&Expires=2008-02-10T12%3A00%3A00Z&ImageId.1=ami-2bb65342&SignatureMethod=HmacSHA256&SignatureVersion=2&Version=2010-06-15");
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<String>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<String>>() {
      };
   }
}