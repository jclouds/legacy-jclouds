To install deltacloud, do the following:
  * OS/X and jruby
    # brew install jruby
    # jruby -S gem --version 1.1.0 install rack 
    # jruby -S gem install deltacloud-core

To run a local deltacloud server, do the following:
  * jruby
    * jruby -S deltacloudd -i mock 
