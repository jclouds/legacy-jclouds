/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.io;

import java.io.Closeable;
import java.io.InputStream;

import com.google.common.io.InputSupplier;

/**
 * @author Adrian Cole
 */
public interface Payload extends InputSupplier<InputStream>, WriteTo, Closeable {

   /**
    * Creates a new InputStream object of the payload.
    */
   InputStream getInput();

   /**
    * Payload in its original form.
    */
   Object getRawContent();

   /**
    * Tells if the payload is capable of producing its data more than once.
    */
   boolean isRepeatable();

   /**
    * release resources used by this entity. This should be called when data is discarded.
    */
   void release();

   MutableContentMetadata getContentMetadata();

   void setContentMetadata(MutableContentMetadata in);

}
