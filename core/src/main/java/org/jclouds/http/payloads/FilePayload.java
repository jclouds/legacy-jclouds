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
package org.jclouds.http.payloads;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.jclouds.http.Payload;

import com.google.common.io.Closeables;
import com.google.common.io.Files;

/**
 * @author Adrian Cole
 */
public class FilePayload implements Payload {
   private final File content;

   public FilePayload(File content) {
      checkArgument(checkNotNull(content, "content").exists(), "file must exist: " + content);
      this.content = content;
   }

   public File getRawContent() {
      return content;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public InputStream getInput() {
      try {
         return new FileInputStream(content);
      } catch (FileNotFoundException e) {
         throw new IllegalStateException("file " + content + " does not exist", e);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean isRepeatable() {
      return true;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void writeTo(OutputStream outstream) throws IOException {
      InputStream in = getInput();
      try {
         Files.copy(content, outstream);
      } finally {
         Closeables.closeQuietly(in);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Long calculateSize() {
      return content.length();
   }
}