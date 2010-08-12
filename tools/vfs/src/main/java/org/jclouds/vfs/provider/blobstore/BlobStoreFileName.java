/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.vfs.provider.blobstore;

import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.provider.GenericFileName;

/**
 * 
 * @author Adrian Cole
 */
public class BlobStoreFileName extends GenericFileName {

   private static final int DEFAULT_PORT = 443;

   private final String container;
   private String uriWithoutAuth;

   protected BlobStoreFileName(final String service, final String identity, final String key,
            final String path, final FileType type, final String container) {
      super("blobstore", service, DEFAULT_PORT, DEFAULT_PORT, identity, key, path, type);
      this.container = container;
   }

   /**
    * Returns the container name.
    */
   public String getContainer() {
      return container;
   }

   @Override
   protected void appendRootUri(final StringBuffer buffer, boolean addPassword) {
      super.appendRootUri(buffer, addPassword);
      buffer.append('/');
      buffer.append(getContainer());
   }

   @Override
   public FileName createName(final String path, FileType type) {
      return new BlobStoreFileName(getService(), getUserName(), getPassword(), path, type,
               getContainer());
   }

   @Override
   public String getFriendlyURI() {
      if (uriWithoutAuth != null) {
         return uriWithoutAuth;
      }

      StringBuffer sb = new StringBuffer(120);
      sb.append(getScheme());
      sb.append("://");
      sb.append(getService());
      sb.append("/");
      sb.append(getContainer());
      sb.append(getPath());
      uriWithoutAuth = sb.toString();
      return uriWithoutAuth;
   }

   public String getService() {
      return getHostName();
   }
}
