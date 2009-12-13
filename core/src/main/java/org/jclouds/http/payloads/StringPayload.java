/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 */
package org.jclouds.http.payloads;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.jclouds.http.Payload;

/**
 * @author Adrian Cole
 */
public class StringPayload implements Payload {
   private final String content;

   public StringPayload(String content) {
      this.content = checkNotNull(content, "content");
   }

   public String getRawContent() {
      return content;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public InputStream getContent() {
      return IOUtils.toInputStream(content);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean isRepeatable() {
      return true;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void writeTo(OutputStream outstream) throws IOException {
      IOUtils.write(content.getBytes(), outstream);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Long calculateSize() {
      return new Long(content.length());
   }

   @Override
   public String toString() {
      return content;
   }
}