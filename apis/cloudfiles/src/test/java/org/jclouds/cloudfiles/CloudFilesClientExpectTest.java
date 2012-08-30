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
package org.jclouds.cloudfiles;

import org.jclouds.cloudfiles.internal.BaseCloudFilesRestClientExpectTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "CloudFilesClientExpectTest")
public class CloudFilesClientExpectTest extends BaseCloudFilesRestClientExpectTest {

   public void deleteContainerReturnsTrueOn200And404() {

      HttpRequest deleteContainer = HttpRequest
               .builder()
               .method("DELETE")
               .endpoint(
                        "https://storage101.lon3.clouddrive.com/v1/MossoCloudFS_83a9d536-2e25-4166-bd3b-a503a934f953/container")
               .addHeader("X-Auth-Token", authToken).build();

      HttpResponse containerDeleted = HttpResponse.builder().statusCode(204).message("HTTP/1.1 204 No Content").build();

      CloudFilesClient clientWhenContainerExists = requestsSendResponses(initialAuth, responseWithAuth, deleteContainer,
               containerDeleted);
      assert clientWhenContainerExists.deleteContainerIfEmpty("container");

      HttpResponse containerNotFound = HttpResponse.builder().statusCode(404).message("HTTP/1.1 404 Not Found").build();

      CloudFilesClient clientWhenContainerDoesntExist = requestsSendResponses(initialAuth, responseWithAuth, deleteContainer,
               containerNotFound);
      assert clientWhenContainerDoesntExist.deleteContainerIfEmpty("container");

   }

}