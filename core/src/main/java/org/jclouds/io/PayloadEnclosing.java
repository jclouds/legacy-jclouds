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

import java.io.File;
import java.io.InputStream;

import org.jclouds.javax.annotation.Nullable;

/**
 * 
 * @author Adrian Cole
 */
public interface PayloadEnclosing {

   /**
    * Sets payload for the request or the content from the response. If size isn't set, this will
    * attempt to discover it.
    * 
    * @param data
    *           typically InputStream for downloads, or File, byte [], String, or InputStream for
    *           uploads.
    */
   void setPayload(Payload data);

   void setPayload(File data);

   void setPayload(byte[] data);

   void setPayload(InputStream data);

   void setPayload(String data);

   @Nullable
   Payload getPayload();

}
