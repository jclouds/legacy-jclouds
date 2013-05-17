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
package org.jclouds.ultradns.ws.parse;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.ultradns.ws.domain.ZoneProperties;
import org.jclouds.ultradns.ws.xml.ZonePropertiesHandler;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(testName = "GetGeneralPropertiesForZoneResponseTest")
public class GetGeneralPropertiesForZoneResponseTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/zoneproperties.xml");

      ZoneProperties expected = expected();

      ZonePropertiesHandler handler = injector.getInstance(ZonePropertiesHandler.class);
      ZoneProperties result = factory.create(handler).parse(is);

      assertEquals(result.toString(), expected.toString());
   }

   public ZoneProperties expected() {
      return ZoneProperties.builder()
                           .name("jclouds.org.")
                           .typeCode(1)
                           .resourceRecordCount(17)
                           .modified(new SimpleDateFormatDateService().iso8601DateParse("2010-09-05 04:04:17.0"))
                           .build();
   }
}
