package org.jclouds.collect;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;

/**
 * Tests behavior of {@code IterableWithMarkers}.
 * 
 * @author Adrian Cole
 */
@Test(testName = "IterableWithMarkersTest")
public class IterableWithMarkersTest {

   @Test
   public void testElementsEqual() {

      IterableWithMarker<String> initial = IterableWithMarkers.from(ImmutableSet.of("foo", "bar"));
      Assert.assertEquals(initial.toSet(), ImmutableSet.of("foo", "bar"));
      Assert.assertEquals(initial.nextMarker(), Optional.absent());
   }

   @Test
   public void testMarkerEqual() {

      IterableWithMarker<String> initial = IterableWithMarkers.from(ImmutableSet.of("foo", "bar"), "MARKER");
      Assert.assertEquals(initial.toSet(), ImmutableSet.of("foo", "bar"));
      Assert.assertEquals(initial.nextMarker(), Optional.of("MARKER"));
   }
}
