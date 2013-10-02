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
package org.jclouds.lifecycle;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Singleton;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Atomics;

import static org.jclouds.lifecycle.Closer.State.AVAILABLE;
import static org.jclouds.lifecycle.Closer.State.DONE;
import static org.jclouds.lifecycle.Closer.State.PROCESSING;

/**
 * This will close objects in the reverse order that they were added.
 * 
 * @author Adrian Cole
 */
@Singleton
public class Closer implements Closeable {
   // guice is single threaded. no need to lock this
   List<Closeable> methodsToClose = Lists.<Closeable> newArrayList();

   public enum State {
      AVAILABLE,
      PROCESSING,
      DONE
   }

   private final AtomicReference<State> state;

   public Closer() {
      this.state = Atomics.newReference(AVAILABLE);
   }

   public void addToClose(Closeable toClose) {
      methodsToClose.add(toClose);
   }

   public void close() throws IOException {
      if (state.compareAndSet(AVAILABLE, PROCESSING)) {
         Collections.reverse(methodsToClose);
         for (Closeable toClose : methodsToClose) {
            toClose.close();
         }
         state.set(DONE);
      }
   }

   public State getState() {
      return state.get();
   }
}
