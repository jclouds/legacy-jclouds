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

import java.io.IOException;

import junit.framework.Test;

import org.apache.commons.vfs.AllFileSelector;
import org.apache.commons.vfs.FileContent;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.impl.DefaultFileSystemManager;
import org.apache.commons.vfs.test.AbstractProviderTestConfig;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.util.Utils;
import org.jclouds.vfs.provider.blobstore.BlobStoreFileObject;
import org.jclouds.vfs.provider.blobstore.BlobStoreFileProvider;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.io.CharStreams;
import com.google.inject.Module;

/**
 * @author Adrian Cole
 */
public class BlobStoreProviderTestCase extends AbstractProviderTestConfig {
   private static final String TEST_URI = "test.blobstore.uri";

   public static Test suite() throws Exception {
      return new BlobStoreProviderTestSuite(new BlobStoreProviderTestCase());
   }

   /**
    * Prepares the file system manager.
    */
   public void prepare(final DefaultFileSystemManager manager) throws Exception {
      manager.addProvider("blobstore", new BlobStoreFileProvider(ImmutableList
               .<Module> of(new Log4JLoggingModule())));
   }

   /**
    * Returns the base folder for tests.
    */
   public FileObject getBaseTestFolder(final FileSystemManager manager) throws Exception {
      String uri = System.getProperty(TEST_URI, "blobstore://account:key@stub/stub");
      return setUpTests(manager, uri);
   }

   private FileObject setUpTests(final FileSystemManager manager, String uri)
            throws FileSystemException, IOException {
      FileObject base = manager.resolveFile(uri);
      FileObject writeTests = base.resolveFile("write-tests");
      FileObject readTests = base.resolveFile("read-tests");

      if (base instanceof BlobStoreFileObject) {
         BlobStore blobStore = ((BlobStoreFileObject) base).getBlobStore();
         String container = ((BlobStoreFileObject) base).getContainer();
         blobStore.clearContainer(container);
      } else {
         writeTests.delete(new AllFileSelector());
         readTests.delete(new AllFileSelector());
      }

      writeTests.createFolder();
      readTests.createFolder();
      for (String name : new String[] { "file1.txt", "file%25.txt", "file space.txt" }) {
         writeFile(readTests, name, FILE1_CONTENT);
      }
      writeFile(readTests, "empty.txt", "");
      FileObject dir = readTests.resolveFile("dir1");
      dir.createFolder();
      write3Files(dir);
      for (String subdirName : new String[] { "subdir1", "subdir2", "subdir3" }) {
         FileObject subdir = dir.resolveFile(subdirName);
         subdir.createFolder();
         write3Files(subdir);
      }
      return base;
   }

   private void write3Files(FileObject base) throws IOException {
      for (String name : new String[] { "file1.txt", "file2.txt", "file3.txt" }) {
         writeFile(base, name, TEST_FILE_CONTENT);
      }
   }

   private void writeFile(FileObject base, String name, String value) throws FileSystemException,
            IOException {
      FileObject file = base.resolveFile(name);
      FileContent content = file.getContent();
      CharStreams.write(value, CharStreams.newWriterSupplier(Utils.newOutputStreamSupplier(content
               .getOutputStream()), Charsets.UTF_8));
      content.close();
   }

}
