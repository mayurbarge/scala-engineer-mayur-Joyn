# Tech Task

## Introduction

Hello,

In this coding exercise you will be addressing an issue/task that is central to our day to day work at Joyn - writing and maintaining data ingestion and transforming services. This exercise aims to test your ability to write services that efficiently solve problems we have in our business.

**Please read this document carefully before starting, as it outlines your task, the constraints and by what criteria you will be evaluated.**

Use git to document your work. When finished, please create a pull/merge request against the `master` branch. We expect the source branch to have following naming convention: `solution/<your name>`. While raising the pull request, please bear in mind that your branch would be **squashed** into `master`.

We will look at the git history of your pull request to determine the way you approached this and the time you roughly spent on the task.  **Please do not squash your commits**.

You are free to chose from following two exercises:
1. **Writing a HTTP endpoint service**
2. **Writing a stream processor**

**You need to implement ONLY ONE of the two exercises. DO NOT address both. If you do, we will flip a coin to decide which one to review.**

## Background

While a user is watching some content, we periodically send heartbeat events to the so-called _collector_ service via HTTP. The _collector_ stores those _heartbeats_ in a topic of a messaging queue. A _heartbeat_ event contains information about the user, current content playing, content duration, and the current position/progress of the playback. 

#### Example of the _heartbeat_ event:

```json
{
  "userId": "user_987",
  "movieId": "movie_214",
  "duration": 3097,
  "position": 3000,
  "timestamp": "2021-10-26T13:27:01+0000"
}
```

In words, this means that a user with id `user_987` is watching a movie with id `movie_214` . The movie has 3097 seconds total length and the user is currently on 3000th second of the content (that is, near the end). The event happened at `2021-10-26T13:27:01+0000`
Or another example of the _heartbeat_:

```json
{
  "userId": "user_987",
  "movieId": "movie_007",
  "duration": 1200,
  "position": 250,
  "timestamp": "2021-10-26T13:27:31+0000"
}
```

This second message is from same user `user_987` but watching different movie `movie_007`, where the user is on position `250` seconds of total movie length 1200 seconds. The event happened at `2021-10-26T13:27:31+0000`

### [Option 1] The HTTP endpoint

For this exercise you would be implementing a simple HTTP API endpoint application. Your solution must be implemented in either Scala, Java, Kotlin or Python. The framework you want to use is your choice, we are happy to see a solution in any of Play!, Spring, Django or others.

The goal is to implement a *Collector API Service*. Assume every user that is watching a movie runs their own HTTP client. Every 10 seconds, such a client sends the updated position of the user inside the playback of the movie. The service you are writing should do the following:

- Service starts and opens a listening socket on TCP/8080 or other suitable port to receive HTTP traffic
- Service accepts HTTP POST requests on `/heartbeat` path with `application/json` payload described earlier as [_heartbeat_](#example-of-the-heartbeat-event)
- Service 
  - Option 1a: sends the _heartbeat_ event to a topic in Kafka / PubSub / Kinesis and acknowledges the client with HTTP response
  - Option 1b: stores the _heartbeat_ event to a table in relational database and acknowledges the client with HTTP response

You may want to implement either Option 1a or Option 1b depending on which you find a better fit for you or harder challenge or more interesting.

### [Option 2] The Stream Processor

For this exercise you would be implementing a simple streaming application. Your solution must be implemented in either Scala, Java, Kotlin or Python. The framework you want to use is your choice, we are happy to see a solution in any of Beam, Flink, KStreams, Spark Structured Streaming or others.

The task is to implement a _Resume Position Service_. Assume that for every distinct user watching a movie, every 10 seconds a _heartbeat_ is written into a topic in the messaging queue. Each _heartbeat_ contains the updated position in the movie. The _Resume Position Service_ should do the following:

- Service reads data from a topic in the messaging queue (the technology to use is your choice, for example Kafka, PubSub, Kinesis)

- Using 1 minute tumbling window it computes for each user the last observed movie position for each movie they were watching during that 1 minute window

- Outputs to stdout position information for each user with all movies the user was watching during the 1 minute window in form:
  Example of message for user `user_987` after the `2021-10-26T13:20:00+0000` time mark has passed:

  ```json
  [
    {
      "userId": "user_987",
      "movieId": "movie_214",
      "position": 3000,
      "timestamp": "2021-10-26T13:27:01+0000"
    },
    {
      "userId": "user_987",
      "movieId": "movie_007",
      "position": 250,
      "timestamp": "2021-10-26T13:27:31+0000"
    }
  ]
  ```

## Constraints

- Do not introduce any external dependencies (remote cloud databases, remote search engines, etc.) to solve the task. The task is about your problem solving skills and not about creating a cloud connected system.

- You shall use any technology which is available without licensing limitation (open-source is preferred), that is, the reviewer can legally and for free obtain all the software needed to run your solution, preferably using simple commands from readme

- You can expect us to have Scala, SBT, Java, Maven, gradle, ,Python, Node, gcc, docker, etc. installed. It should not require more than, **for example**, `pip install && python <your_script>.py`, or `npm install && npm run`, or `docker run` , or `java -jar` or `bash start.sh` to have a running service. 

- **Time limit:** 7 days after being added to the project, your push right will be revoked and the latest commit of your implementation will be the basis for your evaluation.

- Keep it simple. You are not expected to spend days on this - just prove that you know how to write great piece of software.

- Choose only one option to submit:

  - Option 1a - REST API with store into message queue
  - Option 1b - REST with store into database
  - Option 2 - Streaming processing

  All of them will be evaluated the same. The option chosen is not relevant for evaluation, all of them have the same _value_ for us. Choose whichever you find interesting, suitable, challenging or most comfortable with. 

## Evaluation criteria

In general you can think of the evaluation being a thorough peer review of your code. 
You will be evaluated by a number of criteria, among others:

- How well did you apply engineering best practices (general & language specific)?
- Is the service/processor working as intended?
- How readable is your code?
- Does the implementation solve the given problem
    - correctly?
    - efficiently?
- Is your code consistent in itself (styling, language constructs, naming)?
- Appropriate use of 3rd party modules
- We do not expect you to have a high test coverage, but it is important that you demonstrate that you know how to write testable code and provide a few tests that showcase this.
- Proper use of git
- Making good assumptions and documenting them


### Good luck!!
