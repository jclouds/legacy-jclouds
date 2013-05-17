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
package org.jclouds.crypto;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.crypto.Mac;

import com.google.common.annotations.Beta;
import com.google.common.io.ByteProcessor;

/**
 * functions for {@link Mac}
 * 
 * @author Adrian Cole
 */
@Beta
public class Macs {

   /**
    * Computes and returns the MAC value for a supplied input stream.
    * 
    * @param mac
    *           the mac object
    * @return the result of {@link Mac#doFinal()} on {@link ByteProcessor#getResult()}
    */
   public static ByteProcessor<byte[]> asByteProcessor(final Mac mac) {
      checkNotNull(mac, "mac");
      return new ByteProcessor<byte[]>() {
         public boolean processBytes(byte[] buf, int off, int len) {
            mac.update(buf, off, len);
            return true;
         }

         public byte[] getResult() {
            return mac.doFinal();
         }
      };
   }

}
