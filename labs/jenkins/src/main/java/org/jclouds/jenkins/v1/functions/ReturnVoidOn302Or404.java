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
package org.jclouds.jenkins.v1.functions;

import static com.google.common.base.Predicates.equalTo;
import static org.jclouds.http.HttpUtils.returnValueOnCodeOrNull;

import javax.inject.Singleton;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.base.Throwables;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ReturnVoidOn302Or404 implements Function<Exception, Void> {

   public Void apply(Exception from) {
      Boolean returnVal = returnValueOnCodeOrNull(from, true, Predicates.<Integer>or(equalTo(302), equalTo(404)));
      if (returnVal != null && returnVal)
         return null;
      throw Throwables.propagate(from);
   }
}
