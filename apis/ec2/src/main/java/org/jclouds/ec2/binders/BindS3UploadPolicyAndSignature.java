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
package org.jclouds.ec2.binders;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.io.BaseEncoding.base64;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.aws.filters.FormSigner;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class BindS3UploadPolicyAndSignature implements Binder {
   private final FormSigner signer;

   @Inject
   BindS3UploadPolicyAndSignature(FormSigner signer) {
      this.signer = signer;
   }

   @SuppressWarnings("unchecked")
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      String encodedJson = base64().encode(checkNotNull(input, "json").toString().getBytes(UTF_8));
      Builder<String, String> builder = ImmutableMultimap.builder();
      builder.put("Storage.S3.UploadPolicy", encodedJson);
      String signature = signer.sign(encodedJson);
      builder.put("Storage.S3.UploadPolicySignature", signature);
      return (R) request.toBuilder().replaceFormParams(builder.build()).build();
   }

}
