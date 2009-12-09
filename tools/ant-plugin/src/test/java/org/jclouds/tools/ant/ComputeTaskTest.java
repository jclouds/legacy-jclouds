package org.jclouds.tools.ant;

import java.io.IOException;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "compute.ComputeTaskTest")
public class ComputeTaskTest {
   private ComputeTask task;
   private ServerElement serverElement;

   @BeforeTest
   protected void setUp() throws IOException {
      this.task = new ComputeTask();

   }
}
