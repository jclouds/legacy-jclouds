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
package org.jclouds.blobstore;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.io.BaseEncoding.base16;
import static org.jclouds.http.Uris.uriBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.inject.Inject;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.Blob.Factory;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.util.BlobStoreUtils;
import org.jclouds.date.DateService;
import org.jclouds.domain.Location;
import org.jclouds.http.HttpUtils;
import org.jclouds.io.ContentMetadataCodec;
import org.jclouds.io.MutableContentMetadata;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.io.payloads.ByteArrayPayload;
import org.jclouds.io.payloads.DelegatingPayload;

import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimaps;

public class TransientStorageStrategy implements LocalStorageStrategy {
   private final ConcurrentMap<String, ConcurrentMap<String, Blob>> containerToBlobs = new ConcurrentHashMap<String, ConcurrentMap<String, Blob>>();
   private final ConcurrentMap<String, Location> containerToLocation = new ConcurrentHashMap<String, Location>();
   private final Supplier<Location> defaultLocation;
   private final DateService dateService;
   private final Factory blobFactory;
   private final ContentMetadataCodec contentMetadataCodec;

   @Inject
   TransientStorageStrategy(Supplier<Location> defaultLocation, DateService dateService, Factory blobFactory,
         ContentMetadataCodec contentMetadataCodec) {
      this.defaultLocation = defaultLocation;
      this.dateService = dateService;
      this.blobFactory = blobFactory;
      this.contentMetadataCodec = contentMetadataCodec;
   }

   @Override
   public boolean containerExists(final String containerName) {
      return containerToBlobs.containsKey(containerName);
   }

   @Override
   public Iterable<String> getAllContainerNames() {
      return containerToBlobs.keySet();
   }

   @Override
   public boolean createContainerInLocation(final String containerName, final Location location) {
      ConcurrentMap<String, Blob> origValue = containerToBlobs.putIfAbsent(
            containerName, new ConcurrentHashMap<String, Blob>());
      if (origValue != null) {
         return false;
      }
      containerToLocation.put(containerName, location != null ? location : defaultLocation.get());
      return true;
   }

   @Override
   public void deleteContainer(final String containerName) {
      containerToBlobs.remove(containerName);
   }

   @Override
   public void clearContainer(final String containerName) {
      clearContainer(containerName, ListContainerOptions.Builder.recursive());
   }

   @Override
   public void clearContainer(String containerName, ListContainerOptions options) {
      // TODO implement options
      containerToBlobs.get(containerName).clear();
   }

   @Override
   public boolean blobExists(final String containerName, final String blobName) {
      Map<String, Blob> map = containerToBlobs.get(containerName);
      return map != null && map.containsKey(blobName);
   }

   @Override
   public Iterable<String> getBlobKeysInsideContainer(final String containerName) {
      return containerToBlobs.get(containerName).keySet();
   }

   @Override
   public Blob getBlob(final String containerName, final String blobName) {
      Map<String, Blob> map = containerToBlobs.get(containerName);
      return map == null ? null : map.get(blobName);
   }

   @Override
   public String putBlob(final String containerName, final Blob blob) throws IOException {
      Blob newBlob = createUpdatedCopyOfBlobInContainer(containerName, blob);
      Map<String, Blob> map = containerToBlobs.get(containerName);
      map.put(newBlob.getMetadata().getName(), newBlob);
      Payloads.calculateMD5(newBlob);
      return base16().lowerCase().encode(newBlob.getPayload().getContentMetadata().getContentMD5());
   }

   @Override
   public void removeBlob(final String containerName, final String blobName) {
      Map<String, Blob> map = containerToBlobs.get(containerName);
      if (map != null)
         map.remove(blobName);
   }

   @Override
   public Location getLocation(final String containerName) {
      return containerToLocation.get(containerName);
   }

   @Override
   public String getSeparator() {
      return "/";
   }

   private Blob createUpdatedCopyOfBlobInContainer(String containerName, Blob in) {
      checkNotNull(in, "blob");
      checkNotNull(in.getPayload(), "blob.payload");
      ByteArrayPayload payload = (in.getPayload() instanceof ByteArrayPayload) ? ByteArrayPayload.class.cast(in
               .getPayload()) : null;
      if (payload == null)
         payload = (in.getPayload() instanceof DelegatingPayload) ? (DelegatingPayload.class.cast(in.getPayload())
                  .getDelegate() instanceof ByteArrayPayload) ? ByteArrayPayload.class.cast(DelegatingPayload.class
                  .cast(in.getPayload()).getDelegate()) : null : null;
      try {
         if (payload == null || !(payload instanceof ByteArrayPayload)) {
            MutableContentMetadata oldMd = in.getPayload().getContentMetadata();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            in.getPayload().writeTo(out);
            payload = (ByteArrayPayload) Payloads.calculateMD5(Payloads.newPayload(out.toByteArray()));
            HttpUtils.copy(oldMd, payload.getContentMetadata());
         } else {
            if (payload.getContentMetadata().getContentMD5() == null)
               Payloads.calculateMD5(in);
         }
      } catch (IOException e) {
         Throwables.propagate(e);
      }
      Blob blob = blobFactory.create(BlobStoreUtils.copy(in.getMetadata()));
      blob.setPayload(payload);
      blob.getMetadata().setContainer(containerName);
      blob.getMetadata().setUri(
            uriBuilder(new StringBuilder("mem://").append(containerName)).path(in.getMetadata().getName()).build());
      blob.getMetadata().setLastModified(new Date());
      String eTag = base16().lowerCase().encode(payload.getContentMetadata().getContentMD5());
      blob.getMetadata().setETag(eTag);
      // Set HTTP headers to match metadata
      blob.getAllHeaders().replaceValues(HttpHeaders.LAST_MODIFIED,
               ImmutableList.of(dateService.rfc822DateFormat(blob.getMetadata().getLastModified())));
      blob.getAllHeaders().replaceValues(HttpHeaders.ETAG, ImmutableList.of(eTag));
      copyPayloadHeadersToBlob(payload, blob);
      blob.getAllHeaders().putAll(Multimaps.forMap(blob.getMetadata().getUserMetadata()));
      return blob;
   }

   private void copyPayloadHeadersToBlob(Payload payload, Blob blob) {
      blob.getAllHeaders().putAll(contentMetadataCodec.toHeaders(payload.getContentMetadata()));
   }
}
