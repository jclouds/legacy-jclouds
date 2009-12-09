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

import java.net.URI;

import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.provider.URLFileNameParser;
import org.apache.commons.vfs.provider.UriParser;
import org.apache.commons.vfs.provider.VfsComponentContext;
import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpUtils;

/**
 * @author Adrian Cole
 */
public class BlobStoreFileNameParser extends URLFileNameParser {
   public static final BlobStoreFileNameParser INSTANCE = new BlobStoreFileNameParser();

   public static BlobStoreFileNameParser getInstance() {
      return INSTANCE;
   }

   public BlobStoreFileNameParser() {
      super(443);
   }

   public FileName parseUri(final VfsComponentContext context, FileName base, String filename)
            throws FileSystemException {

      // if there are unencoded characters in the password, things break.
      URI uri = HttpUtils.createUri(filename);

      filename = uri.toASCIIString();

      Credentials creds = Credentials.parse(uri);

      StringBuffer name = new StringBuffer();

      // Extract the scheme and authority parts
      Authority auth = extractToPath(filename, name);

      // Decode and adjust separators
      UriParser.canonicalizePath(name, 0, name.length(), this);
      UriParser.fixSeparators(name);

      // Extract the container
      String container = UriParser.extractFirstElement(name);
      if (container == null || container.length() == 0) {
         throw new FileSystemException("vfs.provider.blobstore/missing-container-name.error",
                  filename);
      }

      // Normalise the path. Do this after extracting the container name
      FileType fileType = UriParser.normalisePath(name);
      String path = name.toString();

      return new BlobStoreFileName(auth.getHostName(), creds.account, creds.key, path, fileType,
               container);
   }

}
