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
package org.jclouds.s3.blobstore.functions;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Map.Entry;

import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpUtils;
import org.jclouds.rest.InvocationContext;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.s3.domain.MutableObjectMetadata;
import org.jclouds.s3.domain.internal.MutableObjectMetadataImpl;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
public class BlobToObjectMetadata implements Function<BlobMetadata, MutableObjectMetadata>,
         InvocationContext<BlobToObjectMetadata> {
   private String bucket;

   public MutableObjectMetadata apply(BlobMetadata from) {
      if (from == null)
         return null;
      MutableObjectMetadata to = new MutableObjectMetadataImpl();
      HttpUtils.copy(from.getContentMetadata(), to.getContentMetadata());
      to.setUri(from.getUri());
      to.setETag(from.getETag());
      to.setKey(from.getName());
      to.setBucket(bucket);
      to.setLastModified(from.getLastModified());
      if (from.getUserMetadata() != null) {
         for (Entry<String, String> entry : from.getUserMetadata().entrySet())
            to.getUserMetadata().put(entry.getKey().toLowerCase(), entry.getValue());
      }
      return to;
   }

   @Override
   public BlobToObjectMetadata setContext(HttpRequest request) {
      checkArgument(request instanceof GeneratedHttpRequest, "note this handler requires a GeneratedHttpRequest");
      return setBucket(GeneratedHttpRequest.class.cast(request).getInvocation().getArgs().get(0).toString());
   }

   private BlobToObjectMetadata setBucket(String bucket) {
      this.bucket = bucket;
      return this;
   }

}
