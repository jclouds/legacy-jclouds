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

import javax.inject.Inject;
import javax.inject.Provider;

import org.jclouds.blobstore.functions.CalculateSize;
import org.jclouds.mezeo.pcs2.domain.MutableFileInfo;
import org.jclouds.mezeo.pcs2.domain.PCSFile;
import org.jclouds.mezeo.pcs2.domain.internal.PCSFileImpl;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;

/**
 * Configures the domain object mappings needed for all PCS implementations
 * 
 * @author Adrian Cole
 */
public class PCSObjectModule extends AbstractModule {

   /**
    * explicit factories are created here as it has been shown that Assisted Inject is extremely
    * inefficient. http://code.google.com/p/google-guice/issues/detail?id=435
    */
   @Override
   protected void configure() {
      bind(PCSFile.Factory.class).to(PCSFileFactory.class).in(Scopes.SINGLETON);
   }

   private static class PCSFileFactory implements PCSFile.Factory {
      @Inject
      CalculateSize calculateSize;
      @Inject
      Provider<MutableFileInfo> metadataProvider;

      public PCSFile create(MutableFileInfo metadata) {
         return new PCSFileImpl(calculateSize, metadata != null ? metadata : metadataProvider.get());
      }
   }

   @Provides
   PCSFile providePCSFile(PCSFile.Factory factory) {
      return factory.create(null);
   }
}