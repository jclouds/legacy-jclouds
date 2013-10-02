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
package org.jclouds.ec2.parse;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import org.jclouds.date.DateService;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.ec2.domain.PasswordData;
import org.jclouds.ec2.xml.GetPasswordDataResponseHandler;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "GetPasswordDataResponseTest")
public class GetPasswordDataResponseTest extends BaseHandlerTest {
   protected final DateService dateService = new SimpleDateFormatDateService();

   public void test() {
      InputStream is = getClass().getResourceAsStream("/get_passworddata.xml");

      PasswordData expected = expected();

      GetPasswordDataResponseHandler handler = injector.getInstance(GetPasswordDataResponseHandler.class);
      PasswordData result = factory.create(handler).parse(is);

      assertEquals(result.toString(), expected.toString());
  }

   public PasswordData expected() {
      return PasswordData.builder()
                         .instanceId("i-2574e22a")
                         .timestamp(dateService.iso8601DateParse("2012-07-30T07:27:23.000+0000"))
                         .passwordData("TGludXggdmVyc2lvbiAyLjYuMTYteGVuVSAoYnVpbGRlckBwYXRjaGJhdC5hbWF6b25zYSkgKGdj").build();
   }
}
