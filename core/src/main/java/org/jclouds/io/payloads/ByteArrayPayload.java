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
package org.jclouds.io.payloads;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @author Adrian Cole
 */
public class ByteArrayPayload extends BasePayload<byte[]> {
   public ByteArrayPayload(byte[] content) {
      this(content, null);
   }

   public ByteArrayPayload(byte[] content, byte[] md5) {
      super(content);
      getContentMetadata().setContentLength(Long.valueOf(checkNotNull(content, "content").length));
      getContentMetadata().setContentMD5(md5);
      checkArgument(content.length >= 0, "length cannot me negative");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public InputStream getInput() {
      return new ByteArrayInputStream(content);
   }

}
