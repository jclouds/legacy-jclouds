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
package org.jclouds.azureblob.blobstore.strategy;

import com.google.inject.ImplementedBy;
import org.jclouds.blobstore.domain.Blob;

/**
 * @see <a href="http://msdn.microsoft.com/en-us/library/windowsazure/dd135726.aspx">Azure Put Block Documentation</a>
 *
 * @author John Victor Kew
 */
@ImplementedBy(AzureBlobBlockUploadStrategy.class)
public interface MultipartUploadStrategy {
   /* Maximum number of blocks per upload */
   public static final int MAX_NUMBER_OF_BLOCKS = 50000;

   /* Maximum block size */
   public static final long MAX_BLOCK_SIZE = 4L * 1024 * 1024;

   String execute(String container, Blob blob);
}
