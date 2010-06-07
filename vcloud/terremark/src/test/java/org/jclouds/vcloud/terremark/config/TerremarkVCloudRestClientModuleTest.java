/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.vcloud.terremark.config;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.easymock.classextension.EasyMock.createMock;
import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.jclouds.http.TransformingHttpCommandExecutorService;
import org.jclouds.rest.config.RestModule;
import org.jclouds.util.Utils;
import org.jclouds.vcloud.terremark.TerremarkVCloudPropertiesBuilder;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "vcloud.TerremarkVCloudRestClientModuleTest")
public class TerremarkVCloudRestClientModuleTest {

   protected Injector createInjector() {
      return Guice.createInjector(new TerremarkVCloudRestClientModule(), new RestModule(),
               new AbstractModule() {
                  @Override
                  protected void configure() {
                     Names.bindProperties(binder(), checkNotNull(
                              new TerremarkVCloudPropertiesBuilder("user", "pass").build(),
                              "properties"));
                     bind(TransformingHttpCommandExecutorService.class).toInstance(
                              createMock(TransformingHttpCommandExecutorService.class));
                  }
               });
   }

   @Test
   void postStrings() throws IOException {
      assertEquals(createInjector().getInstance(
               Key.get(String.class, Names.named("CreateInternetService"))), Utils
               .toStringAndClose(getClass().getResourceAsStream(
                        "/terremark/CreateInternetService.xml")));
      assertEquals(createInjector().getInstance(
               Key.get(String.class, Names.named("CreateNodeService"))),
               Utils.toStringAndClose(getClass().getResourceAsStream(
                        "/terremark/CreateNodeService.xml")));
   }

}