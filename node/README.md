# Node instructions

Nodes connect to the Kafka cluster already mentioned in the server README.

Adjust the node configuration (config/node-configuration.yaml) accordingly:

`id` specifies the unique identifier of the node, matching the static list in the server configuration.

`bootstrapAddress` is the Kafka cluster's address the nodes connect to in order for communication.

`wireguardAddresss` is the IP address the nodes are available at, used for firmware transfer by the server.

`workingDirectory` is the directory the nodes store experiment data and logs to.

The server **needs** SSH access to the nodes, as firmware files are copies via SCP.
For this, add the server's public key to the `~/.ssh/authorized-keys` file.