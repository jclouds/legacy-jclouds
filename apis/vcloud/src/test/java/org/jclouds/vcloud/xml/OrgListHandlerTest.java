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
package org.jclouds.vcloud.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;
import java.util.Map;

import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.internal.ReferenceTypeImpl;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

/**
 * Tests behavior of {@code OrgListHandler}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "OrgListHandlerTest")
public class OrgListHandlerTest extends BaseHandlerTest {

   public void testApplyInputStream() {
      InputStream is = getClass().getResourceAsStream("/orglist.xml");

      Map<String, ReferenceType> result = factory.create(injector.getInstance(OrgListHandler.class)).parse(is);
      assertEquals(result, ImmutableMap.of("adrian@jclouds.org", new ReferenceTypeImpl("adrian@jclouds.org",
               "application/vnd.vmware.vcloud.org+xml", URI.create("https://services.vcloudexpress.terremark.com/api/v0.8/org/48"))));
   }
   

   public void testSavvis() {
      InputStream is = getClass().getResourceAsStream("/orglist-savvis.xml");

      Map<String, ReferenceType> result = factory.create(injector.getInstance(OrgListHandler.class)).parse(is);
      assertEquals(result, ImmutableMap.of("SAVVISStation Integration Testing", new ReferenceTypeImpl("SAVVISStation Integration Testing",
               "application/vnd.vmware.vcloud.org+xml", URI.create("https://api.sandbox.symphonyvpdc.savvis.net/rest/api/v0.8/org/100000.0"))));
   }
}
