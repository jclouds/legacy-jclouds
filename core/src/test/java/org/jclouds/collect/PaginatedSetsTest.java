package org.jclouds.collect;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;

import org.easymock.EasyMock;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;

/**
 * Tests behavior of {@code PaginatedSets}.
 * 
 * @author Adrian Cole
 */
@Test(testName = "PaginatedSetsTest")
public class PaginatedSetsTest {

   @SuppressWarnings("unchecked")
   @Test
   public void testSinglePageResultReturnsSame() {

      PaginatedSet<String> initial = PaginatedSet.copyOf(ImmutableSet.of("foo", "bar"));
      Function<String, PaginatedSet<String>> markerToNext = createMock(Function.class);

      EasyMock.replay(markerToNext);

      Assert.assertSame(PaginatedSets.lazyContinue(initial, markerToNext), initial);

      EasyMock.verify(markerToNext);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testMultiPage2Pages() {

      PaginatedSet<String> initial = PaginatedSet.copyOfWithMarker(ImmutableSet.of("foo", "bar"), "MARKER1");
      Function<String, PaginatedSet<String>> markerToNext = createMock(Function.class);

      expect(markerToNext.apply("MARKER1")).andReturn(
               PaginatedSet.copyOfWithMarker(ImmutableSet.of("boo", "baz"), null));

      EasyMock.replay(markerToNext);

      Assert.assertEquals(ImmutableSet.copyOf(PaginatedSets.lazyContinue(initial, markerToNext)), ImmutableSet.of(
               "foo", "bar", "boo", "baz"));
      
      EasyMock.verify(markerToNext);

   }
   
   @SuppressWarnings("unchecked")
   @Test
   public void testMultiPage3Pages() {

      PaginatedSet<String> initial = PaginatedSet.copyOfWithMarker(ImmutableSet.of("foo", "bar"), "MARKER1");
      Function<String, PaginatedSet<String>> markerToNext = createMock(Function.class);

      expect(markerToNext.apply("MARKER1")).andReturn(
               PaginatedSet.copyOfWithMarker(ImmutableSet.of("boo", "baz"), "MARKER2"));

      expect(markerToNext.apply("MARKER2")).andReturn(
               PaginatedSet.copyOfWithMarker(ImmutableSet.of("ham", "cheeze"), null));

      EasyMock.replay(markerToNext);

      Assert.assertEquals(ImmutableSet.copyOf(PaginatedSets.lazyContinue(initial, markerToNext)), ImmutableSet.of(
               "foo", "bar", "boo", "baz", "ham", "cheeze"));
      
      EasyMock.verify(markerToNext);

   }

}
