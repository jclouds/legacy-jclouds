package org.jclouds.collect;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;

import org.easymock.EasyMock;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;

/**
 * Tests behavior of {@code PaginatedIterables}.
 * 
 * @author Adrian Cole
 */
@Test(testName = "PaginatedIterablesTest")
public class PaginatedIterablesTest {

   @SuppressWarnings("unchecked")
   @Test
   public void testSinglePageResultReturnsSame() {

      PaginatedIterable<String> initial = PaginatedIterables.forward(ImmutableSet.of("foo", "bar"));
      Function<Object, PaginatedIterable<String>> markerToNext = createMock(Function.class);

      EasyMock.replay(markerToNext);

      Assert.assertSame(PaginatedIterables.lazyContinue(initial, markerToNext), initial);

      EasyMock.verify(markerToNext);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testMultiPage2Pages() {

      PaginatedIterable<String> initial = PaginatedIterables.forwardWithMarker(ImmutableSet.of("foo", "bar"), "MARKER1");
      Function<Object, PaginatedIterable<String>> markerToNext = createMock(Function.class);

      expect(markerToNext.apply("MARKER1")).andReturn(
               PaginatedIterables.forwardWithMarker(ImmutableSet.of("boo", "baz"), null));

      EasyMock.replay(markerToNext);

      Assert.assertEquals(ImmutableSet.copyOf(PaginatedIterables.lazyContinue(initial, markerToNext)), ImmutableSet.of(
               "foo", "bar", "boo", "baz"));
      
      EasyMock.verify(markerToNext);

   }
   
   @SuppressWarnings("unchecked")
   @Test
   public void testMultiPage3Pages() {

      PaginatedIterable<String> initial = PaginatedIterables.forwardWithMarker(ImmutableSet.of("foo", "bar"), "MARKER1");
      Function<Object, PaginatedIterable<String>> markerToNext = createMock(Function.class);

      expect(markerToNext.apply("MARKER1")).andReturn(
               PaginatedIterables.forwardWithMarker(ImmutableSet.of("boo", "baz"), "MARKER2"));

      expect(markerToNext.apply("MARKER2")).andReturn(
               PaginatedIterables.forwardWithMarker(ImmutableSet.of("ham", "cheeze"), null));

      EasyMock.replay(markerToNext);

      Assert.assertEquals(ImmutableSet.copyOf(PaginatedIterables.lazyContinue(initial, markerToNext)), ImmutableSet.of(
               "foo", "bar", "boo", "baz", "ham", "cheeze"));
      
      EasyMock.verify(markerToNext);

   }

}
