/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.ec2.binders;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.util.Date;
import java.util.Map;

import org.jclouds.ec2.domain.Attachment.Status;
import org.jclouds.ec2.domain.BlockDevice;
import org.jclouds.http.HttpRequest;
import org.testng.annotations.Test;

import com.google.common.collect.Maps;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code BindBlockDeviceMappingToIndexedFormParams}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class BindBlockDeviceMappingToIndexedFormParamsTest {
   Injector injector = Guice.createInjector();
   BindBlockDeviceMappingToIndexedFormParams binder = injector
         .getInstance(BindBlockDeviceMappingToIndexedFormParams.class);

   public void testMappingOrdersLexicographically() {
      Map<String, BlockDevice> mapping = Maps.newLinkedHashMap();
      mapping.put("apple", new BlockDevice("appleId", true));
      Date date = new Date(999999l);
      mapping.put("cranberry", new BlockDevice("cranberry", Status.ATTACHED, date, false));

      HttpRequest request = HttpRequest.builder().method("POST").endpoint("http://localhost")
            .addFormParam("InstanceId", "i-foo").build();
      request = binder.bindToRequest(request, mapping);
      assertEquals(
            request.getPayload().getRawContent(),
            "Action=ModifyInstanceAttribute&BlockDeviceMapping.1.DeviceName=apple&BlockDeviceMapping.1.Ebs.DeleteOnTermination=true&BlockDeviceMapping.1.Ebs.VolumeId=appleId&BlockDeviceMapping.2.DeviceName=cranberry&BlockDeviceMapping.2.Ebs.DeleteOnTermination=false&BlockDeviceMapping.2.Ebs.VolumeId=cranberry&InstanceId=i-foo");
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testMustBeBlockDeviceMapping() {
      HttpRequest request = HttpRequest.builder().method("POST").endpoint("http://localhost").build();;
      binder.bindToRequest(request, new File("foo"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testNullIsBad() {
      HttpRequest request = HttpRequest.builder().method("GET").endpoint("http://momma").build();
      binder.bindToRequest(request, null);
   }
}
