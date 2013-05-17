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
package org.jclouds.gogrid.services;

import static org.jclouds.reflect.Reflection2.method;

import java.io.IOException;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.gogrid.domain.ServerImageState;
import org.jclouds.gogrid.domain.ServerImageType;
import org.jclouds.gogrid.functions.ParseImageFromJsonResponse;
import org.jclouds.gogrid.functions.ParseImageListFromJsonResponse;
import org.jclouds.gogrid.options.GetImageListOptions;
import org.jclouds.gogrid.options.SaveImageOptions;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.reflect.Invokable;
/**
 * Tests behavior of {@code GridImageAsyncClient}
 * 
 * @author Oleksiy Yarmula
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "GridImageAsyncClientTest")
public class GridImageAsyncClientTest extends BaseGoGridAsyncClientTest<GridImageAsyncClient> {

   @Test
   public void testGetImageListWithOptions() throws NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(GridImageAsyncClient.class, "getImageList", GetImageListOptions[].class);
      GeneratedHttpRequest httpRequest = processor.createRequest(
            method, ImmutableList.<Object> of(
            new GetImageListOptions().onlyPublic().setState(ServerImageState.AVAILABLE)
                  .setType(ServerImageType.WEB_APPLICATION_SERVER)));

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/image/list?v=1.5&"
            + "isPublic=true&image.state=Available&" + "image.type=Web%20Server HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseImageListFromJsonResponse.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(httpRequest);
      httpRequest = (GeneratedHttpRequest) Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/image/list?"
            + "v=1.5&isPublic=true&image.state=Available&" + "image.type=Web%20Server&"
            + "sig=e9aafd0a5d4c69bb24536be4bce8a528&api_key=identity " + "HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);
   }

   @Test
   public void testGetImagesByName() throws NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(GridImageAsyncClient.class, "getImagesByName", String[].class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of("name1", "name2"));

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/image/get?v=1.5&"
            + "name=name1&name=name2 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseImageListFromJsonResponse.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(httpRequest);
      httpRequest = (GeneratedHttpRequest) Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/image/get?v=1.5&"
            + "name=name1&name=name2&" + "sig=e9aafd0a5d4c69bb24536be4bce8a528&api_key=identity " + "HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);
   }

   @Test
   public void testEditImageDescription() throws NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(GridImageAsyncClient.class, "editImageDescription", String.class, String.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of("imageName", "newDesc"));

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/image/edit?v=1.5&"
            + "image=imageName&description=newDesc HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseImageFromJsonResponse.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(httpRequest);
      httpRequest = (GeneratedHttpRequest) Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/image/edit?v=1.5&"
            + "image=imageName&description=newDesc&" + "sig=e9aafd0a5d4c69bb24536be4bce8a528&api_key=identity " + "HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);
   }

   @Test
   public void testEditImageFriendlyName() throws NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(GridImageAsyncClient.class, "editImageFriendlyName", String.class, String.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of("imageName", "newFriendlyName"));

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/image/edit?v=1.5&"
            + "image=imageName&friendlyName=newFriendlyName HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseImageFromJsonResponse.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(httpRequest);
      httpRequest = (GeneratedHttpRequest) Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/image/edit?v=1.5&"
            + "image=imageName&friendlyName=newFriendlyName&" + "sig=e9aafd0a5d4c69bb24536be4bce8a528&api_key=identity "
            + "HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);
   }

   @Test
   public void testDeleteById() throws NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(GridImageAsyncClient.class, "deleteById", long.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(11l));

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/image/delete?v=1.5&id=11 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseImageFromJsonResponse.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);
   }

   @Test
   public void testSaveImageFromServerNoOptions() throws NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(GridImageAsyncClient.class, "saveImageFromServer", String.class, String.class,
            SaveImageOptions[].class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of("friendly", "serverName"));

      assertRequestLineEquals(httpRequest,
            "GET https://api.gogrid.com/api/grid/image/save?v=1.5&friendlyName=friendly&server=serverName HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseImageFromJsonResponse.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

   }

   @Test
   public void testSaveImageOptions() throws NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(GridImageAsyncClient.class, "saveImageFromServer", String.class, String.class,
            SaveImageOptions[].class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of("friendly", "serverName",
            new SaveImageOptions().withDescription("fooy")));

      assertRequestLineEquals(
            httpRequest,
            "GET https://api.gogrid.com/api/grid/image/save?v=1.5&friendlyName=friendly&server=serverName&description=fooy HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseImageFromJsonResponse.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

   }
}
