In this module, you can run just unit tests, or run tests that connect directly to the service.  To run against the service, you'll need to specify the maven profile live.
When live is enabled, any tests that have "LiveTest" suffix will be run during the integration-test phase.  In order for this to operate, you must specify the following 
properties:
   * jclouds.test.account
   * jclouds.test.key

Note that this module is intentionally incomplete.  You should global replace and create your own client from this example.  Make sure that you use tests propertly.  For
example, the test ending in *AsyncClientTest will help ensure that your annotations parse in the way you expect.
