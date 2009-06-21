/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
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
package org.jclouds.http.commands.config;

import java.net.URI;

import org.jclouds.http.HttpFutureCommand;
import org.jclouds.http.commands.CommandFactory;
import org.jclouds.http.commands.callables.xml.ParseSax;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * // TODO: Adrian: Document this!
 * 
 * @author Adrian Cole
 */
@Test
public class HttpCommandsModuleTest {

   public void testGetString() {
      Injector i = createInjector();
      CommandFactory factory = i.getInstance(CommandFactory.class);
      HttpFutureCommand<String> get = factory.createGetString("/index.html");
      assert get != null;
      assert get.getResponseFuture() != null;
   }

   private Injector createInjector() {
      Injector i = Guice.createInjector(new HttpCommandsModule(), new AbstractModule() {

         @Override
         protected void configure() {
            bind(URI.class).toInstance(URI.create("http://localhost:8080"));
         }

      });
      return i;
   }

   public void testHead() {
      Injector i = createInjector();
      CommandFactory factory = i.getInstance(CommandFactory.class);
      HttpFutureCommand<Boolean> Head = factory.createHead("/index.html");
      assert Head != null;
      assert Head.getResponseFuture() != null;
   }

   public void testGetAndParseXml() {
      Injector i = createInjector();
      CommandFactory factory = i.getInstance(CommandFactory.class);
      HttpFutureCommand<?> GetAndParseXml = factory.createGetAndParseSax("/index.html",
               new ParseSax.HandlerWithResult<String>() {
                  public String getResult() {
                     return "hello";
                  }
               });
      assert GetAndParseXml != null;
      assert GetAndParseXml.getResponseFuture() != null;
   }
}