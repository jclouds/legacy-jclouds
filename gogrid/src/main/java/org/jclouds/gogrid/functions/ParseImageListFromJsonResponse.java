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
package org.jclouds.gogrid.functions;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jclouds.gogrid.domain.ServerImage;
import org.jclouds.gogrid.domain.internal.GenericResponseContainer;
import org.jclouds.http.functions.ParseJson;

import javax.inject.Inject;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.SortedSet;

/**
 * @author Oleksiy Yarmula
 */
public class ParseImageListFromJsonResponse extends ParseJson<SortedSet<ServerImage>> {

    @Inject
    public ParseImageListFromJsonResponse(Gson gson) {
        super(gson);
    }

    public SortedSet<ServerImage> apply(InputStream stream) {
        Type setType = new TypeToken<GenericResponseContainer<ServerImage>>() {
        }.getType();
        GenericResponseContainer<ServerImage> response;
        try {
            response = gson.fromJson(new InputStreamReader(stream, "UTF-8"), setType);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("jclouds requires UTF-8 encoding", e);
        }
        return response.getList();
    }
}
