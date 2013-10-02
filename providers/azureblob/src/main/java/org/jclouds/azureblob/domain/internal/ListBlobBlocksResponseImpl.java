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
import org.jclouds.azureblob.domain.ListBlobBlocksResponse;

import static com.google.common.base.Preconditions.checkNotNull;


import java.util.List;
import com.google.common.base.Objects;

/**
 * Represents the list of blocks which compose a blob
 */
public class ListBlobBlocksResponseImpl implements ListBlobBlocksResponse {
   private final List<BlobBlockProperties> blocks;

   public ListBlobBlocksResponseImpl(List<BlobBlockProperties> blocks) {
      this.blocks = checkNotNull(blocks, "block list must not be null");
   }

   @Override
   public List<BlobBlockProperties> getBlocks() {
      return blocks;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      ListBlobBlocksResponseImpl that = (ListBlobBlocksResponseImpl) o;
      return Objects.equal(blocks, that.blocks);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(blocks);
   }
}
