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

package org.jclouds.io.payloads;

import java.io.File;
import java.io.InputStream;

public class ChunkedFilePayload extends FilePayload {

   private int part;
   private long chunkOffset;
   private long chunkSize;
   
   public ChunkedFilePayload(File content) {
      this(content, 1, 0, content.length());
   }
   
   public ChunkedFilePayload(File content, int part, long chunkOffset, long chunkSize) {
      super(content);
      this.part = part;
      this.chunkOffset = chunkOffset;
      this.chunkSize = chunkSize;
   }
   
   public int getPart() {
      return part;
   }

   @Override
   public InputStream getInput() {
      return new ChunkedFileInputStream(getRawContent(), chunkOffset, chunkSize);
   }
}
