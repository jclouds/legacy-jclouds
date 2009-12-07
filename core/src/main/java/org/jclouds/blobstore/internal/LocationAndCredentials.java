/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.blobstore.internal;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jclouds.util.Utils;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

/**
 * Used to extract the URI and authentication data from a String. Note that the java URI class
 * breaks, if there are special characters like '/' present. Otherwise, we wouldn't need this class,
 * and we could simply use URI.create("uri").getUserData();
 * 
 * @author Adrian Cole
 */
public class LocationAndCredentials {
   public static final Pattern URI_PATTERN = Pattern.compile("([a-z0-9]+)://([^:]*):(.*)@(.*)");
   public static final Pattern PATTERN_THAT_BREAKS_URI = Pattern.compile("[a-z0-9]+://.*/.*@.*"); // slash
   // in
   // userdata
   // breaks
   // URI.create()
   public final URI uri;
   public final String acccount;
   public final String key;

   public LocationAndCredentials(URI uri, String acccount, String key) {
      this.uri = checkNotNull(uri, "uri");
      checkArgument(uri.getHost() != null, "type missing from %s", uri.toASCIIString());
      checkArgument(uri.getPath() != null, "path missing from %s", uri.toASCIIString());
      this.acccount = acccount;
      this.key = key;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((acccount == null) ? 0 : acccount.hashCode());
      result = prime * result + ((key == null) ? 0 : key.hashCode());
      result = prime * result + ((uri == null) ? 0 : uri.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      LocationAndCredentials other = (LocationAndCredentials) obj;
      if (acccount == null) {
         if (other.acccount != null)
            return false;
      } else if (!acccount.equals(other.acccount))
         return false;
      if (key == null) {
         if (other.key != null)
            return false;
      } else if (!key.equals(other.key))
         return false;
      if (uri == null) {
         if (other.uri != null)
            return false;
      } else if (!uri.equals(other.uri))
         return false;
      return true;
   }

   public static LocationAndCredentials parse(String uriPath) {
      if (uriPath.indexOf('@') != 1) {
         List<String> parts = Lists.newArrayList(Splitter.on('@').split(uriPath));
         String path = parts.remove(parts.size() - 1);
         parts.add(Utils.urlEncode(path, '/', ':'));
         uriPath = Joiner.on('@').join(parts);
      } else {
         List<String> parts = Lists.newArrayList(Splitter.on('/').split(uriPath));
         String path = parts.remove(parts.size() - 1);
         parts.add(Utils.urlEncode(path, ':'));
         uriPath = Joiner.on('/').join(parts);
      }
      LocationAndCredentials locationAndCredentials;

      if (PATTERN_THAT_BREAKS_URI.matcher(uriPath).matches()) {
         // Compile and use regular expression
         Matcher matcher = URI_PATTERN.matcher(uriPath);
         if (matcher.find()) {
            String scheme = matcher.group(1);
            String rest = matcher.group(4);
            String account = matcher.group(2);
            String key = matcher.group(3);
            locationAndCredentials = new LocationAndCredentials(URI.create(String.format("%s://%s",
                     scheme, rest)), account, key);
         } else {
            throw new IllegalArgumentException("bad syntax");
         }
      } else {
         URI uri = URI.create(uriPath);
         String account = null;
         String key = null;
         if (uri.getUserInfo() != null) {
            List<String> userInfo = Lists.newArrayList(Splitter.on(':').split(uri.getUserInfo()));
            account = userInfo.get(0);
            key = userInfo.size() > 1 ? userInfo.get(1) : null;
         }
         locationAndCredentials = new LocationAndCredentials(uri, account, key);
      }
      return locationAndCredentials;
   }
}