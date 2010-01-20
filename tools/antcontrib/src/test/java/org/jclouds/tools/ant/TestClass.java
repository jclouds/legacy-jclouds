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

package org.jclouds.tools.ant;

import java.io.File;
import java.util.Arrays;

public class TestClass {
   public static void main(String... args) {
      System.out.println("env:");
      System.out.println(System.getenv(args[0]));
      File cwd = new File(System.getProperty("user.dir"));
      System.out.println("children:");
      for (File child : cwd.listFiles())
         System.out.println("    " + child);
      System.out.println("what you wrote:");
      System.out.println(Arrays.asList(args));
      System.err.println("this is the error stream");
      System.out.println("will exit 3:");
      System.exit(3);
   }
}