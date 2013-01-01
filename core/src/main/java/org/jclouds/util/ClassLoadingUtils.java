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
package org.jclouds.util;

import java.net.URL;

import com.google.common.io.Resources;

public class ClassLoadingUtils {

    public ClassLoadingUtils() {
        //Utility Class
    }

    /**
     * Returns the url of a resource.
     * 1. The context class is being used.
     * 2. The thread context class loader is being used.
     * If both approach fail, returns null.
     *
     * @param contextClass
     * @param resourceName
     * @return
     */
    public static URL loadResource(Class<?> contextClass, String resourceName) {
        URL url = null;
        if (contextClass != null) {
            url = Resources.getResource(contextClass, resourceName);
        }
        if (url == null && Thread.currentThread().getContextClassLoader() != null) {
            url = Thread.currentThread().getContextClassLoader().getResource(resourceName);
        }
        return url;
    }
}
