package org.jclouds.openstack.nova.v2_0.functions;

import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.Set;

import javax.inject.Named;

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.internal.ClassMethodArgsAndReturnVal;
import org.jclouds.openstack.nova.v2_0.domain.Extension;
import org.jclouds.openstack.nova.v2_0.extensions.ExtensionNamespaces;
import org.jclouds.openstack.nova.v2_0.extensions.KeyPairAsyncClient;
import org.jclouds.openstack.nova.v2_0.functions.PresentWhenExtensionAnnotationNamespaceEqualsAnyNamespaceInExtensionsSet;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.rest.annotations.Delegate;
import org.testng.annotations.Test;

import com.google.common.base.Functions;
import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Provides;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "PresentWhenExtensionAnnotationNamespaceEqualsAnyNamespaceInExtensionsSetTest")
public class PresentWhenExtensionAnnotationNamespaceEqualsAnyNamespaceInExtensionsSetTest {

   Extension keypairs = Extension.builder().alias("os-keypairs").name("Keypairs").namespace(
            URI.create("http://docs.openstack.org/ext/keypairs/api/v1.1")).updated(
            new SimpleDateFormatDateService().iso8601SecondsDateParse("2011-08-08T00:00:00+00:00")).description(
            "Keypair Support").build();

   @org.jclouds.openstack.v2_0.services.Extension(of = ServiceType.COMPUTE, namespace = ExtensionNamespaces.KEYPAIRS)
   static interface KeyPairIPAsyncClient {

   }

   Extension floatingIps = Extension.builder().alias("os-floating-ips").name("Floating_ips").namespace(
            URI.create("http://docs.openstack.org/ext/floating_ips/api/v1.1")).updated(
            new SimpleDateFormatDateService().iso8601SecondsDateParse("2011-06-16T00:00:00+00:00")).description(
            "Floating IPs support").build();

   @org.jclouds.openstack.v2_0.services.Extension(of = ServiceType.COMPUTE, namespace = ExtensionNamespaces.FLOATING_IPS)
   static interface FloatingIPAsyncClient {

   }

   static interface NovaAsyncClient {

      @Delegate
      Optional<FloatingIPAsyncClient> getFloatingIPExtensionForZone(String zone);

      @Delegate
      Optional<KeyPairAsyncClient> getKeyPairExtensionForZone(String zone);

   }

   ClassMethodArgsAndReturnVal getFloatingIPExtension() throws SecurityException, NoSuchMethodException {
      return ClassMethodArgsAndReturnVal.builder().clazz(FloatingIPAsyncClient.class).method(
               NovaAsyncClient.class.getDeclaredMethod("getFloatingIPExtensionForZone", String.class)).args(
               new Object[] { "expectedzone" }).returnVal("foo").build();
   }

   ClassMethodArgsAndReturnVal getKeyPairExtension() throws SecurityException, NoSuchMethodException {
      return ClassMethodArgsAndReturnVal.builder().clazz(KeyPairAsyncClient.class).method(
               NovaAsyncClient.class.getDeclaredMethod("getKeyPairExtensionForZone", String.class)).args(
               new Object[] { "expectedzone" }).returnVal("foo").build();
   }

   public void testPresentWhenExtensionsIncludeNamespaceFromAnnotationAbsentWhenNot() throws SecurityException, NoSuchMethodException {

      assertEquals(whenExtensionsInclude(keypairs, floatingIps).apply(getFloatingIPExtension()), Optional.of("foo"));
      assertEquals(whenExtensionsInclude(keypairs, floatingIps).apply(getKeyPairExtension()), Optional.of("foo"));
      assertEquals(whenExtensionsInclude(keypairs).apply(getFloatingIPExtension()), Optional.absent());
      assertEquals(whenExtensionsInclude(floatingIps).apply(getKeyPairExtension()), Optional.absent());
   }
   
   public void testZoneWithoutExtensionsReturnsAbsent() throws SecurityException, NoSuchMethodException {
      assertEquals(whenExtensionsInclude(floatingIps).apply(
               getFloatingIPExtension().toBuilder().args(new Object[] { "differentzone" }).build()), Optional.absent());
      assertEquals(whenExtensionsInclude(keypairs).apply(
               getKeyPairExtension().toBuilder().args(new Object[] { "differentzone" }).build()), Optional.absent());
   }

   /**
    * It is possible that the /extensions call returned the correct extension, but that the
    * namespaces were different, for whatever reason. One way to address this is to have a multimap
    * of the authoritative namespace to alternate onces, which could be wired up with guice
    * 
    */
   public void testPresentWhenAliasForExtensionMapsToNamespace() throws SecurityException, NoSuchMethodException {
      Extension keypairsWithDifferentNamespace = keypairs.toBuilder().namespace(
               URI.create("http://docs.openstack.org/ext/arbitrarilydifferent/keypairs/api/v1.1")).build();

      Multimap<URI, URI> aliases = ImmutableMultimap.of(keypairs.getNamespace(), keypairsWithDifferentNamespace
               .getNamespace());

      assertEquals(whenExtensionsAndAliasesInclude(ImmutableSet.of(keypairsWithDifferentNamespace), aliases).apply(
              getKeyPairExtension()), Optional.of("foo"));
      assertEquals(whenExtensionsAndAliasesInclude(ImmutableSet.of(keypairsWithDifferentNamespace), aliases).apply(
              getFloatingIPExtension()), Optional.absent());

   }

   private PresentWhenExtensionAnnotationNamespaceEqualsAnyNamespaceInExtensionsSet whenExtensionsInclude(
            Extension... extensions) {
      return whenExtensionsAndAliasesInclude(ImmutableSet.copyOf(extensions), ImmutableMultimap.<URI, URI> of());
   }

   private PresentWhenExtensionAnnotationNamespaceEqualsAnyNamespaceInExtensionsSet whenExtensionsAndAliasesInclude(
            final Set<Extension> extensions, final Multimap<URI, URI> aliases) {
      final LoadingCache<String, Set<Extension>> extensionsForZone = CacheBuilder.newBuilder().build(
               CacheLoader.from(Functions.forMap(ImmutableMap.of("expectedzone", extensions, "differentzone",
                        ImmutableSet.<Extension> of()))));

      PresentWhenExtensionAnnotationNamespaceEqualsAnyNamespaceInExtensionsSet fn = Guice.createInjector(
               new AbstractModule() {
                  @Override
                  protected void configure() {
                  }

                  @SuppressWarnings("unused")
                  @Provides
                  LoadingCache<String, Set<Extension>> getExtensions() {
                     return extensionsForZone;
                  }

                  @SuppressWarnings("unused")
                  @Provides
                  @Named("openstack.nova.extensions")
                  Multimap<URI, URI> getAliases() {
                     return aliases;
                  }
               }).getInstance(PresentWhenExtensionAnnotationNamespaceEqualsAnyNamespaceInExtensionsSet.class);
      
      return fn;
   }
}
