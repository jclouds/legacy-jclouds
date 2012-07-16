package org.jclouds.collect;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;

import org.easymock.EasyMock;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Tests behavior of {@code IterableWithMarkers}.
 * 
 * @author Adrian Cole
 */
@Test(testName = "PagedIteratorsTest")
public class PagedIteratorsTest {

   @SuppressWarnings("unchecked")
   @Test
   public void testSinglePageResultReturnsSame() {

      IterableWithMarker<String> initial = IterableWithMarkers.from(ImmutableSet.of("foo", "bar"));
      Function<Object, IterableWithMarker<String>> markerToNext = createMock(Function.class);

      EasyMock.replay(markerToNext);

      Assert.assertSame(PagedIterators.advancing(initial, markerToNext).next(), initial);

      EasyMock.verify(markerToNext);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testMultiPage2Pages() {

      IterableWithMarker<String> initial = IterableWithMarkers.from(ImmutableSet.of("foo", "bar"), "MARKER1");
      Function<Object, IterableWithMarker<String>> markerToNext = createMock(Function.class);

      expect(markerToNext.apply("MARKER1")).andReturn(IterableWithMarkers.from(ImmutableSet.of("boo", "baz"), null));

      EasyMock.replay(markerToNext);

      Assert.assertEquals(ImmutableSet.copyOf(Iterables.concat(ImmutableSet.copyOf(PagedIterators.advancing(initial,
               markerToNext)))), ImmutableSet.of("foo", "bar", "boo", "baz"));

      EasyMock.verify(markerToNext);

   }

   @SuppressWarnings("unchecked")
   @Test
   public void testMultiPage3Pages() {

      IterableWithMarker<String> initial = IterableWithMarkers.from(ImmutableSet.of("foo", "bar"), "MARKER1");
      Function<Object, IterableWithMarker<String>> markerToNext = createMock(Function.class);

      expect(markerToNext.apply("MARKER1")).andReturn(
               IterableWithMarkers.from(ImmutableSet.of("boo", "baz"), "MARKER2"));

      expect(markerToNext.apply("MARKER2")).andReturn(IterableWithMarkers.from(ImmutableSet.of("ham", "cheeze"), null));

      EasyMock.replay(markerToNext);

      Assert.assertEquals(ImmutableSet.copyOf(Iterables.concat(ImmutableSet.copyOf(PagedIterators.advancing(initial,
               markerToNext)))), ImmutableSet.of("foo", "bar", "boo", "baz", "ham", "cheeze"));

      EasyMock.verify(markerToNext);

   }

   @SuppressWarnings("unchecked")
   @Test
   public void testMultiPage3PagesNextMarkerSetCorrectly() {

      IterableWithMarker<String> initial = IterableWithMarkers.from(ImmutableSet.of("foo", "bar"), "MARKER1");
      Function<Object, IterableWithMarker<String>> markerToNext = createMock(Function.class);
      IterableWithMarker<String> second = IterableWithMarkers.from(ImmutableSet.of("boo", "baz"), "MARKER2");
      expect(markerToNext.apply("MARKER1")).andReturn(second);
      IterableWithMarker<String> third = IterableWithMarkers.from(ImmutableSet.of("ham", "cheeze"), null);
      expect(markerToNext.apply("MARKER2")).andReturn(third);

      EasyMock.replay(markerToNext);
      PagedIterator<String> iterator = PagedIterators.advancing(initial, markerToNext);

      Assert.assertEquals(iterator.hasNext(), true);
      Assert.assertEquals(iterator.nextMarker(), Optional.of("MARKER1"));
      Assert.assertEquals(iterator.next(), initial);
      Assert.assertEquals(iterator.hasNext(), true);
      Assert.assertEquals(iterator.nextMarker(), Optional.of("MARKER2"));
      Assert.assertEquals(iterator.next(), second);
      Assert.assertEquals(iterator.hasNext(), true);
      Assert.assertEquals(iterator.nextMarker(), Optional.absent());
      Assert.assertEquals(iterator.next(), third);
      Assert.assertEquals(iterator.hasNext(), false);
      Assert.assertEquals(iterator.nextMarker(), Optional.absent());
      EasyMock.verify(markerToNext);

   }

}
