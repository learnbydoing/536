DeathWatch Application
====================

| README For DeathWatch |
---
| Project for CSC 536: Distributed Systems 2 |
| Professor Ljubomir Perkovic |
| By Student Kevin Westropp |

There are three main directories:

1. common - code compiled into jar to use as common between server and watcher

2. server - Server to host children of watchers/supervisors

3. watcher - client type application which creates a child on the server to watch and forward messages


To Compile
---

- navigate to root of both /watcher and /server

			$ sbt compile


To Run
---

- navigate to root of both /watcher and /server (start server first then watchers)

			$ sbt run

Interaction
---

	1. Wait for print out of messages/communication and deathwatch, then type yes when finished

	2. Interface works in loop, type yes to exit


Stopping
---

- type yes to exit/shutdown


