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
package org.jclouds.io;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStream;

import com.google.common.annotations.Beta;
import com.google.common.io.ByteSource;

/**
 * functions related to or replacing those in {@link ByteSource}
 * 
 * @author Adrian Cole
 */
@Beta
public class ByteSources {

   /**
    * always returns the same stream
    * 
    * @param in
    *           stream to always return
    */
   public static ByteSource asByteSource(final InputStream in) {
      checkNotNull(in, "in");
      return new ByteSource() {
         @Override
         public InputStream openStream() throws IOException {
            return in;
         }
      };
   }

}
