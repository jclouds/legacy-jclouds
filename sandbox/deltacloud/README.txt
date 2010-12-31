jclouds binding to deltacloud requires a minimum server version of 0.1.0.

To install deltacloud, do the following:
  * OS/X and jruby
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
    # use homebrew or equiv to install jruby
      * brew install jruby
    # install specific version of rack that doesn't conflict with deltacloud
      * jruby -S gem --version 1.1.0 install rack
    # install deltacloud core
      * jruby -S gem install deltacloud-core

To run a local deltacloud server, do the following:
  * jruby
    # export SSL_CERT_DIR=$HOME/certs
    # export SSL_CERT_FILE=$HOME/certs/cacert.pem
    # jruby -S deltacloudd -i mock 

The identity and credential specified in jclouds will passthrough deltacloud to 
the backend, such as gogrid.

