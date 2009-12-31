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
#set( $lcaseClientName = ${clientName.toLowerCase()} )
#set( $ucaseClientName = ${clientName.toUpperCase()} )
#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package ${package};

import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.RestContextImpl;
import ${package}.config.${clientName}RestClientModule;
import ${package}.reference.${clientName}Constants;
import org.testng.annotations.Test;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of modules configured in ${clientName}ContextBuilder
 * 
 * @author ${author}
 */
@Test(groups = "unit", testName = "${lcaseClientName}.${clientName}ContextBuilderTest")
public class ${clientName}ContextBuilderTest {

   public void testNewBuilder() {
      ${clientName}ContextBuilder builder = newBuilder();
      assertEquals(builder.getProperties().getProperty(${clientName}Constants.PROPERTY_${ucaseClientName}_ENDPOINT),
               "${clientEndpoint}");
      assertEquals(builder.getProperties().getProperty(${clientName}Constants.PROPERTY_${ucaseClientName}_USER),
               "user");
      assertEquals(builder.getProperties().getProperty(${clientName}Constants.PROPERTY_${ucaseClientName}_PASSWORD),
               "password");
   }

   public void testBuildContext() {
      RestContext<${clientName}AsyncClient, ${clientName}Client> context = newBuilder().buildContext();
      assertEquals(context.getClass(), RestContextImpl.class);
      assertEquals(context.getAccount(), "user");
      assertEquals(context.getEndPoint(), URI.create("${clientEndpoint}"));
   }

   public void testBuildInjector() {
      Injector i = newBuilder().buildInjector();
      assert i.getInstance(Key.get(new TypeLiteral<RestContext<${clientName}AsyncClient, ${clientName}Client>>() {
      })) != null; // TODO: test all things taken from context
      assert i.getInstance(BasicAuthentication.class) != null;
   }

   protected void testAddContextModule() {
      List<Module> modules = new ArrayList<Module>();
      ${clientName}ContextBuilder builder = newBuilder();
      builder.addContextModule(modules);
      assertEquals(modules.size(), 1);
      assertEquals(modules.get(0).getClass(), ${clientName}RestClientModule.class);
   }

   private ${clientName}ContextBuilder newBuilder() {
       ${clientName}ContextBuilder builder = new ${clientName}ContextBuilder(new ${clientName}PropertiesBuilder(
               "user", "password").build());
      return builder;
   }

   protected void addClientModule() {
      List<Module> modules = new ArrayList<Module>();
      ${clientName}ContextBuilder builder = newBuilder();
      builder.addClientModule(modules);
      assertEquals(modules.size(), 1);
      assertEquals(modules.get(0).getClass(), ${clientName}RestClientModule.class);
   }

}
