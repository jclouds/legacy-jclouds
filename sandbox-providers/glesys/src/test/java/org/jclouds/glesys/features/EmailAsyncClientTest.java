/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.glesys.features;

import com.google.inject.TypeLiteral;
import org.jclouds.rest.functions.MapHttp4xxCodesToExceptions;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import java.util.Map;

/**
 * Tests annotation parsing of {@code ArchiveAsyncClient}
 * 
 * @author Adam Lowe
 */
@Test(groups = "unit", testName = "EmailAsyncClientTest")
public class EmailAsyncClientTest extends BaseGleSYSAsyncClientTest<EmailAsyncClient> {
   public EmailAsyncClientTest() {
      asyncClientClass = EmailAsyncClient.class;
      remoteServicePrefix = "email";
   }

   public void testList() throws Exception {
      testMethod("listAccounts", "list", "POST", true, ReturnEmptySetOnNotFoundOr404.class, newEntry("domain","test"));
   }

   public void testOverview() throws Exception {
      testMethod("getEmailOverview", "overview", "POST", true, ReturnEmptySetOnNotFoundOr404.class);
   }

   public void testCreateAccount() throws Exception {
      testMethod("createAccount", "createaccount", "POST", false, MapHttp4xxCodesToExceptions.class, 
            newEntry("emailaccount", "jclouds.org"), newEntry("password", "test@jclouds.org"));
   }
   
   public void testCreateAlias() throws Exception {
      testMethod("createAlias", "createalias", "POST", false, MapHttp4xxCodesToExceptions.class,
            newEntry("emailalias", "test2@jclouds.org"), newEntry("goto", "test@jclouds.org"));
   }
   
   public void testEditAlias() throws Exception {
      testMethod("editAlias", "editalias", "POST", false, MapHttp4xxCodesToExceptions.class,
            newEntry("emailalias", "test2@jclouds.org"), newEntry("goto", "test1@jclouds.org"));
   }
   
   @Override
   protected TypeLiteral<RestAnnotationProcessor<EmailAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<EmailAsyncClient>>() {
      };
   }
}
