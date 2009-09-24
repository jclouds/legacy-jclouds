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
package org.jclouds.gae.config;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

import org.jclouds.concurrent.SingleThreadCompatible;
import org.jclouds.concurrent.WithinThreadExecutorService;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.gae.GaeHttpCommandExecutorService;
import org.jclouds.http.HttpCommandExecutorService;
import org.jclouds.util.Jsr330;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests the ability to configure a {@link GaeHttpCommandExecutorService}
 * 
 * @author Adrian Cole
 */
@Test
public class GaeHttpCommandExecutorServiceModuleTest {

   public void testConfigureBindsClient() {
      final Properties properties = new Properties();

      Injector i = Guice.createInjector(
               new ExecutorServiceModule(new WithinThreadExecutorService()),
               new GaeHttpCommandExecutorServiceModule() {
                  @Override
                  protected void configure() {
                     Jsr330.bindProperties(binder(), properties);
                     super.configure();
                  }
               });
      HttpCommandExecutorService client = i.getInstance(HttpCommandExecutorService.class);
      assert client instanceof GaeHttpCommandExecutorService;
      ExecutorService executorService = i.getInstance(ExecutorService.class);
      assert executorService.getClass().isAnnotationPresent(SingleThreadCompatible.class) : Arrays
               .asList(executorService.getClass().getAnnotations());
   }
}
