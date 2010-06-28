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
package org.jclouds.gogrid.services;

import java.io.IOException;
import java.lang.reflect.Method;

import org.jclouds.gogrid.domain.ServerImageState;
import org.jclouds.gogrid.domain.ServerImageType;
import org.jclouds.gogrid.functions.ParseImageFromJsonResponse;
import org.jclouds.gogrid.functions.ParseImageListFromJsonResponse;
import org.jclouds.gogrid.options.GetImageListOptions;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.inject.TypeLiteral;

/**
 * @author Oleksiy Yarmula
 */
public class GridImageAsyncClientTest extends BaseGoGridAsyncClientTest<GridImageAsyncClient> {

   @Test
   public void testGetImageListWithOptions() throws NoSuchMethodException, IOException {
      Method method = GridImageAsyncClient.class.getMethod("getImageList",
               GetImageListOptions[].class);
      GeneratedHttpRequest<GridImageAsyncClient> httpRequest = processor.createRequest(method,
               new GetImageListOptions().onlyPublic().setState(ServerImageState.AVAILABLE).setType(
                        ServerImageType.WEB_APPLICATION_SERVER));

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/image/list?v=1.4&"
               + "isPublic=true&image.state=Available&" + "image.type=Web%20Server HTTP/1.1");
      assertHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null);

      assertResponseParserClassEquals(method, httpRequest, ParseImageListFromJsonResponse.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);
      Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/image/list?"
               + "v=1.4&isPublic=true&image.state=Available&" + "image.type=Web%20Server&"
               + "sig=3f446f171455fbb5574aecff4997b273&api_key=foo " + "HTTP/1.1");
      assertHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null);
   }

   @Test
   public void testGetImagesByName() throws NoSuchMethodException, IOException {
      Method method = GridImageAsyncClient.class.getMethod("getImagesByName", String[].class);
      GeneratedHttpRequest<GridImageAsyncClient> httpRequest = processor.createRequest(method,
               "name1", "name2");

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/image/get?v=1.4&"
               + "name=name1&name=name2 HTTP/1.1");
      assertHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null);

      assertResponseParserClassEquals(method, httpRequest, ParseImageListFromJsonResponse.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);
      Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/image/get?v=1.4&"
               + "name=name1&name=name2&" + "sig=3f446f171455fbb5574aecff4997b273&api_key=foo "
               + "HTTP/1.1");
      assertHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null);
   }

   @Test
   public void testEditImageDescription() throws NoSuchMethodException, IOException {
      Method method = GridImageAsyncClient.class.getMethod("editImageDescription", String.class,
               String.class);
      GeneratedHttpRequest<GridImageAsyncClient> httpRequest = processor.createRequest(method,
               "imageName", "newDesc");

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/image/edit?v=1.4&"
               + "image=imageName&description=newDesc HTTP/1.1");
      assertHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null);

      assertResponseParserClassEquals(method, httpRequest, ParseImageFromJsonResponse.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);
      Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/image/edit?v=1.4&"
               + "image=imageName&description=newDesc&"
               + "sig=3f446f171455fbb5574aecff4997b273&api_key=foo " + "HTTP/1.1");
      assertHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null);
   }

   @Test
   public void testEditImageFriendlyName() throws NoSuchMethodException, IOException {
      Method method = GridImageAsyncClient.class.getMethod("editImageFriendlyName", String.class,
               String.class);
      GeneratedHttpRequest<GridImageAsyncClient> httpRequest = processor.createRequest(method,
               "imageName", "newFriendlyName");

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/image/edit?v=1.4&"
               + "image=imageName&friendlyName=newFriendlyName HTTP/1.1");
      assertHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null);

      assertResponseParserClassEquals(method, httpRequest, ParseImageFromJsonResponse.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);
      Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/image/edit?v=1.4&"
               + "image=imageName&friendlyName=newFriendlyName&"
               + "sig=3f446f171455fbb5574aecff4997b273&api_key=foo " + "HTTP/1.1");
      assertHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<GridImageAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<GridImageAsyncClient>>() {
      };
   }

}
