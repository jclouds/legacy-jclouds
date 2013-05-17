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
package org.jclouds.trmk.vcloud_0_8.binders;

import static org.testng.Assert.assertEquals;

import java.util.Map;
import java.util.Properties;

import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.trmk.vcloud_0_8.internal.BasePayloadTest;
import org.jclouds.trmk.vcloud_0_8.internal.TerremarkVCloudApiMetadata;
import org.jclouds.trmk.vcloud_0_8.options.CloneVAppOptions;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;

/**
 * Tests behavior of {@code BindCloneVAppParamsToXmlPayload}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class BindCloneVAppParamsToXmlPayloadTest extends BasePayloadTest {
   Injector injector = Guice.createInjector(new AbstractModule() {

      @Override
      protected void configure() {
         Properties props = TerremarkVCloudApiMetadata.defaultProperties();
         props.setProperty("jclouds.vcloud.xml.ns", "http://www.vmware.com/vcloud/v0.8");
         props.setProperty("jclouds.vcloud.xml.schema", "http://vcloud.safesecureweb.com/ns/vcloud.xsd");
         Names.bindProperties(binder(), props);
      }
   });
   
   public void testWithDescriptionDeployOn() throws Exception {
      String expected = Strings2.toStringAndClose(getClass().getResourceAsStream("/cloneVApp.xml"));

      CloneVAppOptions options = new CloneVAppOptions().deploy().powerOn().withDescription(
               "The description of the new vApp");
      GeneratedHttpRequest request = requestForArgs(ImmutableList.<Object> of(options));

      BindCloneVAppParamsToXmlPayload binder = injector.getInstance(BindCloneVAppParamsToXmlPayload.class);

      Map<String, Object> map = Maps.newHashMap();
      map.put("newName", "new-linux-server");
      map.put("vApp", "https://vcloud.safesecureweb.com/api/v0.8/vapp/201");
      assertEquals(binder.bindToRequest(request, map).getPayload().getRawContent(), expected);
   }

   public void testDefault() throws Exception {
      String expected = Strings2.toStringAndClose(getClass().getResourceAsStream("/cloneVApp-default.xml"));

      GeneratedHttpRequest request = requestForArgs(ImmutableList.<Object> of());

      BindCloneVAppParamsToXmlPayload binder = injector.getInstance(BindCloneVAppParamsToXmlPayload.class);

      Map<String, Object> map = Maps.newHashMap();
      map.put("newName", "my-vapp");
      map.put("vApp", "https://vcloud.safesecureweb.com/api/v0.8/vapp/4181");
      assertEquals(binder.bindToRequest(request, map).getPayload().getRawContent(), expected);
   }
}
