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
package org.jclouds.atmosonline.saas.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.inject.Inject;
import javax.inject.Provider;

import org.jclouds.atmosonline.saas.AtmosStorageClient;
import org.jclouds.atmosonline.saas.blobstore.functions.BlobToObject;
import org.jclouds.atmosonline.saas.domain.AtmosStorageError;
import org.jclouds.atmosonline.saas.filters.SignRequest;
import org.jclouds.atmosonline.saas.xml.ErrorHandler;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.encryption.EncryptionService;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.util.Utils;

import com.google.common.base.Supplier;

/**
 * Encryption, Hashing, and IO Utilities needed to sign and verify Atmos Storage requests and
 * responses.
 * 
 * @author Adrian Cole
 */
public class AtmosStorageUtils {

   @Inject
   SignRequest signer;

   @Inject
   ParseSax.Factory factory;

   @Inject
   Provider<ErrorHandler> errorHandlerProvider;

   public AtmosStorageError parseAtmosStorageErrorFromContent(HttpCommand command,
            HttpResponse response, InputStream content) throws HttpException {
      AtmosStorageError error = (AtmosStorageError) factory.create(errorHandlerProvider.get())
               .parse(content);
      if (error.getCode() == 1032) {
         error.setStringSigned(signer.createStringToSign(command.getRequest()));
      }
      return error;

   }

   public static String putBlob(final AtmosStorageClient sync, EncryptionService encryptionService,
            BlobToObject blob2Object, String container, Blob blob) {
      final String path = container + "/" + blob.getMetadata().getName();
      deleteAndEnsureGone(sync, path);
      if (blob.getMetadata().getContentMD5() != null)
         blob.getMetadata().getUserMetadata().put("content-md5",
                  encryptionService.toHexString(blob.getMetadata().getContentMD5()));
      sync.createFile(container, blob2Object.apply(blob));
      return path;
   }

   public static void deleteAndEnsureGone(final AtmosStorageClient sync, final String path) {
      try {
         if (!Utils.enventuallyTrue(new Supplier<Boolean>() {
            public Boolean get() {
               sync.deletePath(path);
               return !sync.pathExists(path);
            }
         }, 3000)) {
            throw new IllegalStateException(path + " still exists after deleting!");
         }
      } catch (InterruptedException e) {
         new IllegalStateException(path + " interrupted during deletion!", e);
      }
   }

   public AtmosStorageError parseAtmosStorageErrorFromContent(HttpCommand command,
            HttpResponse response, String content) throws HttpException {
      return parseAtmosStorageErrorFromContent(command, response, new ByteArrayInputStream(content
               .getBytes()));
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