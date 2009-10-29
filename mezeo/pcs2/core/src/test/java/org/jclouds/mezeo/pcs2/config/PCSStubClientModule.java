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
package org.jclouds.mezeo.pcs2.config;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jclouds.mezeo.pcs2.PCS;
import org.jclouds.mezeo.pcs2.PCSClient;
import org.jclouds.mezeo.pcs2.domain.PCSFile;
import org.jclouds.mezeo.pcs2.endpoints.RootContainer;
import org.jclouds.mezeo.pcs2.internal.PCSStubClient;
import org.jclouds.rest.ConfiguresRestClient;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

/**
 * adds a stub alternative to invoking PCSBlob
 * 
 * @author Adrian Cole
 */
@ConfiguresRestClient
public class PCSStubClientModule extends AbstractModule {

   static final ConcurrentHashMap<String, Map<String, PCSFile>> map = new ConcurrentHashMap<String, Map<String, PCSFile>>();

   protected void configure() {
      bind(new TypeLiteral<Map<String, Map<String, PCSFile>>>() {
      }).toInstance(map);
      bind(PCSClient.class).to(PCSStubClient.class).asEagerSingleton();
      bind(URI.class).annotatedWith(PCS.class).toInstance(URI.create("https://localhost/pcsblob"));
      bind(URI.class).annotatedWith(RootContainer.class).toInstance(
               URI.create("http://localhost/root"));
   }
}