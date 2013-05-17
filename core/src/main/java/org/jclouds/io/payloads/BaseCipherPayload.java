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

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;

import org.jclouds.io.Payload;

/**
 * 
 * @author Adrian Cole
 */
public abstract class BaseCipherPayload extends DelegatingPayload {

   private final Key key;

   public BaseCipherPayload(Payload delegate, Key key) {
      super(delegate);
      this.key = checkNotNull(key, "key");
   }

   public abstract Cipher initializeCipher(Key key);

   @Override
   public CipherInputStream getInput() {
      return new CipherInputStream(super.getInput(), initializeCipher(key));
   }

   @Override
   public void writeTo(OutputStream outstream) throws IOException {
      super.writeTo(new CipherOutputStream(outstream, initializeCipher(key)));
   }
}
