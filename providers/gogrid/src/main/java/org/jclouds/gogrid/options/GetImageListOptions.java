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
package org.jclouds.gogrid.options;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.gogrid.reference.GoGridQueryParams.DATACENTER_KEY;
import static org.jclouds.gogrid.reference.GoGridQueryParams.IMAGE_STATE_KEY;
import static org.jclouds.gogrid.reference.GoGridQueryParams.IMAGE_TYPE_KEY;
import static org.jclouds.gogrid.reference.GoGridQueryParams.IS_PUBLIC_KEY;
import static org.jclouds.gogrid.reference.GoGridQueryParams.MAX_NUMBER_KEY;

import org.jclouds.gogrid.domain.ServerImageState;
import org.jclouds.gogrid.domain.ServerImageType;
import org.jclouds.http.options.BaseHttpRequestOptions;

/**
 * @author Oleksiy Yarmula
 */
public class GetImageListOptions extends BaseHttpRequestOptions {

   public GetImageListOptions setType(ServerImageType imageType) {
      checkState(!queryParameters.containsKey(IMAGE_TYPE_KEY), "Can't have duplicate image type restrictions");
      queryParameters.put(IMAGE_TYPE_KEY, imageType.toString());
      return this;
   }

   public GetImageListOptions setState(ServerImageState imageState) {
      checkState(!queryParameters.containsKey(IMAGE_STATE_KEY), "Can't have duplicate image state restrictions");
      queryParameters.put(IMAGE_STATE_KEY, imageState.toString());
      return this;
   }

   public GetImageListOptions onlyPublic() {
      checkState(!queryParameters.containsKey(IS_PUBLIC_KEY), "Can't have duplicate image visibility restrictions");
      queryParameters.put(IS_PUBLIC_KEY, "true");
      return this;
   }

   public GetImageListOptions onlyPrivate() {
      checkState(!queryParameters.containsKey(IS_PUBLIC_KEY), "Can't have duplicate image visibility restrictions");
      queryParameters.put(IS_PUBLIC_KEY, "false");
      return this;
   }

   public GetImageListOptions inDatacenter(String datacenterId) {
      checkState(!queryParameters.containsKey(DATACENTER_KEY), "Can't have duplicate datacenter id");
      queryParameters.put(DATACENTER_KEY, datacenterId);
      return this;
   }

   public GetImageListOptions maxItemsNumber(Integer maxNumber) {
      checkState(!queryParameters.containsKey(MAX_NUMBER_KEY), "Can't have duplicate parameter of max returned items");
      queryParameters.put(MAX_NUMBER_KEY, maxNumber.toString());
      return this;
   }

   public static class Builder {
      public static GetImageListOptions maxItems(int maxNumber) {
         return new GetImageListOptions().maxItemsNumber(maxNumber);
      }

      public static GetImageListOptions inDatacenter(String datacenterId) {
         return new GetImageListOptions().inDatacenter(checkNotNull(datacenterId, "datacenterId"));
      }

      public static GetImageListOptions publicWebServers() {
         return new GetImageListOptions().setState(ServerImageState.AVAILABLE).setType(
                  ServerImageType.WEB_APPLICATION_SERVER).onlyPublic();
      }

      public static GetImageListOptions publicDatabaseServers() {
         return new GetImageListOptions().setState(ServerImageState.AVAILABLE).setType(ServerImageType.DATABASE_SERVER)
                  .onlyPublic();
      }
   }

}
