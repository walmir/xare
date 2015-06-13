# User Manual

this is a work in progress...


## Endpoints

Routes communicate with the world and each other through endpoints. Once deployed, the route listens to messages received by its incoming-endpoint, and can pass messages on through one or more outgoing endpoints. \n

Currently only one type of Endpoints is implemneted, namely `direct-endpoint`.
This type of endpoints receives or sends messages directly from or to the Vert.x `EventBus`. \n

Route have a single incoming endpoint but can have multiple outgoing ones, if there are sub-routes.

## Enterprise Integration Patterns (EIPs)

A goal of this project is to implement as many of the Enterprise Integration Patterns from the book by Gregor Hohpe and Bobby Woolf as possible.
EIPs are implemented as `Nodes` that make up a route. \n
Currently the following EIPs are implemented.

*   Splitter: splits an array into single items and passes them on to the next node.
*   Filter: only passes packets on that fulfill a given predicate.

### Sub-route Nodes

Nodes like filter and splitter that pass only part of incoming packets on the next node in the route start a sub-route to process the packets they pass on. This way the main route is allowed to process all the complete packets if needed.

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