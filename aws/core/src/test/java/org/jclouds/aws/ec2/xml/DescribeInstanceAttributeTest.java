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
package org.jclouds.aws.ec2.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import org.jclouds.aws.ec2.domain.InstanceType;
import org.jclouds.aws.ec2.domain.Volume.InstanceInitiatedShutdownBehavior;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code InstanceInitiatedShutdownBehaviorHandler}, {@code InstanceTypeHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ec2.DescribeInstanceAttributeTest")
public class DescribeInstanceAttributeTest extends BaseHandlerTest {

   public void testInstanceInitiatedShutdownBehaviorHandler() {
      InputStream is = getClass().getResourceAsStream("/ec2/instanceInitiatedShutdownBehavior.xml");

      InstanceInitiatedShutdownBehaviorHandler handler = injector
               .getInstance(InstanceInitiatedShutdownBehaviorHandler.class);
      InstanceInitiatedShutdownBehavior result = factory.create(handler).parse(is);

      assertEquals(result, InstanceInitiatedShutdownBehavior.STOP);
   }

   public void testInstanceTypeHandler() {
      InputStream is = getClass().getResourceAsStream("/ec2/instanceType.xml");

      InstanceTypeHandler handler = injector.getInstance(InstanceTypeHandler.class);
      InstanceType result = factory.create(handler).parse(is);

      assertEquals(result, InstanceType.M1_SMALL);
   }

   public void testBooleanValueHandler() {
      InputStream is = getClass().getResourceAsStream("/ec2/disableApiTermination.xml");

      BooleanValueHandler handler = injector.getInstance(BooleanValueHandler.class);
      Boolean result = factory.create(handler).parse(is);

      assert !result;
   }

   public void testStringValueHandler() {
      InputStream is = getClass().getResourceAsStream("/ec2/ramdisk.xml");

      StringValueHandler handler = injector.getInstance(StringValueHandler.class);
      String result = factory.create(handler).parse(is);

      assertEquals(result, "ari-a51cf9cc");
   }

   public void testUnencodeStringValueHandler() {
      InputStream is = getClass().getResourceAsStream("/ec2/userData.xml");

      UnencodeStringValueHandler handler = injector.getInstance(UnencodeStringValueHandler.class);
      String result = factory.create(handler).parse(is);

      assertEquals(result, "#!/bin/bash\n");
   }
}