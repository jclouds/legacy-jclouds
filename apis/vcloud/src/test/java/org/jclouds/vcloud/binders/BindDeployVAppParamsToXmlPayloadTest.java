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
package org.jclouds.vcloud.binders;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Map;

import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.vcloud.internal.BasePayloadTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

/**
 * Tests behavior of {@code BindDeployVAppParamsToXmlPayload}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class BindDeployVAppParamsToXmlPayloadTest extends BasePayloadTest {

   public void testPowerOnTrue() throws IOException {
      String expected = "<DeployVAppParams xmlns=\"http://www.vmware.com/vcloud/v1\" powerOn=\"true\"/>";

      GeneratedHttpRequest request = requestForArgs(ImmutableList.<Object> of());

      BindDeployVAppParamsToXmlPayload binder = injector.getInstance(BindDeployVAppParamsToXmlPayload.class);

      Map<String, Object> map = Maps.newHashMap();
      map.put("powerOn", "true");
      assertEquals(binder.bindToRequest(request, map).getPayload().getRawContent(), expected);
   }

   public void testDefault() throws IOException {
      String expected = "<DeployVAppParams xmlns=\"http://www.vmware.com/vcloud/v1\"/>";

      GeneratedHttpRequest request = requestForArgs(ImmutableList.<Object> of());

      BindDeployVAppParamsToXmlPayload binder = injector.getInstance(BindDeployVAppParamsToXmlPayload.class);

      Map<String, Object> map = Maps.newHashMap();
      assertEquals(binder.bindToRequest(request, map).getPayload().getRawContent(), expected);
   }
}
