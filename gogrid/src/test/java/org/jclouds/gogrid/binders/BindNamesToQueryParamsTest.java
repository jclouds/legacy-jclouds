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

package org.jclouds.gogrid.binders;

import static org.testng.Assert.assertEquals;

import java.net.URI;

import javax.inject.Provider;
import javax.ws.rs.core.UriBuilder;

import org.jboss.resteasy.specimpl.UriBuilderImpl;
import org.jclouds.http.HttpRequest;
import org.testng.annotations.Test;

/**
 * Tests that name bindings are proper for request
 * 
 * @author Oleksiy Yarmula
 */
public class BindNamesToQueryParamsTest {

   @Test
   public void testBinding() {
      String[] input = { "hello", "world" };

      HttpRequest request = new HttpRequest("GET", URI.create("http://momma/"));

      BindNamesToQueryParams binder = new BindNamesToQueryParams(new Provider<UriBuilder>() {

         @Override
         public UriBuilder get() {
            return new UriBuilderImpl();
         }

      });

      binder.bindToRequest(request, input);

      assertEquals(request.getRequestLine(), "GET http://momma/?name=hello&name=world HTTP/1.1");
   }

}
