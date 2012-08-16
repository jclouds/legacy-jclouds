---
layout: jclouds
title: Using jclouds to manage VMWare vCloud Director 1.5
---
#Using jclouds to manage VMWare vCloud Director 1.5

## Introduction

jclouds currently supports most of the VMware vCloud Director API.
We also provide tools that make developing to it easier.

## Obtaining a context to vCloud Director
TBD

* Getting a context to any portable version 1.0 vCloud:
TBD

## Access all the available clients inside the 
{% highlight text %}
RestContext<VCloudDirectorApi, VCloudDirectorAsyncApi> context;

catalogApi = context.getApi().getCatalogApi();
queryApi = context.getApi().getQueryApi();
vAppApi = context.getApi().getVAppApi();
vAppTemplateApi = context.getApi().getVAppTemplateApi();
vdcApi = context.getApi().getVdcApi();
vmApi = context.getApi().getVmApi();
networkApi = context.getApi().getNetworkApi();
{% endhighlight %}

Each one of them allows you to access the API functionalities of any vCloud Director subsytem.

## Portability 
The actual implementation assumes that some vAppTemplates are already available in this vCD instance.
Starting from a vApp template, in fact, is possible to compose a vApp

{% highlight java %}
VApp composedVApp = vdcApi.composeVApp(vdcURI, ComposeVAppParams.builder()
  .name(name("vApp-"))
  .instantiationParams(instantiationParams())
  .build());
{% endhighlight %}

where instantiationParams() are in charge to create a 'vAppNetwork' attached to the orgNetwork 'networkURI':

{% highlight java %}
   /** Build a {@link NetworkConfigSection} object. */
   private NetworkConfigSection networkConfigSection() {
      NetworkConfigSection networkConfigSection = NetworkConfigSection.builder()
            .info("Configuration parameters for logical networks")
            .networkConfigs(
                  ImmutableSet.of(
                        VAppNetworkConfiguration.builder()
                              .networkName("vAppNetwork")
                              .configuration(networkConfiguration())
                              .build()))
            .build();

      return networkConfigSection;
   }

   /** Build a {@link NetworkConfiguration} object. */
   private NetworkConfiguration networkConfiguration() {
      Vdc vdc = context.getApi().getVdcApi().getVdc(vdcURI);
      Set<Reference> networks = vdc.getAvailableNetworks();
      // Look up the network in the Vdc with the id specified by the user
      Optional<Reference> parentNetwork = Iterables.tryFind(networks, new Predicate<Reference>() {
         @Override
         public boolean apply(Reference reference) {
            return reference.getHref().equals(networkURI);
         }
      });

      // Check we actually found a network reference
      if (!parentNetwork.isPresent()) {
         fail(String.format("Could not find network %s in vdc", networkURI.toASCIIString()));
      }

      // Build the configuration object
      NetworkConfiguration networkConfiguration = NetworkConfiguration.builder()
            .parentNetwork(parentNetwork.get())
            .fenceMode(Network.FenceMode.BRIDGED)
            .build();

      return networkConfiguration;
   }
{% endhighlight %}

Or you can always recompose an existing vApp

{% highlight java %}
vAppApi.recompose(vApp.getHref(), params);
{% endhighlight %}

where 'params' are RecomposeVAppParams.