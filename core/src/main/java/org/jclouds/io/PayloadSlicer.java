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

import org.jclouds.io.internal.BasePayloadSlicer;

import com.google.inject.ImplementedBy;


/**
 * 
 * @author Adrian Cole
 */
@ImplementedBy(BasePayloadSlicer.class)
public interface PayloadSlicer {
   /**
    * Returns a {@link Payload} that returns input streams from the an underlying payload, where
    * each stream starts at the given offset and is limited to the specified number of bytes.
    * 
    * @param input
    *           the payload from which to get the raw streams
    * @param offset
    *           the offset in bytes into the underlying stream where the returned streams will start
    * @param length
    *           the maximum length of the returned streams
    * @throws IllegalArgumentException
    *            if offset or length are negative
    */
   Payload slice(Payload input, long offset, long length);
}
