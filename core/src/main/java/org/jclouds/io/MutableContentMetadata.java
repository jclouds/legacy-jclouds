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

import java.util.Date;

import org.jclouds.javax.annotation.Nullable;

/**
 * @author Adrian Cole
 */
public interface MutableContentMetadata extends ContentMetadata {

   void setContentLength(@Nullable Long contentLength);

   void setContentMD5(@Nullable byte[] md5);

   void setContentType(@Nullable String contentType);

   /**
    * Set Content Disposition of the payload
    * <p/>
    * Not all providers may support it
    * 
    * @param contentDisposition
    */
   void setContentDisposition(@Nullable String contentDisposition);

   /**
    * Set Content Language of the payload
    * <p/>
    * Not all providers may support it
    * 
    * @param contentLanguage
    */
   void setContentLanguage(@Nullable String contentLanguage);

   /**
    * Set Content Encoding of the payload
    * <p/>
    * Not all providers may support it
    * 
    * @param contentEncoding
    */
   void setContentEncoding(@Nullable String contentEncoding);

   void setExpires(@Nullable Date expires);
}
