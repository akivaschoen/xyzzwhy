# xyzzwhy

A Clojure-powered Twitter bot who goes on random adventures through a dreamlike world. 

## Installation

1. Clone the repository
2. `lein uberjar`
3. Either `lein run` or `java -jar target/uberjar/xyzzwhy-2.0.0-standalone.jar`

You'll need to use Twitter's developer's tools to get an app going. This is done [here](https://apps.twitter.com) and is beyond the scope of this document.

## Current Status

xyzzwhy has been going strong on Twitter since November 2014. You can get a taste of what he's currently capable of [here](https://twitter.com/xyzzwhy). 

### Ongoing

As of 2.0, all of the basic functionality that was established in 0.7.0 remains the same only written with far better Clojure code (yeesh). Now it's time for the data to take the wheel and dictate what I need to do with the code to add some more depth to xyzzwhy's adventures. 

My main idea is to occasionally have arcs where there are multiple tweets that continue a single 'story' which would consist of some state so xyzzwhy knows where he is, what he's carrying, how many hit points he has, whatever. This lays the ground work to be able to have custom stories that are randomly determined. For example, xyzzwhy at a Halloween carnival. Let that play out for a limited time and then switch back to the standard corpus. 

We'll see.

## License

Copyright © 2014-15 Akiva R. Schoen

Distributed under the MIT License (see LICENSE for details).
