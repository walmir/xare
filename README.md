# Xare

Xare is an open-source integration framework built on top of vert.x. It allows you to mediate and route messages through chains of nodes that implement different Enterprise Integration Patterns (EIPs) and (soon) connect with thirdparty applications and platforms. 
Currently the framework is in the very early stages with few EIPs implemented.

Xare routes are deployed as Vert.x worker verticals, allowing the lifecycle of a single route to be managed independently of all others. Xare routes can therefore be started, stopped and undeployed on the fly, without affecting the rest of the system. 


## Read more:
* [Getting started](docs/getting_started.md)
* [Manual](docs/manual.md)
* [Examples](docs/examples.md)
