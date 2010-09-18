#set( $lcaseProviderName = ${providerName.toLowerCase()} )
#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.rest.RestContextFactory.contextSpec;
import static org.jclouds.rest.RestContextFactory.createContext;
import static org.testng.Assert.assertNotNull;

import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rest.RestContext;

import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Tests behavior of {@code ${providerName}Client}
 * 
 * @author ${author}
 */
@Test(groups = "live", testName = "${lcaseProviderName}.${providerName}ClientLiveTest")
public class ${providerName}ClientLiveTest {

   protected RestContext<${providerName}Client, ${providerName}AsyncClient> context;
   protected String provider = "${lcaseProviderName}";
   protected String identity;
   protected String credential;
   protected String endpoint;
   protected String apiVersion;

   protected void setupCredentials() {
      identity = checkNotNull(System.getProperty("test.${lcaseProviderName}.identity"), "test.${lcaseProviderName}.identity");
      credential = checkNotNull(System.getProperty("test.${lcaseProviderName}.credential"), "test.${lcaseProviderName}.credential");
      endpoint = checkNotNull(System.getProperty("test.${lcaseProviderName}.endpoint"), "test.${lcaseProviderName}.endpoint");
      apiVersion = checkNotNull(System.getProperty("test.${lcaseProviderName}.apiversion"), "test.${lcaseProviderName}.apiversion");
   }
   
   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      setupCredentials();
      context = createContext(contextSpec(provider, endpoint, apiVersion, identity, credential,
               ${providerName}Client.class, ${providerName}AsyncClient.class), ImmutableSet.<Module> of(new Log4JLoggingModule()));
   }

   @Test
   public void testList() throws Exception {
      String response = context.getApi().list();
      assertNotNull(response);
   }

   @Test
   public void testGet() throws Exception {
      String response = context.getApi().get(1l);
      assertNotNull(response);
   }

   /*
    * TODO: add tests for ${providerName} interface methods
    */
}
