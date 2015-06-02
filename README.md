# Xare

Xare is an integration framework build on top of vert.x. Currently, it is in the very early stages with only very few EIPs implemented.

Xare allows routes to be deployed as Vert.x worker verticals, thus running as micro-services, completely independently of one another. The key advantage is that routes can be added, removed and edited on the fly, without downtime and without affecting the rest of the system.


## Endpoints

Routes communicate with the world and each other through endpoints. Once deployed, the route listens to messages received by its incoming-endpoint, and can pass messages on through one or more outgoing endpoints.

Currently only one type of Endpoints is implemneted, namely `direct-endpoint`.
This type of endpoints receives or sends messages directly from or to the Vert.x `EventBus`. 

## Enterprise Integration Patterns (EIPs)

A goal of this project is to implement as many of the Enterprise Integration Patterns from the book by Gregor Hohpe and Bobby Woolf as possible.
Currently the following EIPs are implemented.

*   Splitter: splits an array into single items and passes them on to the next node.
*   Filter: only passes packets on that fulfill a given predicate.


### Next EIPs:

* Choice (Message Router)
* Aggregator


## Selectors

A selector can be used by route nodes to select the part of a packet that is to be processed by the node. A selector can be configured by setting an expression language (currently only JsonPath is supported) and a selection expression.
In the example below, the splitter attempts to select a books list, while the logger node the splitters sub-route selects the book title to be logged out.

## Route Configuration

Route Configuration can be done either programmatically or using JSON. The following is an example of a route containing a splitter node and two logger nodes.
The route starts with a direct endpoint that listens on the EventBus on the address `address-0`.
Outgoing messages from the route are sent out on the EventBus to the addresses `route-output` and `sub-route-output`.


```
{  
  "name":"the-route",
  "from":{  
    "type":"endpoint",
    "address":"address-0",
    "direction":"incoming",
    "endpointType":"direct"
  },
  "nodes":[  
    {  
      "type":"logger",
      "loglevel":"info"
    },
    {  
      "type":"splitter",
      "selector":{  
        "expression-language":"jsonPath",
        "segment":"body",
        "expression":"$.books"
      },
      "nodes":[  
        {  
          "type":"logger",
          "loglevel":"info",
          "selector":{  
            "expression-language":"jsonPath",
            "expression":"$.title"
          }
        },
        {  
          "type":"endpoint",
          "address":"sub-route-output",
          "direction":"outgoing",
          "endpointType":"direct"
        }
      ]
    },
    {  
      "type":"endpoint",
      "address":"route-output",
      "direction":"outgoing",
      "endpointType":"direct"
    }
  ]
}
```

## Route Deployment

Deploying a route programmatically can be done with the following call:
```
container.deployWorkerVerticle(DefaultRoute.class.getName(), routeConfig);
```


## Next Steps:

*   HTTP-Endpoint
*   Message Router EIP
*	and more... 