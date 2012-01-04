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

import java.util.Map;

import org.jclouds.rest.functions.MapHttp4xxCodesToExceptions;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.inject.TypeLiteral;

/**
 * Tests annotation parsing of {@code ArchiveAsyncClient}
 * 
 * @author Adam Lowe
 */
@Test(groups = "unit", testName = "ArchiveAsyncClientTest")
public class ArchiveAsyncClientTest extends BaseGleSYSAsyncClientTest<ArchiveAsyncClient> {
   public ArchiveAsyncClientTest() {
      asyncClientClass = ArchiveAsyncClient.class;
      remoteServicePrefix = "archive";
   }
   
   private Map.Entry<String, String> userName = newEntry("username", "x");
   
   public void testListArchives() throws Exception {
      testMethod("listArchives", "list", "POST", true, ReturnEmptySetOnNotFoundOr404.class);
   }
   
   public void testArchiveDetails() throws Exception {
      testMethod("getArchiveDetails", "details", "POST", true, ReturnNullOnNotFoundOr404.class, userName);
   }
   
   public void testCreateArchive() throws Exception {
      testMethod("createArchive", "create", "POST", false, MapHttp4xxCodesToExceptions.class, userName,
            newEntry("password", "somepass"), newEntry("size", 5));
   }
   
   public void testDeleteArchive() throws Exception {
      testMethod("deleteArchive", "delete", "POST", false, MapHttp4xxCodesToExceptions.class, userName);
   }

   public void testResizeArchive() throws Exception {
      testMethod("resizeArchive", "resize", "POST", false, MapHttp4xxCodesToExceptions.class, userName,
            newEntry("size", "5 GB"));
   }
   
   public void testChangeArchivePassword() throws Exception {
      testMethod("changeArchivePassword", "changepassword", "POST", false, MapHttp4xxCodesToExceptions.class, userName,
            newEntry("password", "newpass"));
   }

   public void testGetArchiveAllowedArguments() throws Exception {
      testMethod("getArchiveAllowedArguments", "allowedarguments", "GET", true, ReturnEmptySetOnNotFoundOr404.class);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<ArchiveAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<ArchiveAsyncClient>>() {
      };
   }
}
