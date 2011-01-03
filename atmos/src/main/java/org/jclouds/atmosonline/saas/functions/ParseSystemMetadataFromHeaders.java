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

package org.jclouds.atmosonline.saas.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.atmosonline.saas.domain.FileType;
import org.jclouds.atmosonline.saas.domain.SystemMetadata;
import org.jclouds.atmosonline.saas.reference.AtmosStorageHeaders;
import org.jclouds.crypto.CryptoStreams;
import org.jclouds.date.DateService;
import org.jclouds.http.HttpResponse;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

/**
 * @author Adrian Cole
 */
@Singleton
public class ParseSystemMetadataFromHeaders implements Function<HttpResponse, SystemMetadata> {
   private final DateService dateService;

   @Inject
   public ParseSystemMetadataFromHeaders(DateService dateService) {
      this.dateService = checkNotNull(dateService, "dateService");
   }

   public SystemMetadata apply(HttpResponse from) {
      checkNotNull(from, "http response");
      String meta = checkNotNull(from.getFirstHeaderOrNull(AtmosStorageHeaders.META), AtmosStorageHeaders.META);
      Map<String, String> metaMap = Maps.newHashMap();
      String[] metas = meta.split(", ");
      for (String entry : metas) {
         String[] entrySplit = entry.split("=");
         metaMap.put(entrySplit[0], entrySplit[1]);
      }
      assert metaMap.size() >= 12 : String.format("Should be 12 entries in %s", metaMap);
      byte[] md5 = metaMap.containsKey("content-md5") ? CryptoStreams.hex(metaMap.get("content-md5")) : null;
      return new SystemMetadata(md5, dateService.iso8601SecondsDateParse(checkNotNull(metaMap.get("atime"), "atime")),
            dateService.iso8601SecondsDateParse(checkNotNull(metaMap.get("ctime"), "ctime")), checkNotNull(
                  metaMap.get("gid"), "gid"), dateService.iso8601SecondsDateParse(checkNotNull(metaMap.get("itime"),
                  "itime")), dateService.iso8601SecondsDateParse(checkNotNull(metaMap.get("mtime"), "mtime")),
            Integer.parseInt(checkNotNull(metaMap.get("nlink"), "nlink")), checkNotNull(metaMap.get("objectid"),
                  "objectid"), checkNotNull(metaMap.get("objname"), "objname"), checkNotNull(metaMap.get("policyname"),
                  "policyname"), Long.parseLong(checkNotNull(metaMap.get("size"), "size")),
            FileType.fromValue(checkNotNull(metaMap.get("type"), "type")), checkNotNull(metaMap.get("uid"), "uid"));
   }
}