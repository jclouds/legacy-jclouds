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
package org.jclouds.blobstore.internal;

import static org.jclouds.blobstore.options.GetOptions.Builder.range;
import static org.testng.Assert.assertEquals;

import java.util.Date;
import java.util.Map;
import java.util.Properties;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.options.GetOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.internal.BaseRestClientExpectTest;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Module;

/**
 * @author Adrian Cole
 */
public abstract class BaseBlobSignerExpectTest extends BaseRestClientExpectTest<BlobStore> {

   /**
    * define the requests and responses needed to initialize the blobstore. For
    * example, you may need to add in discovery requests needed (ex. KeyStone in
    */
   protected Map<HttpRequest, HttpResponse> init() {
      return ImmutableMap.<HttpRequest, HttpResponse> of();
   }

   protected String container = "container";
   protected String name = "name";
   protected String text = "fooooooooooooooooooooooo";
   protected GetOptions options = range(0, 1);

   @Test
   public void testSignGetBlob() {
      BlobStore getBlob = requestsSendResponses(init());
      assertEquals(getBlob.getContext().getSigner().signGetBlob(container, name), getBlob());
   }

   protected abstract HttpRequest getBlob();

   @Test
   public void testSignGetBlobWithTime() {
      BlobStore getBlobWithTime = requestsSendResponses(init());
      HttpRequest compare = getBlobWithTime();
      assertEquals(getBlobWithTime.getContext().getSigner().signGetBlob(container, name, 3l /* seconds */),
            compare);
   }

   protected abstract HttpRequest getBlobWithTime();

   @Test
   public void testSignGetBlobWithOptions() {
      BlobStore getBlobWithOptions = requestsSendResponses(init());
      assertEquals(getBlobWithOptions.getContext().getSigner().signGetBlob(container, name, options),
            getBlobWithOptions());
   }

   protected abstract HttpRequest getBlobWithOptions();

   @Test
   public void testSignRemoveBlob() {
      BlobStore removeBlob = requestsSendResponses(init());
      assertEquals(removeBlob.getContext().getSigner().signRemoveBlob(container, name), removeBlob());
   }

   @Test
   public void testSignPutBlob() throws Exception {
      BlobStore signPutBlob = requestsSendResponses(init());
      Blob blob = signPutBlob.blobBuilder("name").forSigning().contentLength(2l).contentMD5(new byte[] { 0, 2, 4, 8 })
            .contentType("text/plain").expires(new Date(1000)).build();
      HttpRequest compare = putBlob();
      compare.setPayload(blob.getPayload());
      assertEquals(signPutBlob.getContext().getSigner().signPutBlob(container, blob), compare);
   }

   protected abstract HttpRequest putBlob();

   @Test
   public void testSignPutBlobWithTime() throws Exception {
      BlobStore signPutBloblWithTime = requestsSendResponses(init());
      Blob blob = signPutBloblWithTime.blobBuilder(name).payload(text).contentType("text/plain").build();
      HttpRequest compare = putBlobWithTime();
      compare.setPayload(blob.getPayload());
      assertEquals(signPutBloblWithTime.getContext().getSigner().signPutBlob(container, blob, 3l /* seconds */),
            compare);
   }

   protected abstract HttpRequest putBlobWithTime();

   protected abstract HttpRequest removeBlob();

   @Override
   public BlobStore createClient(Function<HttpRequest, HttpResponse> fn, Module module, Properties props) {
      return createInjector(fn, module, props).getInstance(BlobStore.class);
   }
}
