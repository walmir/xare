# Getting started

First, download the modules core and sender from Github.

Navigate to `core` and build and package the module with the maven command `mvn package`. 
Then do the same for module `sender`.



Now that both modules are built and packaged into zip files (found under the folder `target`), you can use the command `vertx runzip` to start them.
Begin with `core`:
```vertx runzip target/core-<version>-mod.zip -conf <config-file> -cluster```

You can find the config file shown below under `src/test/resources/route.json`.

This would start a route with an incoming endpoint that listens on the address "address-0", and has a splitter that splits an array of
books to single items, then logs the title of each book.

Now, to send the JSON message shown below, with an array of two books, simply start the sender module with the following command 
```vertx runzip target/sender-<version>-mod.zip -cluster```


### JSON Message
```
{
    "someField": "someValue",
    "books": [
        {
            "title": "The Jungle Book",
            "author": "Rudyard Kipling"
        },
        {
            "title": "Demian",
            "author": "Hermann Hesse"
        }
    ]
}
```


### Route Configuration
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