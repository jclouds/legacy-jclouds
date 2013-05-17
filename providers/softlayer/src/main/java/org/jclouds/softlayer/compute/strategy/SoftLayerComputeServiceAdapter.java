/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.softlayer.compute.strategy;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Predicates.and;
import static com.google.common.collect.Iterables.contains;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.get;
import static org.jclouds.util.Predicates2.retry;
import static org.jclouds.softlayer.predicates.ProductItemPredicates.capacity;
import static org.jclouds.softlayer.predicates.ProductItemPredicates.categoryCode;
import static org.jclouds.softlayer.predicates.ProductItemPredicates.matches;
import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_VIRTUALGUEST_CPU_REGEX;
import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_VIRTUALGUEST_DISK0_TYPE;
import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_VIRTUALGUEST_LOGIN_DETAILS_DELAY;
import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_VIRTUALGUEST_PORT_SPEED;

import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.collect.Memoized;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.logging.Logger;
import org.jclouds.softlayer.SoftLayerClient;
import org.jclouds.softlayer.compute.functions.ProductItemToImage;
import org.jclouds.softlayer.compute.options.SoftLayerTemplateOptions;
import org.jclouds.softlayer.domain.Datacenter;
import org.jclouds.softlayer.domain.Password;
import org.jclouds.softlayer.domain.ProductItem;
import org.jclouds.softlayer.domain.ProductItemPrice;
import org.jclouds.softlayer.domain.ProductOrder;
import org.jclouds.softlayer.domain.ProductOrderReceipt;
import org.jclouds.softlayer.domain.ProductPackage;
import org.jclouds.softlayer.domain.VirtualGuest;

import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Iterables;

/**
 * defines the connection between the {@link SoftLayerClient} implementation and
 * the jclouds {@link ComputeService}
 * 
 */
