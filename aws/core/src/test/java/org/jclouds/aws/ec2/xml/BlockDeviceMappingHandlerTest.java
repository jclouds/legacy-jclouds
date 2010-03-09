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
import java.util.Map;

import org.jclouds.aws.ec2.domain.Attachment;
import org.jclouds.aws.ec2.domain.RunningInstance.EbsBlockDevice;
import org.jclouds.date.DateService;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

/**
 * Tests behavior of {@code BlockDeviceMappingHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ec2.BlockDeviceMappingHandlerTest")
public class BlockDeviceMappingHandlerTest extends BaseHandlerTest {
   public void testApplyInputStream() {

      InputStream is = getClass().getResourceAsStream(
               "/ec2/describe_image_attribute_blockDeviceMapping.xml");

      DateService dateService = injector.getInstance(DateService.class);
      Map<String, EbsBlockDevice> expected = ImmutableMap.<String, EbsBlockDevice> of("/dev/sda1",
               new EbsBlockDevice("vol-d74b82be", Attachment.Status.ATTACHED, dateService
                        .iso8601DateParse("2010-02-20T18:25:26.000Z"), true), "/dev/sdf",
               new EbsBlockDevice("vol-another", Attachment.Status.DETACHED, dateService
                        .iso8601DateParse("2010-02-20T19:26:26.000Z"), false));

      Map<String, EbsBlockDevice> result = factory.create(
               injector.getInstance(BlockDeviceMappingHandler.class)).parse(is);

      assertEquals(result, expected);
   }
}
