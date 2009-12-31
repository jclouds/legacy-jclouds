/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.rimuhosting.miro.binder;

import static com.google.common.base.Preconditions.checkState;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.binders.BindToJsonPayload;

import java.util.HashMap;
import java.util.Map;

/**
 * Generic binder for RimuHosting POSTS/PUTS. In the form of
 *
 * {"request":{...}}
 *
 * @author Ivan Meredith
 */
public class RimuHostingJsonBinder extends BindToJsonPayload {
  public void bindToRequest(HttpRequest request, Map<String, String> postParams) {
      bindToRequest(request, (Object) postParams);
   }

   public void bindToRequest(HttpRequest request, Object toBind) {
       checkState(gson != null, "Program error: gson should have been injected at this point");
       Map<String, Object> test = new HashMap<String, Object>();
       test.put("request", toBind);
       super.bindToRequest(request, test);
   }
}
