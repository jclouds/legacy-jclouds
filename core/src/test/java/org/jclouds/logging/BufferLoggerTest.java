package org.jclouds.logging;

import java.util.logging.Level;

import org.jclouds.logging.BufferLogger.Record;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class BufferLoggerTest {

   public void testLogCapturesRightMessages() {
      BufferLogger b = new BufferLogger("foo");
      b.setLevel(Level.INFO);
      b.info("hi 1");
      b.error(new Throwable("check"), "hi 2");
      b.debug("hi 3 nope");
      
      Record r;
      r = b.assertLogContains("hi 1");
      Assert.assertEquals(Level.INFO, r.getLevel());
      Assert.assertNull(r.getTrace());
      
      r = b.assertLogContains("hi 2");
      Assert.assertEquals(Level.SEVERE, r.getLevel());
      Assert.assertEquals(r.getTrace().getMessage(), "check");
      
      b.assertLogDoesntContain("hi 3");
   }
   
}
