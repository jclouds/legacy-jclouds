package org.jclouds.collect;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;

import org.easymock.EasyMock;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;

/**
 * Tests behavior of {@code IterableWithMarkers}.
 * 
 * @author Adrian Cole
 */
@Test(testName = "PagedIterablesTest")
public class PagedIterablesTest {

   @SuppressWarnings("unchecked")
   @Test
   public void testSinglePageResultReturnsSame() {

      IterableWithMarker<String> initial = IterableWithMarkers.from(ImmutableSet.of("foo", "bar"));
      Function<Object, IterableWithMarker<String>> markerToNext = createMock(Function.class);

      EasyMock.replay(markerToNext);

      PagedIterable<String> iterable = PagedIterables.advance(initial, markerToNext);

      Assert.assertSame(iterable.get(0), initial);

      EasyMock.verify(markerToNext);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testConcatPage3Pages() {

      IterableWithMarker<String> initial = IterableWithMarkers.from(ImmutableSet.of("foo", "bar"), "MARKER1");
      Function<Object, IterableWithMarker<String>> markerToNext = createMock(Function.class);

      expect(markerToNext.apply("MARKER1")).andReturn(
               IterableWithMarkers.from(ImmutableSet.of("boo", "baz"), "MARKER2"));

      expect(markerToNext.apply("MARKER2")).andReturn(IterableWithMarkers.from(ImmutableSet.of("ham", "cheeze"), null));

      EasyMock.replay(markerToNext);

      PagedIterable<String> iterable = PagedIterables.advance(initial, markerToNext);

      Assert.assertEquals(iterable.concat().toImmutableSet(),
               ImmutableSet.of("foo", "bar", "boo", "baz", "ham", "cheeze"));

      EasyMock.verify(markerToNext);

   }

}
