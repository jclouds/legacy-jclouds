/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.aws.ec2;

import static org.jclouds.aws.ec2.reference.AWSEC2Constants.PROPERTY_EC2_AMI_QUERY;
import static org.jclouds.ec2.reference.EC2Constants.PROPERTY_EC2_AMI_OWNERS;
import static org.testng.Assert.assertEquals;

import java.util.Map;
import java.util.Properties;

import org.jclouds.ContextBuilder;
import org.jclouds.aws.ec2.compute.config.ImageQuery;
import org.testng.annotations.Test;

import com.google.inject.Key;
import com.google.inject.TypeLiteral;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "AWSEC2ContextBuilderTest")
public class AWSEC2ContextBuilderTest {
   private Map<String, String> queriesForProperties(Properties input) {
      return ContextBuilder.newBuilder(new AWSEC2ProviderMetadata()).overrides(input).credentials("foo", "bar")
               .buildInjector().getInstance(Key.get(new TypeLiteral<Map<String, String>>() {
               }, ImageQuery.class));
   }

   public void testConvertImageSyntax() {
      Properties input = new Properties();
      input.setProperty(PROPERTY_EC2_AMI_OWNERS, "137112412989,063491364108,099720109477,411009282317");
      Map<String, String> queries = queriesForProperties(input);
      assertEquals(queries.get(PROPERTY_EC2_AMI_OWNERS), null);
      assertEquals(queries.get(PROPERTY_EC2_AMI_QUERY),
               "owner-id=137112412989,063491364108,099720109477,411009282317;state=available;image-type=machine");
   }

   public void testConvertImageSyntaxWhenStar() {
      Properties input = new Properties();
      input.setProperty(PROPERTY_EC2_AMI_OWNERS, "*");
      Map<String, String> queries = queriesForProperties(input);
      assertEquals(queries.get(PROPERTY_EC2_AMI_OWNERS), null);
      assertEquals(queries.get(PROPERTY_EC2_AMI_QUERY), "state=available;image-type=machine");
   }

   public void testBlankAmiOwnersRemovesAmiQuery() {
      Properties input = new Properties();
      input.setProperty(PROPERTY_EC2_AMI_OWNERS, "");
      Map<String, String> queries = queriesForProperties(input);
      assertEquals(queries.get(PROPERTY_EC2_AMI_OWNERS), null);
      assertEquals(queries.get(PROPERTY_EC2_AMI_QUERY), null);
   }
}
