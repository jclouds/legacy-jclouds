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
package org.jclouds.http;

import java.util.concurrent.ExecutionException;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Command that utilizes RESTFul apis and extracts <code>T</code> from the HttpResponse.
 * 
 * @author Adrian Cole
 */
public interface TransformingHttpCommand<T> extends HttpCommand {

   /**
    * invoke and transform the response {@code <R>} into value type {@code <T>}
    * 
    * @return future containing the expected value
    * 
    * @throws ExecutionException
    *            if there is a fatal error preventing the command from invoking
    */
   ListenableFuture<T> execute() throws ExecutionException;
}
