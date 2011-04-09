/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.mezeo.pcs2.binders;

import java.io.File;

import javax.inject.Singleton;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.utils.ModifyRequest;
import org.jclouds.mezeo.pcs2.domain.PCSFile;
import org.jclouds.rest.Binder;

import com.google.common.collect.ImmutableMultimap;

/**
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class BindFileInfoToXmlPayload implements Binder {

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      PCSFile blob = (PCSFile) input;
      String file = String.format("<file><name>%s</name><mime_type>%s</mime_type><public>false</public></file>",
            new File(blob.getMetadata().getName()).getName(), blob.getMetadata().getMimeType());
      request.setPayload(file);
      return ModifyRequest.replaceHeaders(request, ImmutableMultimap.<String, String> of(HttpHeaders.CONTENT_LENGTH,
            file.getBytes().length + "", HttpHeaders.CONTENT_TYPE, "application/vnd.csp.file-info+xml"));
   }
}
