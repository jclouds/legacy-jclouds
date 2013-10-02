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
package org.jclouds.http.functions;

import static org.jclouds.http.HttpUtils.releasePayload;

import java.io.IOException;
import java.io.InputStream;

import javax.inject.Singleton;

import org.jclouds.http.HttpException;
import org.jclouds.http.HttpResponse;
import org.jclouds.util.Strings2;

import com.google.common.base.Function;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ReturnStringIf2xx implements Function<HttpResponse, String> {

   public String apply(HttpResponse from) {
      if (from.getPayload() == null)
         return null;
      try {
         if (from.getStatusCode() >= 200) {
            InputStream payload = from.getPayload().getInput();
            String toReturn = null;
            try {
               toReturn = Strings2.toStringAndClose(payload);
            } catch (IOException e) {
               throw new HttpException(String.format(
                        "Couldn't receive response %1$s, payload: %2$s ", from, toReturn), e);
            }
            return toReturn;
         } else {
            throw new HttpException(String.format("Unhandled status code  - %1$s", from));
         }
      } finally {
         releasePayload(from);
      }
   }

}