@Singleton
public class SoftLayerComputeServiceAdapter implements
      ComputeServiceAdapter<VirtualGuest, Iterable<ProductItem>, ProductItem, Datacenter> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final SoftLayerClient client;
   private final Supplier<ProductPackage> productPackageSupplier;
   private final Predicate<VirtualGuest> loginDetailsTester;
   private final long guestLoginDelay;
   private final Pattern cpuPattern;
   private final Pattern disk0Type;
   private final float portSpeed;
   private final Iterable<ProductItemPrice> prices;

   @Inject
   public SoftLayerComputeServiceAdapter(SoftLayerClient client,
         VirtualGuestHasLoginDetailsPresent virtualGuestHasLoginDetailsPresent,
         @Memoized Supplier<ProductPackage> productPackageSupplier, Iterable<ProductItemPrice> prices,
         @Named(PROPERTY_SOFTLAYER_VIRTUALGUEST_CPU_REGEX) String cpuRegex,
         @Named(PROPERTY_SOFTLAYER_VIRTUALGUEST_DISK0_TYPE) String disk0Type,
         @Named(PROPERTY_SOFTLAYER_VIRTUALGUEST_PORT_SPEED) float portSpeed,
         @Named(PROPERTY_SOFTLAYER_VIRTUALGUEST_LOGIN_DETAILS_DELAY) long guestLoginDelay) {
      this.client = checkNotNull(client, "client");
      this.guestLoginDelay = guestLoginDelay;
      this.productPackageSupplier = checkNotNull(productPackageSupplier, "productPackageSupplier");
      checkArgument(guestLoginDelay > 500, "guestOrderDelay must be in milliseconds and greater than 500");
      this.loginDetailsTester = retry(virtualGuestHasLoginDetailsPresent, guestLoginDelay);
      this.cpuPattern = Pattern.compile(checkNotNull(cpuRegex, "cpuRegex"));
      this.prices = checkNotNull(prices, "prices");
      this.portSpeed = portSpeed;
      checkArgument(portSpeed > 0, "portSpeed must be greater than zero, often 10, 100, 1000, 10000");
      this.disk0Type = Pattern.compile(".*" + checkNotNull(disk0Type, "disk0Type") + ".*");
   }

   @Override
   public NodeAndInitialCredentials<VirtualGuest> createNodeWithGroupEncodedIntoName(String group, String name,
         Template template) {
      checkNotNull(template, "template was null");
      checkNotNull(template.getOptions(), "template options was null");
      checkArgument(template.getOptions().getClass().isAssignableFrom(SoftLayerTemplateOptions.class),
            "options class %s should have been assignable from SoftLayerTemplateOptions", template.getOptions()
                  .getClass());

      String domainName = template.getOptions().as(SoftLayerTemplateOptions.class).getDomainName();

      VirtualGuest newGuest = VirtualGuest.builder().domain(domainName).hostname(name).build();

      ProductOrder order = ProductOrder.builder().packageId(productPackageSupplier.get().getId())
            .location(template.getLocation().getId()).quantity(1).useHourlyPricing(true).prices(getPrices(template))
            .virtualGuests(newGuest).build();

      logger.debug(">> ordering new virtualGuest domain(%s) hostname(%s)", domainName, name);
      ProductOrderReceipt productOrderReceipt = client.getVirtualGuestClient().orderVirtualGuest(order);
      VirtualGuest result = get(productOrderReceipt.getOrderDetails().getVirtualGuests(), 0);
      logger.trace("<< virtualGuest(%s)", result.getId());

      logger.debug(">> awaiting login details for virtualGuest(%s)", result.getId());
      boolean orderInSystem = loginDetailsTester.apply(result);
      logger.trace("<< virtualGuest(%s) complete(%s)", result.getId(), orderInSystem);

      checkState(orderInSystem, "order for guest %s doesn't have login details within %sms", result,
            Long.toString(guestLoginDelay));
      result = client.getVirtualGuestClient().getVirtualGuest(result.getId());

      Password pw = get(result.getOperatingSystem().getPasswords(), 0);
      return new NodeAndInitialCredentials<VirtualGuest>(result, result.getId() + "", LoginCredentials.builder().user(pw.getUsername()).password(
            pw.getPassword()).build());
   }

   private Iterable<ProductItemPrice> getPrices(Template template) {
      Builder<ProductItemPrice> result = ImmutableSet.builder();

      int imageId = Integer.parseInt(template.getImage().getId());
      result.add(ProductItemPrice.builder().id(imageId).build());

      Iterable<String> hardwareIds = Splitter.on(",").split(template.getHardware().getId());
      for (String hardwareId : hardwareIds) {
         int id = Integer.parseInt(hardwareId);
         result.add(ProductItemPrice.builder().id(id).build());
      }
      ProductItem uplinkItem = find(productPackageSupplier.get().getItems(),
            and(capacity(portSpeed), categoryCode("port_speed")));
      result.add(get(uplinkItem.getPrices(), 0));
      result.addAll(prices);
      return result.build();
   }

   @Override
   public Iterable<Iterable<ProductItem>> listHardwareProfiles() {
      ProductPackage productPackage = productPackageSupplier.get();
      Set<ProductItem> items = productPackage.getItems();
      Builder<Iterable<ProductItem>> result = ImmutableSet.builder();
      for (ProductItem cpuItem : filter(items, matches(cpuPattern))) {
         for (ProductItem ramItem : filter(items, categoryCode("ram"))) {
            for (ProductItem sanItem : filter(items, and(matches(disk0Type), categoryCode("guest_disk0")))) {
               result.add(ImmutableSet.of(cpuItem, ramItem, sanItem));
            }
         }
      }
      return result.build();
   }

   @Override
   public Iterable<ProductItem> listImages() {
      return filter(productPackageSupplier.get().getItems(), categoryCode("os"));
   }
   
   // cheat until we have a getProductItem command
   @Override
   public ProductItem getImage(final String id) {
      return find(listImages(), new Predicate<ProductItem>(){

         @Override
         public boolean apply(ProductItem input) {
            return ProductItemToImage.imageId().apply(input).equals(id);
         }
         
      }, null);
   }
   
   @Override
   public Iterable<VirtualGuest> listNodes() {
      return filter(client.getVirtualGuestClient().listVirtualGuests(), new Predicate<VirtualGuest>() {

         @Override
         public boolean apply(VirtualGuest arg0) {
            boolean hasBillingItem = arg0.getBillingItemId() != -1;
            if (hasBillingItem)
               return true;
            logger.trace("guest invalid, as it has no billing item %s", arg0);
            return false;
         }

      });
   }

   @Override
   public Iterable<VirtualGuest> listNodesByIds(final Iterable<String> ids) {
      return filter(listNodes(), new Predicate<VirtualGuest>() {

            @Override
            public boolean apply(VirtualGuest server) {
               return contains(ids, server.getId());
            }
         });
   }

   @Override
   public Iterable<Datacenter> listLocations() {
      return productPackageSupplier.get().getDatacenters();
   }

   @Override
   public VirtualGuest getNode(String id) {
      long serverId = Long.parseLong(id);
      return client.getVirtualGuestClient().getVirtualGuest(serverId);
   }

   @Override
   public void destroyNode(String id) {
      VirtualGuest guest = getNode(id);
      if (guest == null)
         return;

      if (guest.getBillingItemId() == -1)
         throw new IllegalStateException(String.format("no billing item for guest(%s) so we cannot cancel the order",
               id));

      logger.debug(">> canceling service for guest(%s) billingItem(%s)", id, guest.getBillingItemId());
      client.getVirtualGuestClient().cancelService(guest.getBillingItemId());
   }

   @Override
   public void rebootNode(String id) {
      client.getVirtualGuestClient().rebootHardVirtualGuest(Long.parseLong(id));
   }

   @Override
   public void resumeNode(String id) {
      client.getVirtualGuestClient().resumeVirtualGuest(Long.parseLong(id));
   }

   @Override
   public void suspendNode(String id) {
      client.getVirtualGuestClient().pauseVirtualGuest(Long.parseLong(id));
   }

   public static class VirtualGuestHasLoginDetailsPresent implements Predicate<VirtualGuest> {
      private final SoftLayerClient client;

      @Inject
      public VirtualGuestHasLoginDetailsPresent(SoftLayerClient client) {
         this.client = checkNotNull(client, "client was null");
      }

      @Override
      public boolean apply(VirtualGuest guest) {
         checkNotNull(guest, "virtual guest was null");

         VirtualGuest newGuest = client.getVirtualGuestClient().getVirtualGuest(guest.getId());
         boolean hasBackendIp = newGuest.getPrimaryBackendIpAddress() != null;
         boolean hasPrimaryIp = newGuest.getPrimaryIpAddress() != null;
         boolean hasPasswords = newGuest.getOperatingSystem() != null
               && newGuest.getOperatingSystem().getPasswords().size() > 0;

         return hasBackendIp && hasPrimaryIp && hasPasswords;
      }
   }
}
