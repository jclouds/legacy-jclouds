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
package org.jclouds.atmos.blobstore.functions;

import javax.inject.Singleton;

import org.jclouds.atmos.domain.MutableContentMetadata;
import org.jclouds.atmos.domain.internal.DelegatingMutableContentMetadata;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.internal.MutableBlobMetadataImpl;
import org.jclouds.http.HttpUtils;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class BlobToContentMetadata implements Function<BlobMetadata, MutableContentMetadata> {
   public MutableContentMetadata apply(BlobMetadata base) {
      MutableBlobMetadataImpl to = new MutableBlobMetadataImpl();
      HttpUtils.copy(base.getContentMetadata(), to.getContentMetadata());
      return new DelegatingMutableContentMetadata(base.getUri(), base.getName(), base.getUri() != null ? base.getUri()
               .getPath() : null, to.getContentMetadata());
   }

}
