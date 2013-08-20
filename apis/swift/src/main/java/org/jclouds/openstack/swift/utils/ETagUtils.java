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
package org.jclouds.openstack.swift.utils;

import java.util.regex.Pattern;

import com.google.common.annotations.VisibleForTesting;

import static com.google.common.io.BaseEncoding.base16;

/**
 * @author Francis Devereux
 */
public class ETagUtils {
   private static final Pattern QUOTED_STRING = Pattern.compile("^\"(.*)\"$");

   /**
    * <p>Converts the ETag of an OpenStack object to a byte array.</p>
    *
    * <p>Not applicable to all ETags, only those of OpenStack objects. According
    * to the <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec3.html#sec3.11">HTTP spec</a>
    * an eTag can be any string, but the
    * <a href="http://docs.openstack.org/trunk/openstack-object-storage/admin/content/additional-notes-on-large-objects.html">OpenStack Object Storage Administration Guide</a>
    * says that the ETag of an OpenStack object will be an MD5 sum (and MD5 sums
    * are conventionally represented as hex strings). This method only accepts
    * hex strings as input, not arbitrary strings.</p>
    */
   public static byte[] convertHexETagToByteArray(String hexETag) {
      hexETag = unquote(hexETag);
      return base16().lowerCase().decode(hexETag);
   }

   @VisibleForTesting
   static String unquote(String eTag) {
      return QUOTED_STRING.matcher(eTag).replaceAll("$1");
   }
}
