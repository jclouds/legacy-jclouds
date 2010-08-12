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

package org.jclouds.chef.servlet.suppliers;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.testng.Assert.assertEquals;

import java.util.Properties;

import javax.servlet.ServletContext;

import org.jclouds.chef.servlet.functions.InitParamsToProperties;
import org.jclouds.json.Json;
import org.jclouds.json.config.GsonModule;
import org.jclouds.ohai.servlet.config.ServletOhaiModule;
import org.jclouds.ohai.servlet.suppliers.ServletContextInfoSupplier;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;

/**
 * 
 * @author Adrian Cole
 */

@Test(groups = "unit", testName = "chef.ServletContextInfoSupplierTest")
public class ServletContextInfoSupplierTest {

   @Test
   public void testApply() {

      final ServletContext servletContext = createMock(ServletContext.class);
      final InitParamsToProperties converter = createMock(InitParamsToProperties.class);
      Properties props = new Properties();
      props.setProperty("foo", "bar");

      expect(servletContext.getContextPath()).andReturn("path");
      expect(servletContext.getServerInfo()).andReturn("serverinfo");
      expect(converter.apply(servletContext)).andReturn(props);

      replay(servletContext);
      replay(converter);

      Injector injector = Guice.createInjector(new GsonModule(), new ServletOhaiModule() {
         @SuppressWarnings("unused")
         @Provides
         protected ServletContext provideServletContext() {
            return servletContext;
         }

         @SuppressWarnings("unused")
         @Provides
         protected InitParamsToProperties provideInitParamsToProperties() {
            return converter;
         }
      });

      Json json = injector.getInstance(Json.class);
      ServletContextInfoSupplier ohai = injector.getInstance(ServletContextInfoSupplier.class);
      assertEquals(json.toJson(ohai.get()),
            "{\"path\":{\"server_info\":\"serverinfo\",\"init_params\":{\"foo\":\"bar\"}}}");

   }

   @Test
   public void testApplyNullPath() {

      final ServletContext servletContext = createMock(ServletContext.class);
      final InitParamsToProperties converter = createMock(InitParamsToProperties.class);
      Properties props = new Properties();
      props.setProperty("foo", "bar");

      expect(servletContext.getContextPath()).andReturn(null);
      expect(servletContext.getServerInfo()).andReturn("serverinfo");
      expect(converter.apply(servletContext)).andReturn(props);

      replay(servletContext);
      replay(converter);

      Injector injector = Guice.createInjector(new GsonModule(), new ServletOhaiModule() {
         @SuppressWarnings("unused")
         @Provides
         protected ServletContext provideServletContext() {
            return servletContext;
         }

         @SuppressWarnings("unused")
         @Provides
         protected InitParamsToProperties provideInitParamsToProperties() {
            return converter;
         }
      });

      Json json = injector.getInstance(Json.class);
      ServletContextInfoSupplier ohai = injector.getInstance(ServletContextInfoSupplier.class);
      assertEquals(json.toJson(ohai.get()),
            "{\"/\":{\"server_info\":\"serverinfo\",\"init_params\":{\"foo\":\"bar\"}}}");

   }

   @Test
   public void testApplyEmptyPath() {

      final ServletContext servletContext = createMock(ServletContext.class);
      final InitParamsToProperties converter = createMock(InitParamsToProperties.class);
      Properties props = new Properties();
      props.setProperty("foo", "bar");

      expect(servletContext.getContextPath()).andReturn("");
      expect(servletContext.getServerInfo()).andReturn("serverinfo");
      expect(converter.apply(servletContext)).andReturn(props);

      replay(servletContext);
      replay(converter);

      Injector injector = Guice.createInjector(new GsonModule(), new ServletOhaiModule() {
         @SuppressWarnings("unused")
         @Provides
         protected ServletContext provideServletContext() {
            return servletContext;
         }

         @SuppressWarnings("unused")
         @Provides
         protected InitParamsToProperties provideInitParamsToProperties() {
            return converter;
         }
      });

      Json json = injector.getInstance(Json.class);
      ServletContextInfoSupplier ohai = injector.getInstance(ServletContextInfoSupplier.class);
      assertEquals(json.toJson(ohai.get()),
            "{\"/\":{\"server_info\":\"serverinfo\",\"init_params\":{\"foo\":\"bar\"}}}");

   }
}
