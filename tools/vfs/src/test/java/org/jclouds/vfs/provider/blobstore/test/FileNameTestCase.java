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
package org.jclouds.vfs.provider.blobstore.test;

import org.apache.commons.AbstractVfsTestCase;
import org.apache.commons.vfs.FileSystemException;
import org.jclouds.vfs.provider.blobstore.BlobStoreFileName;
import org.jclouds.vfs.provider.blobstore.BlobStoreFileNameParser;

/**
 * @author Adrian Cole
 */
public class FileNameTestCase extends AbstractVfsTestCase {
   /**
    * Tests parsing a URI into its parts.
    */
   public void testParseUri() throws Exception {
      // Simple name
      BlobStoreFileName name = (BlobStoreFileName) BlobStoreFileNameParser.getInstance().parseUri(
               null, null, "blobstore://service/container/file");
      assertEquals("blobstore", name.getScheme());
      assertNull(name.getUserName());
      assertNull(name.getPassword());
      assertEquals("service", name.getHostName());
      assertEquals(443, name.getPort());
      assertEquals(name.getDefaultPort(), name.getPort());
      assertEquals("container", name.getContainer());
      assertEquals("/file", name.getPath());
      assertEquals("blobstore://service/container/", name.getRootURI());
      assertEquals("blobstore://service/container/file", name.getURI());

      // Name with no path
      name = (BlobStoreFileName) BlobStoreFileNameParser.getInstance().parseUri(null, null,
               "blobstore://service/container");
      assertEquals("blobstore", name.getScheme());
      assertNull(name.getUserName());
      assertNull(name.getPassword());
      assertEquals("service", name.getHostName());
      assertEquals(443, name.getPort());
      assertEquals("container", name.getContainer());
      assertEquals("/", name.getPath());
      assertEquals("blobstore://service/container/", name.getRootURI());
      assertEquals("blobstore://service/container/", name.getURI());

      // Name with username
      name = (BlobStoreFileName) BlobStoreFileNameParser.getInstance().parseUri(null, null,
               "blobstore://user@service/container/file");
      assertEquals("blobstore", name.getScheme());
      assertEquals("user", name.getUserName());
      assertNull(name.getPassword());
      assertEquals("service", name.getHostName());
      assertEquals(443, name.getPort());
      assertEquals("container", name.getContainer());
      assertEquals("/file", name.getPath());
      assertEquals("blobstore://user@service/container/", name.getRootURI());
      assertEquals("blobstore://user@service/container/file", name.getURI());

      // Name with extension
      name = (BlobStoreFileName) BlobStoreFileNameParser.getInstance().parseUri(null, null,
               "blobstore://user@service/container/file.txt");
      assertEquals("blobstore", name.getScheme());
      assertEquals("user", name.getUserName());
      assertNull(name.getPassword());
      assertEquals("service", name.getHostName());
      assertEquals(443, name.getPort());
      assertEquals("container", name.getContainer());
      assertEquals("/file.txt", name.getPath());
      assertEquals("file.txt", name.getBaseName());
      assertEquals("txt", name.getExtension());
      assertEquals("blobstore://user@service/container/", name.getRootURI());
      assertEquals("blobstore://user@service/container/file.txt", name.getURI());

      // Name look likes extension, but isnt
      name = (BlobStoreFileName) BlobStoreFileNameParser.getInstance().parseUri(null, null,
               "blobstore://user@service/container/.bashrc");
      assertEquals("blobstore", name.getScheme());
      assertEquals("user", name.getUserName());
      assertNull(name.getPassword());
      assertEquals("service", name.getHostName());
      assertEquals(443, name.getPort());
      assertEquals("container", name.getContainer());
      assertEquals("/.bashrc", name.getPath());
      assertEquals(".bashrc", name.getBaseName());
      assertEquals("", name.getExtension());
      assertEquals("blobstore://user@service/container/", name.getRootURI());
      assertEquals("blobstore://user@service/container/.bashrc", name.getURI());
   }

   /**
    * Tests error handling in URI parser.
    */
   public void disabledTestBadlyFormedUri() throws Exception {
      // Does not start with blobstore://
      testBadlyFormedUri("blobstore:", "vfs.provider/missing-double-slashes.error");
      testBadlyFormedUri("blobstore:/", "vfs.provider/missing-double-slashes.error");
      testBadlyFormedUri("blobstore:a", "vfs.provider/missing-double-slashes.error");

      // Missing service
      testBadlyFormedUri("blobstore://", "vfs.provider/missing-hostname.error");
      testBadlyFormedUri("blobstore://:21/container", "vfs.provider/missing-hostname.error");
      testBadlyFormedUri("blobstore:///container", "vfs.provider/missing-hostname.error");

      // Empty port
      testBadlyFormedUri("blobstore://host:", "vfs.provider/missing-port.error");
      testBadlyFormedUri("blobstore://host:/container", "vfs.provider/missing-port.error");
      testBadlyFormedUri("blobstore://host:port/container/file", "vfs.provider/missing-port.error");

      // Missing absolute path
      testBadlyFormedUri("blobstore://host:90a", "vfs.provider/missing-hostname-path-sep.error");
      testBadlyFormedUri("blobstore://host?a", "vfs.provider/missing-hostname-path-sep.error");

      // Missing container name
      testBadlyFormedUri("blobstore://host", "vfs.provider.blobstore/missing-container-name.error");
      testBadlyFormedUri("blobstore://host/", "vfs.provider.blobstore/missing-container-name.error");
      testBadlyFormedUri("blobstore://host:9090/",
               "vfs.provider.blobstore/missing-container-name.error");
   }

   /**
    * Tests that parsing a URI fails with the expected error.
    */
   private void testBadlyFormedUri(final String uri, final String errorMsg) {
      try {
         BlobStoreFileNameParser.getInstance().parseUri(null, null, uri);
         fail();
      } catch (final FileSystemException e) {
         assertSameMessage(errorMsg, uri, e);
      }
   }
}
