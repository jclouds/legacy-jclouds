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

package org.jclouds.blobstore.domain.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.io.Payloads.newPayload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobBuilder;
import org.jclouds.blobstore.domain.StorageType;
import org.jclouds.crypto.Crypto;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;

import com.google.common.base.Throwables;
import com.google.common.collect.Maps;

/**
 * @author Adrian Cole
 */
public class BlobBuilderImpl implements BlobBuilder {

   private Payload payload;
   private String name;
   private Map<String, String> userMetadata = Maps.newLinkedHashMap();
   private StorageType type = StorageType.BLOB;
   @Inject
   private Crypto crypto;

   @Override
   public BlobBuilder name(String name) {
      this.name = name;
      return this;
   }

   @Override
   public BlobBuilder type(StorageType type) {
      this.type = type;
      return this;
   }

   @Override
   public BlobBuilder userMetadata(Map<String, String> userMetadata) {
      this.userMetadata = Maps.newLinkedHashMap(checkNotNull(userMetadata, "userMetadata"));
      return this;
   }

   @Override
   public PayloadBlobBuilder payload(Payload payload) {
      this.payload = payload;
      return new PayloadBlobBuilderImpl(this, payload, crypto);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public PayloadBlobBuilder payload(InputStream data) {
      return payload(newPayload(checkNotNull(data, "data")));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public PayloadBlobBuilder payload(byte[] data) {
      return payload(newPayload(checkNotNull(data, "data")));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public PayloadBlobBuilder payload(String data) {
      return payload(newPayload(checkNotNull(data, "data")));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public PayloadBlobBuilder payload(File data) {
      return payload(newPayload(checkNotNull(data, "data")));
   }

   @Override
   public Blob build() {
      Blob blob = new BlobImpl(new MutableBlobMetadataImpl());
      if (name != null)
         blob.getMetadata().setName(name);
      if (payload != null)
         blob.setPayload(payload);
      blob.getMetadata().setUserMetadata(userMetadata);
      blob.getMetadata().setType(type);
      return blob;
   }

   public class PayloadBlobBuilderImpl implements PayloadBlobBuilder {
      private final BlobBuilder builder;
      private final Payload payload;
      private MessageDigest digest;

      public PayloadBlobBuilderImpl(BlobBuilder builder, Payload payload, @Nullable Crypto crypto) {
         this.builder = checkNotNull(builder, "builder");
         this.payload = checkNotNull(payload, "payload");
         try {
            this.digest = crypto != null ? crypto.md5() : MessageDigest.getInstance("MD5");
         } catch (NoSuchAlgorithmException e) {
            Throwables.propagate(e);
            this.digest = null;
         }
      }

      @Override
      public BlobBuilder name(String name) {
         return builder.name(name);
      }

      @Override
      public BlobBuilder type(StorageType type) {
         return builder.type(type);
      }

      @Override
      public BlobBuilder userMetadata(Map<String, String> userMetadata) {
         return builder.userMetadata(userMetadata);
      }

      @Override
      public PayloadBlobBuilder payload(Payload payload) {
         return builder.payload(payload);
      }

      @Override
      public PayloadBlobBuilder calculateMD5() throws IOException {
         return builder.payload(Payloads.calculateMD5(payload, digest));
      }

      @Override
      public PayloadBlobBuilder payload(InputStream payload) {
         return builder.payload(payload);
      }

      @Override
      public PayloadBlobBuilder payload(byte[] payload) {
         return builder.payload(payload);
      }

      @Override
      public PayloadBlobBuilder payload(String payload) {
         return builder.payload(payload);
      }

      @Override
      public PayloadBlobBuilder payload(File payload) {
         return builder.payload(payload);
      }

      @Override
      public Blob build() {
         return builder.build();
      }

      @Override
      public PayloadBlobBuilder contentLength(long contentLength) {
         payload.getContentMetadata().setContentLength(contentLength);
         return this;
      }

      @Override
      public PayloadBlobBuilder contentMD5(byte[] md5) {
         payload.getContentMetadata().setContentMD5(md5);
         return this;
      }

      @Override
      public PayloadBlobBuilder contentType(String contentType) {
         payload.getContentMetadata().setContentType(contentType);
         return this;
      }

      @Override
      public PayloadBlobBuilder contentDisposition(String contentDisposition) {
         payload.getContentMetadata().setContentDisposition(contentDisposition);
         return this;
      }

      @Override
      public PayloadBlobBuilder contentLanguage(String contentLanguage) {
         payload.getContentMetadata().setContentLanguage(contentLanguage);
         return this;

      }

      @Override
      public PayloadBlobBuilder contentEncoding(String contentEncoding) {
         payload.getContentMetadata().setContentEncoding(contentEncoding);
         return this;
      }

   }
}
