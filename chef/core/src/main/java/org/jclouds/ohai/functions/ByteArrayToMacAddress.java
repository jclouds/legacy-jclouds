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

package org.jclouds.ohai.functions;

import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.partition;
import static com.google.common.primitives.Bytes.asList;
import static com.google.common.primitives.Bytes.toArray;

import java.util.List;

import javax.inject.Singleton;

import org.jclouds.crypto.CryptoStreams;

import com.google.common.base.Function;
import com.google.common.base.Joiner;

/**
 * 
 * Creates a string in the form: {@code 00:26:bb:09:e6:c4 }
 * 
 * @author Adrian Cole
 */
@Singleton
public class ByteArrayToMacAddress implements Function<byte[], String> {

   @Override
   public String apply(byte[] from) {
      return Joiner.on(':').join(transform(partition(asList(from), 1), new Function<List<Byte>, String>() {

         @Override
         public String apply(List<Byte> from) {
            return CryptoStreams.hex(toArray(from));
         }

      }));
   }

}