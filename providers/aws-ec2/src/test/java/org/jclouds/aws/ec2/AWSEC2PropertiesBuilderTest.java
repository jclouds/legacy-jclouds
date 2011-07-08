/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.aws.ec2;

import static org.jclouds.aws.ec2.reference.AWSEC2Constants.PROPERTY_EC2_AMI_QUERY;
import static org.jclouds.aws.ec2.reference.AWSEC2Constants.PROPERTY_EC2_CC_AMI_QUERY;
import static org.jclouds.aws.ec2.reference.AWSEC2Constants.PROPERTY_EC2_CC_AMIs;
import static org.jclouds.ec2.reference.EC2Constants.PROPERTY_EC2_AMI_OWNERS;
import static org.testng.Assert.assertEquals;

import java.util.Properties;

import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "AWSEC2PropertiesBuilderTest")
public class AWSEC2PropertiesBuilderTest {
   public void testConvertCCImageSyntax() {
      Properties input = new Properties();
      input.setProperty(PROPERTY_EC2_CC_AMIs, "us-east-1/ami-321eed5b,us-east-1/ami-7ea24a17");
      Properties props = new AWSEC2PropertiesBuilder(input).build();
      assertEquals(props.getProperty(PROPERTY_EC2_CC_AMIs), null);
      assertEquals(props.getProperty(PROPERTY_EC2_CC_AMI_QUERY), "image-id=ami-321eed5b,ami-7ea24a17");
   }

   public void testConvertImageSyntax() {
      Properties input = new Properties();
      input.setProperty(PROPERTY_EC2_AMI_OWNERS, "137112412989,063491364108,099720109477,411009282317");
      Properties props = new AWSEC2PropertiesBuilder(input).build();
      assertEquals(props.getProperty(PROPERTY_EC2_AMI_OWNERS), null);
      assertEquals(props.getProperty(PROPERTY_EC2_AMI_QUERY),
               "owner-id=137112412989,063491364108,099720109477,411009282317;state=available;image-type=machine");
   }

   public void testConvertImageSyntaxWhenStar() {
      Properties input = new Properties();
      input.setProperty(PROPERTY_EC2_AMI_OWNERS, "*");
      Properties props = new AWSEC2PropertiesBuilder(input).build();
      assertEquals(props.getProperty(PROPERTY_EC2_AMI_OWNERS), null);
      assertEquals(props.getProperty(PROPERTY_EC2_AMI_QUERY), "state=available;image-type=machine");
   }

   public void testConvertImageSyntaxWhenBlank() {
      Properties input = new Properties();
      input.setProperty(PROPERTY_EC2_AMI_OWNERS, "");
      Properties props = new AWSEC2PropertiesBuilder(input).build();
      assertEquals(props.getProperty(PROPERTY_EC2_AMI_OWNERS), null);
      assertEquals(props.getProperty(PROPERTY_EC2_AMI_QUERY), "");
   }
}
