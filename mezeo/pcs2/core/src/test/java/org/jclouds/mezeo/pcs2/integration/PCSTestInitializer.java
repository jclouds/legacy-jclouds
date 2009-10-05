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
package org.jclouds.mezeo.pcs2.integration;

import java.net.URI;

import org.jclouds.blobstore.integration.internal.BaseTestInitializer;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.mezeo.pcs2.PCSConnection;
import org.jclouds.mezeo.pcs2.PCSContext;
import org.jclouds.mezeo.pcs2.PCSContextBuilder;
import org.jclouds.mezeo.pcs2.PCSContextFactory;
import org.jclouds.mezeo.pcs2.config.StubPCSBlobStoreModule;
import org.jclouds.mezeo.pcs2.domain.ContainerMetadata;
import org.jclouds.mezeo.pcs2.domain.FileMetadata;
import org.jclouds.mezeo.pcs2.domain.PCSFile;

import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
public class PCSTestInitializer extends
         BaseTestInitializer<PCSConnection, ContainerMetadata, FileMetadata, PCSFile> {

   @Override
   protected PCSContext createLiveContext(Module configurationModule, String url, String app,
            String account, String key) {
      return new PCSContextBuilder(URI.create(url), account, key).relaxSSLHostname().withModules(
               configurationModule, new Log4JLoggingModule()).buildContext();
   }

   @Override
   protected PCSContext createStubContext() {
      return PCSContextFactory.createContext(URI.create("http://localhost/stubpcs"), "user",
               "pass", new StubPCSBlobStoreModule());
   }
}