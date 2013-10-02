/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.azureblob.domain.internal;

import org.jclouds.azureblob.domain.BlobBlockProperties;

import com.google.common.base.Objects;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Representation of the blocks which compose a Blob
 */
public class BlobBlockPropertiesImpl implements BlobBlockProperties {
   private final String blockName;
   private final long contentLength;
   private final boolean committed;

   public BlobBlockPropertiesImpl(String blockName, long contentLength, boolean committed) {
      this.blockName = checkNotNull(blockName);
      this.contentLength = contentLength;
      this.committed = committed;
   }

   @Override
   public String getBlockName() {
      return blockName;
   }

   @Override
   public boolean isCommitted() {
      return committed;
   }

   @Override
   public long getContentLength() {
      return contentLength;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      BlobBlockPropertiesImpl that = (BlobBlockPropertiesImpl) o;
      return Objects.equal(blockName, that.blockName)
            && Objects.equal(committed, that.committed)
            && Objects.equal(contentLength, that.contentLength);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(blockName, contentLength, committed);
   }
}
