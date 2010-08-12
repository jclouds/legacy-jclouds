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

package org.jclouds.aws.ec2.binders;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.http.HttpUtils.addFormParamTo;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.aws.filters.FormSigner;
import org.jclouds.crypto.Crypto;
import org.jclouds.crypto.CryptoStreams;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;

import com.google.common.base.Charsets;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class BindS3UploadPolicyAndSignature implements Binder {
   private final FormSigner signer;

   @Inject
   BindS3UploadPolicyAndSignature(FormSigner signer, Crypto crypto) {
      this.signer = signer;
   }

   public void bindToRequest(HttpRequest request, Object input) {
      String encodedJson = CryptoStreams.base64(checkNotNull(input, "json").toString().getBytes(Charsets.UTF_8));
      addFormParamTo(request, "Storage.S3.UploadPolicy", encodedJson);
      String signature = signer.sign(encodedJson);
      addFormParamTo(request, "Storage.S3.UploadPolicySignature", signature);
   }

}