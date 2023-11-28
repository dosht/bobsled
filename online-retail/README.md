# Online Retail Spark Job

## Download the data 

You need to download the data from https://archive-beta.ics.uci.edu/dataset/502/online+retail+ii and copy it into the
root directory of the project

## Run the job

You should run this with `sbt run` but to be safe, make sure to use the Java version 11

```shell
JAVA_HOME=<path/to/java> sbt run
```

## Run the tests

The tests here don't give full coverage, but I just provided examples of writing unit tests for Spark operations.
In real projects, I prefer to follow TDD (Test Driven Development) by writing the test first and implement what makes
the test pass, and then write more tests and so on. We should follow the same method when fixing bugs or modifying code. 
To run all tests:

```shell
JAVA_HOME=<path/to/java> sbt test
```

## Domain Driven Design (DDD)

Using domain driven design might seem over-engineering for this task, but that saves a lot of time and effort when the code evolves.
That will also allow us to use a single repo while keeping every feature clean and in its intuitive place.

## Using Git and GitHub

I put the whole task in a single git commit, but in real projects every feature should go to its own commit.

## CI/CD

Depending on the CI/CD technology you are using, you might need to add a file that describes how run tests and deploy the job.

## Examples of deployment options

 - Deploying to DataBricks can be done but uploading the jars and submitting the job to DataBricks cluster.
 - Deploying to Kubernetes will require defining Dockerfile and a pod template yaml file. 
