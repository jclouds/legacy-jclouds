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
package org.jclouds.aws.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import org.jclouds.aws.domain.SessionCredentials;
import org.jclouds.aws.xml.SessionCredentialsHandler;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "SessionCredentialsHandlerTest")
public class SessionCredentialsHandlerTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/credentials.xml");

      SessionCredentials expected = expected();

      SessionCredentialsHandler handler = injector.getInstance(SessionCredentialsHandler.class);
      SessionCredentials result = factory.create(handler).parse(is);

      assertEquals(result, expected);
      assertEquals(result.getAccessKeyId(), expected.getAccessKeyId());
      assertEquals(result.getSecretAccessKey(), expected.getSecretAccessKey());
      assertEquals(result.getSessionToken(), expected.getSessionToken());
      assertEquals(result.getExpiration(), expected.getExpiration());
   }

   public SessionCredentials expected() {
      return SessionCredentials.builder()
            .accessKeyId("AKIAIOSFODNN7EXAMPLE")
            .secretAccessKey("wJalrXUtnFEMI/K7MDENG/bPxRfiCYzEXAMPLEKEY")
            .sessionToken("AQoEXAMPLEH4aoAH0gNCAPyJxz4BlCFFxWNE1OPTgk5TthT")
            .expiration(new SimpleDateFormatDateService().iso8601DateParse("2011-07-11T19:55:29.611Z")).build();
   }

}
