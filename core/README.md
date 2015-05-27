# Xare

Xare is an integration framework build on top of vert.x. Currently, it is in the very early stages with only very few EIPs implemented.

Xare allows for routes to be deployed as Vert.x verticals, thus running independently of one another.


## Endpoints

Routes communicate with the world through endpoints. Once deployed, the route listens to messages received by its incoming-endpoint.

Currently only one type of Endpoints is implemneted, namely `direct-endpoint`.
This type of endpoints receives or sends messages directly from or to the Vert.x `EventBus`. 

## Enterprise Integration Patters (EIPs)

A goal of this project is to implement as many of the Enterprise Integration Patterns from the book by Gregor Hohpe and Bobby Woolf as possible.
Currently, though only the Splitter EIP is implemented.

## Route Configuration

Route Configuration can be done either programmatically or using JSON. The following is an example of a route containing a splitter node and two logger nodes.
The route starts with a direct endpoint is defined that listens on the EvventBus on the address `address-0`.
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
            "expression":"$..title"
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

## Selector

A selector can be used by route nodes to select the part of a packet that is to be processed by the node. A selector can be configured by setting an expression language (currently only JsonPath is supported) and a selection expression.
In the example above, the splitter attempts to select a books list, while the logger node the splitters sub-route selects the book title to be logged out.


## Next Steps:

*   HTTP-Endpoint
*   Message Router EIP
*	and more... 
