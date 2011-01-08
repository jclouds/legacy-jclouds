/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.rackspace.filters;

import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.Date;

import javax.inject.Provider;
import javax.ws.rs.core.UriBuilder;

import org.jclouds.http.HttpRequest;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.sun.jersey.api.uri.UriBuilderImpl;

/**
 * 
 * @author Adrian Cole
 */

@Test(groups = "unit")
public class AddTimestampQueryTest {

   @Test
   public void testApplySetsKey() {
      final Date date = new Date();
      Supplier<Date> dateSupplier = new Supplier<Date>() {

         @Override
         public Date get() {
            return date;
         }

      };

      HttpRequest request = new HttpRequest("GET", URI.create("http://momma/"));

      AddTimestampQuery filter = new AddTimestampQuery(dateSupplier, new Provider<UriBuilder>() {

         @Override
         public UriBuilder get() {
            return new UriBuilderImpl();
         }

      });

      request = filter.filter(request);

      assertEquals(request.getRequestLine(), String.format("GET http://momma/?now=%s HTTP/1.1", date.getTime()));

   }

}
