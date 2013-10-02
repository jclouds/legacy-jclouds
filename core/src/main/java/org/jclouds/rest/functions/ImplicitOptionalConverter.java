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
package org.jclouds.rest.functions;

import org.jclouds.reflect.InvocationSuccess;
import org.jclouds.rest.annotations.ApiVersion;
import org.jclouds.rest.annotations.SinceApiVersion;
import org.jclouds.rest.config.RestClientModule;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.inject.ImplementedBy;

/**
 * When a client marked @Delegate is optional, the implementation of this is
 * responsible for creating the optional object.
 * 
 * For example.
 * 
 * <pre>
 * interface MyCloud {
 *    &#064;Delegate
 *    Optional&lt;KeyPairClient&gt; getKeyPairExtensionForRegion(String region);
 * }
 * </pre>
 * 
 * The input object of type {@link InvocationSuccess} will include the
 * following.
 * <ol>
 * <li>the class declaring the method that returns optional:
 * {@link InvocationSuccess#getClazz}; in the example above,
 * {@code MyCloud}</li>
 * <li>the method returning the optional:
 * {@link InvocationSuccess#getMethod}; in the example above,
 * {@code getKeyPairExtensionForRegion}</li>
 * <li>the args passed to that method at runtime:
 * {@link InvocationSuccess#getArgs}; for example {@code North}</li>
 * <li>the rest client to be enclosed in the optional, should you choose to
 * return it: {@link InvocationSuccess#getReturnVal}; in the example
 * above, an implementation of {@code KeyPairClient}</li>
 * </ol>
 * 
 * Using this context, your implementation of {@link ImplicitOptionalConverter}
 * can perform whatever you need, when deciding if the the returnVal is present
 * and available. Here are some ideas:
 * <ul>
 * <li>call a smoke test command</li>
 * <li>look at the annotations on the class and compare those against a
 * configuration switch enabling the extension</li>
 * <li>inspect the health of the client, perhaps looking for error status</li>
 * <li>call another api which can validate the feature can be presented</li>
 * </ul>
 * 
 * The {@link PresentWhenApiVersionLexicographicallyAtOrAfterSinceApiVersion
 * default implementation} returns present if no {@link SinceApiVersion}
 * annotation is assigned, or the value is less than or equal to the current
 * {@link ApiVersion}. To override this, add the following in your subclass
 * override of {@link RestClientModule#configure} method:
 * 
 * <pre>
 * bind(ImplicitOptionalConverter.class).to(MyCustomOptionalConverter.class);
 * </pre>
 * 
 * @author Adrian Cole
 */
@Beta
@ImplementedBy(PresentWhenApiVersionLexicographicallyAtOrAfterSinceApiVersion.class)
public interface ImplicitOptionalConverter extends Function<InvocationSuccess, Optional<Object>> {

}
