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
import org.jclouds.glesys.options.DomainOptions;
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
@Test(groups = "unit", testName = "DomainAsyncClientTest")
public class DomainAsyncClientTest extends BaseGleSYSAsyncClientTest<DomainAsyncClient> {
   public DomainAsyncClientTest() {
      asyncClientClass = DomainAsyncClient.class;
      remoteServicePrefix = "domain";
   }
   
   private Map.Entry<String, String> domainName = newEntry("domain", "cl666666someuser");
   
   public void testListDomains() throws Exception {
      testMethod("listDomains", "list", "POST", true, ReturnEmptySetOnNotFoundOr404.class);
   }
   
   public void testAddDomain() throws Exception {
      testMethod("addDomain", "add", "POST", false, MapHttp4xxCodesToExceptions.class, newEntry("name", "cl66666_x"),
            DomainOptions.Builder.primaryNameServer("ns1.somewhere.x").expire("1").minimum("1").refresh("1").
                  responsiblePerson("Tester").retry("1").ttl(1));
      testMethod("addDomain", "add", "POST", false, MapHttp4xxCodesToExceptions.class, newEntry("name", "cl66666_x"));
   }

   public void testEditDomain() throws Exception {
      testMethod("editDomain", "edit", "POST", false, MapHttp4xxCodesToExceptions.class, newEntry("domain", "x"));
   }

   public void testDeleteDomain() throws Exception {
      testMethod("deleteDomain", "delete", "POST", false, MapHttp4xxCodesToExceptions.class, domainName);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<DomainAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<DomainAsyncClient>>() {
      };
   }
}
