/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.atmos.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.inject.Inject;
import javax.inject.Provider;

import org.jclouds.atmos.AtmosClient;
import org.jclouds.atmos.blobstore.functions.BlobToObject;
import org.jclouds.atmos.domain.AtmosError;
import org.jclouds.atmos.domain.AtmosObject;
import org.jclouds.atmos.filters.SignRequest;
import org.jclouds.atmos.options.PutOptions;
import org.jclouds.atmos.xml.ErrorHandler;
import org.jclouds.blobstore.KeyAlreadyExistsException;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.crypto.Crypto;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.util.Assertions;

import com.google.common.base.Supplier;

/**
 * Encryption, Hashing, and IO Utilities needed to sign and verify Atmos Storage requests and
 * responses.
 * 
 * @author Adrian Cole
 */
public class AtmosUtils {

   @Inject
   SignRequest signer;

   @Inject
   ParseSax.Factory factory;

   @Inject
   Provider<ErrorHandler> errorHandlerProvider;

   public AtmosError parseAtmosErrorFromContent(HttpCommand command, HttpResponse response, InputStream content)
            throws HttpException {
      AtmosError error = (AtmosError) factory.create(errorHandlerProvider.get()).parse(content);
      if (error.getCode() == 1032) {
         error.setStringSigned(signer.createStringToSign(command.getCurrentRequest()));
      }
      return error;

   }

   public static String putBlob(final AtmosClient sync, Crypto crypto, BlobToObject blob2Object, String container,
            Blob blob, PutOptions options) {
      final String path = container + "/" + blob.getMetadata().getName();
      final AtmosObject object = blob2Object.apply(blob);
      
      try {
         sync.createFile(container, object, options);
         
      } catch(KeyAlreadyExistsException e) {
         deleteAndEnsureGone(sync, path);
         sync.createFile(container, object, options);
      }
      return path;
   }

   public static void deleteAndEnsureGone(final AtmosClient sync, final String path) {
      try {
         if (!Assertions.eventuallyTrue(new Supplier<Boolean>() {
            public Boolean get() {
               sync.deletePath(path);
               return !sync.pathExists(path);
            }
         }, 3000)) {
            throw new IllegalStateException(path + " still exists after deleting!");
         }
      } catch (InterruptedException e) {
         throw new IllegalStateException(path + " interrupted during deletion!", e);
      }
   }

   public AtmosError parseAtmosErrorFromContent(HttpCommand command, HttpResponse response, String content)
            throws HttpException {
      return parseAtmosErrorFromContent(command, response, new ByteArrayInputStream(content.getBytes()));
   }

   public static String adjustContainerIfDirOptionPresent(String container,
            org.jclouds.blobstore.options.ListContainerOptions options) {
      if (options != org.jclouds.blobstore.options.ListContainerOptions.NONE) {
         // if (options.isRecursive()) {
         // throw new UnsupportedOperationException("recursive not currently supported in emcsaas");
         // }
         if (options.getDir() != null) {
            container = container + "/" + options.getDir();
         }
      }
      return container;
   }
}
