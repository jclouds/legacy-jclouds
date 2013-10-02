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
package org.jclouds.blobstore.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import javax.inject.Singleton;

import org.jclouds.http.HttpMessage;
import org.jclouds.io.PayloadEnclosing;
import org.jclouds.io.Payloads;

import com.google.common.base.Function;
import com.google.common.base.Throwables;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ObjectMD5 implements Function<Object, byte[]> {

   public byte[] apply(Object from) {
      checkNotNull(from, "thing to md5");
      PayloadEnclosing payloadEnclosing;
      if (from instanceof PayloadEnclosing) {
         payloadEnclosing = (PayloadEnclosing) from;
      } else {
         payloadEnclosing = HttpMessage.builder().payload(Payloads.newPayload(from)).build();
      }
      if (payloadEnclosing.getPayload().getContentMetadata().getContentMD5() == null)
         try {
            Payloads.calculateMD5(payloadEnclosing);
         } catch (IOException e) {
            Throwables.propagate(e);
         }
      return payloadEnclosing.getPayload().getContentMetadata().getContentMD5();
   }

}
