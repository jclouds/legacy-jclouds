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
package org.jclouds.dynect.v3.features;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.dynect.v3.domain.Session;
import org.jclouds.dynect.v3.domain.SessionCredentials;
import org.jclouds.dynect.v3.internal.BaseDynECTApiLiveTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "SessionApiLiveTest")
public class SessionApiLiveTest extends BaseDynECTApiLiveTest {

   private Session session;

   private void checkSession(Session zone) {
      checkNotNull(zone.getToken(), "Token cannot be null for a Session.");
      checkNotNull(zone.getVersion(),  "Version cannot be null for a Session.");
   }

   @Test
   protected void testCreateSession() {
      SessionCredentials credentials = SessionCredentials.builder()
                                                         .customerName(identity.substring(0, identity.indexOf(':')))
                                                         .userName(identity.substring(identity.indexOf(':') + 1))
                                                         .password(credential).build();
      session = api().login(credentials);
      checkSession(session);
   }

   protected SessionApi api() {
      return api.getSessionApi();
   }
   
   @AfterClass(groups = { "integration", "live" })
   @Override
   protected void tearDown() {
      if (session != null)
         api().logout(session.getToken());
      super.tearDown();
   }
}
