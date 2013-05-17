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
package org.jclouds.reflect;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.jclouds.reflect.Reflection2.methods;

import java.lang.reflect.Method;
import java.lang.reflect.ReflectPermission;

import org.easymock.IAnswer;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Tests {@link Reflection2#methods()} on core Java classes where
 * reflective access may be limited by a {@link SecurityManager}.
 *
 * This test has been separated out into a separate class as it modifies
 * a system-wide setting (the {@code SecurityManager}) and needs to perform 
 * cleanup to avoid affecting other tests.
 *
 * @author Andrew Phillips
 */
@Test(singleThreaded = true)
public class Reflection2CoreJavaClassesTest {
   private SecurityManager originalSecurityManager;
   private boolean securityManagerOverridden = false;

   @BeforeClass
   public void backupSecurityManager() {
      originalSecurityManager = System.getSecurityManager();
   }

   public void testCoreJavaMethodsNotMadeAccessible(final Method testMethod) {
      // a nice mock is required because plenty of other checks will be made
      SecurityManager mockSecurityManager = createNiceMock(SecurityManager.class);
      // clunky way of failing if the following method is ever called
      mockSecurityManager.checkPermission(new ReflectPermission("suppressAccessChecks"));
      expectLastCall().andStubAnswer(new IAnswer<Void>() {
            @Override
            public Void answer() throws Throwable {
               try {
                  // generate a stacktrace
                  throw new Exception();
               } catch(Exception exception) {
                  // fail *only* if being called from this test
                  for (StackTraceElement element : exception.getStackTrace()) {
                     if (element.getMethodName().equals(testMethod.getName())) {
                        throw new AssertionError("checkPermission(new ReflectPermission(\"suppressAccessChecks\")) should not be called");
                     }
                  }
               }
               return null;
            }
         });
      replay(mockSecurityManager);
      System.setSecurityManager(mockSecurityManager);
      securityManagerOverridden = true;
      methods(Enum.class);
   }

   @AfterClass(alwaysRun = true)
   public void restoreSecurityManager() {
      // will only be true if we have permission to set the SecurityManager 
      if (securityManagerOverridden) {
         System.setSecurityManager(originalSecurityManager);
      }
   }
}
