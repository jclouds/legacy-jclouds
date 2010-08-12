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

package org.jclouds.mezeo.pcs2.functions;


/**
 * @author Adrian Cole
 */
public class AssembleBlobFromBlobMetadataCallTest {

//   @SuppressWarnings("unchecked")
//   @Test(expectedExceptions = NullPointerException.class)
//   public void testCall() throws HttpException {
//      FileMetadata metadata = new FileMetadata("blob");
//      metadata.setSize(103);
//      ConcurrentMap<Key, FileMetadata> mdCache = createMock(ConcurrentMap.class);
//      InputStream data = createMock(InputStream.class);
//      AssembleBlobFromContentAndMetadataCache callable = new AssembleBlobFromContentAndMetadataCache(
//               mdCache);
//      HttpResponse response = createMock(HttpResponse.class);
//      expect(mdCache.get(new Key("container", "blob"))).andReturn(metadata);
//      expect(response.getContent()).andReturn(data);
//      replay(mdCache);
//      replay(response);
//      PCSFile file = callable.apply(response);
//      assertEquals(file.getMetadata(), metadata);
//      assertEquals(file.getName(), "blob");
//      assertEquals(file.getData(), data);
//      assertEquals(file.getContentLength(), metadata.getSize());
//   }

}
