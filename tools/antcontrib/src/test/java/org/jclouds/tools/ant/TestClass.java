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