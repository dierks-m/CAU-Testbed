# Server instructions
To launch the server and achieve functionality, you have to provide a running Kafka instance.

Once this is set up, adjust the server's configuration file in `config/sample-server-configuration.yaml`.

`kafkaAddress` will be the address of the kafka cluster, usually running on the same machine, as this is the address the nodes need to connect to.

`nodes` is a static list of nodes that are connected to the testbed.
This list serves to validate experiment descriptors provided by users to check if a node and the corresponding modules exist.

`numFirmwareDistributionThreads` and `numLogRetrievalThreads` determine the amount of parallel threads handling firmware distribution and log retrieval.
Note that, in order to utilize parallelization in the first place, you have to provide as many partitions as the number of threads for the topics `firmwareRetrieval` and `logRetrieval`, respectively.

`heartbeatInterval` specifies the timeout in which nodes need to send a heartbeat message to the server to stay 'alive' in the node status.
This time needs to match one one specified for the nodes (by default, 10 seconds)