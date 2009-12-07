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
package org.jclouds.vfs.provider.blobstore;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;

import org.apache.commons.vfs.Capability;
import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileSystem;
import org.apache.commons.vfs.FileSystemConfigBuilder;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemOptions;
import org.apache.commons.vfs.UserAuthenticationData;
import org.apache.commons.vfs.provider.AbstractOriginatingFileProvider;
import org.apache.commons.vfs.provider.http.HttpFileSystemConfigBuilder;
import org.apache.commons.vfs.util.UserAuthenticatorUtils;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContextFactory;
import org.jclouds.blobstore.internal.LocationAndCredentials;

import com.google.common.io.Resources;
import com.google.inject.Module;

/**
 * @author Adrian Cole
 */
public class BlobStoreFileProvider extends AbstractOriginatingFileProvider {

   public final static Collection<Capability> capabilities = Collections
            .unmodifiableCollection(Arrays.asList(Capability.CREATE, Capability.DELETE,
                     Capability.GET_TYPE, Capability.GET_LAST_MODIFIED, Capability.LIST_CHILDREN,
                     Capability.READ_CONTENT, Capability.URI, Capability.WRITE_CONTENT,
                     Capability.RANDOM_ACCESS_READ, Capability.ATTRIBUTES));

   public final static UserAuthenticationData.Type[] AUTHENTICATOR_TYPES = new UserAuthenticationData.Type[] {
            UserAuthenticationData.USERNAME, UserAuthenticationData.PASSWORD };

   private final Module[] modules;

   public BlobStoreFileProvider(Module... modules) {
      this.modules = modules;
      setFileNameParser(new BlobStoreFileNameParser());
   }

   protected FileSystem doCreateFileSystem(final FileName name,
            final FileSystemOptions fileSystemOptions) throws FileSystemException {
      BlobStoreFileName rootName = (BlobStoreFileName) name;

      UserAuthenticationData authData = null;
      BlobStore blobStore;
      try {
         String uriToParse = rootName.getFriendlyURI();
         authData = UserAuthenticatorUtils.authenticate(fileSystemOptions, AUTHENTICATOR_TYPES);
         Properties properties = new Properties();
         properties.load(Resources.newInputStreamSupplier(
                  Resources.getResource("jclouds.properties")).getInput());
         LocationAndCredentials locationAndCredentials = LocationAndCredentials.parse(uriToParse);

         blobStore = new BlobStoreContextFactory(properties).createContext(
                  locationAndCredentials.uri,
                  UserAuthenticatorUtils.toString(UserAuthenticatorUtils.getData(authData,
                           UserAuthenticationData.USERNAME, UserAuthenticatorUtils.toChar(rootName
                                    .getUserName()))),
                  UserAuthenticatorUtils.toString(UserAuthenticatorUtils.getData(authData,
                           UserAuthenticationData.PASSWORD, UserAuthenticatorUtils.toChar(rootName
                                    .getPassword()))),modules).getBlobStore();
      } catch (IOException e) {
         throw new FileSystemException("vfs.provider.blobstore/properties.error", name, e);
      } finally {
         UserAuthenticatorUtils.cleanup(authData);
      }

      return new BlobStoreFileSystem(rootName, blobStore, fileSystemOptions);
   }

   public FileSystemConfigBuilder getConfigBuilder() {
      return HttpFileSystemConfigBuilder.getInstance();
   }

   public Collection<?> getCapabilities() {
      return capabilities;
   }
}
