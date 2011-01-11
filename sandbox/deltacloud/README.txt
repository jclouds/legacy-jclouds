jclouds binding to deltacloud requires a minimum server version of 0.1.0.

The identity and credential specified in jclouds will pass through deltacloud to 
the backend, such as gogrid.

To install deltacloud, do the following:
  * OS/X and jruby
    # use homebrew or equiv to install jruby
      * brew install jruby
    # install and configure openssl to avoid "certificate verify failed" errors
      # install and link openssl
        * brew install openssl
        * brew link openssl
      # install jruby ssl
        * jruby -S gem install jruby-openssl
      # setup cert directory
        * mkdir $HOME/certs
        * curl -o $HOME/certs/cacert.pem http://curl.haxx.se/ca/cacert.pem
      # rehash cert directory
        * export SSL_CERT_DIR=$HOME/certs
        * export SSL_CERT_FILE=$HOME/certs/cacert.pem
        * c_rehash
      # test
        * jruby -ropen-uri -e 'p open("https://encrypted.google.com")'
          * should see something like #<StringIO:0x5330cb4b>
    # install specific version of rack that doesn't conflict with deltacloud
      * jruby -S gem --version 1.1.0 install rack
    # install deltacloud core
      * jruby -S gem install deltacloud-core

To run a local deltacloud server, do the following:
  * jruby
    # export SSL_CERT_DIR=$HOME/certs
    # export SSL_CERT_FILE=$HOME/certs/cacert.pem
    # jruby -S deltacloudd -i mock 

Here are some notes about specific cloud providers
  * terremark
    # install fog gem
      * jruby -S gem install fog
  * rackspace
    # install cloudfiles gem
      * jruby -S gem install cloudfiles
  * ec2
    # install amazon-ec2 gem
      * jruby -S gem install amazon-ec2


Local Development of Delta
   * jruby -S gem install rack-test cucumber

  * What tests are available
    * jruby -S rake -T
      * server/tests/drivers
        * specific tests to a particular driver
  * Running tests
    * jruby -S rake test

