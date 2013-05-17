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

import static org.jclouds.util.Strings2.toStringAndClose;
import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.vcloud.internal.BasePayloadTest;
import org.jclouds.vcloud.options.CloneVAppTemplateOptions;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * Tests behavior of {@code BindCloneVAppTemplateParamsToXmlPayload}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class BindCloneVAppTemplateParamsToXmlPayloadTest extends BasePayloadTest {
   public void testWithDescription() throws IOException {
      String expected = toStringAndClose(getClass().getResourceAsStream("/copyVAppTemplate.xml"));
      
      CloneVAppTemplateOptions options = new CloneVAppTemplateOptions()
            .description("The description of the new vAppTemplate");
      GeneratedHttpRequest request = requestForArgs(ImmutableList.<Object> of(options));

      BindCloneVAppTemplateParamsToXmlPayload binder = injector
            .getInstance(BindCloneVAppTemplateParamsToXmlPayload.class);

      Builder<String, Object> map = ImmutableMap.builder();
      map.put("name", "new-linux-server");
      map.put("Source", "https://vcenterprise.bluelock.com/api/v1.0/vAppTemplate/201");

      assertEquals(binder.bindToRequest(request, map.build()).getPayload().getRawContent(), expected);
   }

   public void testWithDescriptionSourceDelete() throws IOException {
      String expected = toStringAndClose(getClass().getResourceAsStream("/moveVAppTemplate.xml"));

      CloneVAppTemplateOptions options = new CloneVAppTemplateOptions()
            .description("The description of the new vAppTemplate");
      GeneratedHttpRequest request = requestForArgs(ImmutableList.<Object> of(options));

      BindCloneVAppTemplateParamsToXmlPayload binder = injector
            .getInstance(BindCloneVAppTemplateParamsToXmlPayload.class);

      Builder<String, Object> map = ImmutableMap.builder();
      map.put("name", "new-linux-server");
      map.put("Source", "https://vcenterprise.bluelock.com/api/v1.0/vAppTemplate/201");
      map.put("IsSourceDelete", "true");

      assertEquals(binder.bindToRequest(request, map.build()).getPayload().getRawContent(), expected);
   }

   public void testDefault() throws IOException {
      String expected = toStringAndClose(getClass().getResourceAsStream("/copyVAppTemplate-default.xml"));

      GeneratedHttpRequest request = requestForArgs(ImmutableList.<Object> of());

      BindCloneVAppTemplateParamsToXmlPayload binder = injector
            .getInstance(BindCloneVAppTemplateParamsToXmlPayload.class);

      Builder<String, Object> map = ImmutableMap.builder();
      map.put("name", "my-vapptemplate");
      map.put("Source", "https://vcenterprise.bluelock.com/api/v1.0/vAppTemplate/4181");

      assertEquals(binder.bindToRequest(request, map.build()).getPayload().getRawContent(), expected);
   }
}
