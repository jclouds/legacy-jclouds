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

import org.jclouds.http.commands.CommandFactory;
import org.jclouds.http.commands.GetString;
import org.jclouds.http.commands.Head;
import org.jclouds.http.commands.Put;
import org.jclouds.http.commands.callables.xml.ParseSax;
import org.jclouds.http.commands.callables.xml.config.SaxModule;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryProvider;

/**
 * note that all this private factory clutter will go away when the following is implemented @link
 * http://code.google.com/p/google-guice/issues/detail?id=346 it will be replaced with a
 * configuration:
 * 
 * @author Adrian Cole
 */
public class HttpCommandsModule extends AbstractModule {
   protected void configure() {
      bind(CommandFactory.GetStringFactory.class).toProvider(
               FactoryProvider.newFactory(CommandFactory.GetStringFactory.class, GetString.class));
      bind(CommandFactory.HeadFactory.class).toProvider(
               FactoryProvider.newFactory(CommandFactory.HeadFactory.class, Head.class));
      bind(CommandFactory.PutFactory.class).toProvider(
               FactoryProvider.newFactory(CommandFactory.PutFactory.class, Put.class));
      install(new SaxModule());
      bind(CommandFactory.ParseSaxFactory.class).toProvider(
               FactoryProvider.newFactory(new TypeLiteral<CommandFactory.ParseSaxFactory>() {
               }, new TypeLiteral<ParseSax<?>>() {
               }));

   }

}
