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
package org.jclouds.http.functions;

import javax.ws.rs.core.UriBuilder;

import org.jclouds.http.functions.config.SaxParserModule;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.sun.jersey.api.uri.UriBuilderImpl;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class BaseHandlerTest {

   protected Injector injector = null;
   protected ParseSax.Factory factory;

   @BeforeTest
   protected void setUpInjector() {
      injector = Guice.createInjector(new SaxParserModule() {
         public void configure() {
            super.configure();
            bind(UriBuilder.class).to(UriBuilderImpl.class);
         }
      });
      factory = injector.getInstance(ParseSax.Factory.class);
      assert factory != null;
   }

   @AfterTest
   protected void tearDownInjector() {
      factory = null;
      injector = null;
   }

}
