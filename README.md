akka-codepot-workshop
=====================
codepot workshop combining Akka Cluster with streaming and various patterns.

TODO:
- [ ] akka-http and ask into worker which scans entire thing
- [ ] akka-http and ask into worker with separate dispatcher
- [ ] akka-http and ask into master, who has workers (remote)
- [ ] akka-http and ask into master, who has workers (remote), who fail
- [ ] akka-http and ask into master, who has workers (remote), who fail, so we use backup-requests
- [ ] akka-http and ask into cluster sharding
- [ ] akka-http and ask into cluster sharding, with LRU cache (compose actors)
- [ ] akka-http streaming response from workers as soon as they come back (no Results(...) message, just Result())

License
=======

Apache 2.0