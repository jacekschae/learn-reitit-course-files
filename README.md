![alt learn-reitit-logo](https://res.cloudinary.com/schae/image/upload/f_auto,q_80,r_12/v1601822305/learnreitit.com/1200x640-learn-reitit.png)

# [LearnReitit.com](https://www.learnreitit.com)

Learn how to build robust REST API with Clojure by composing libraries including Reitit, Ring, Integrant, and next.jdbc. The service exposes Recipes, Conversations, and Accounts, everything documented with Swagger. Our REST API service exposes multiple endpoints with JWT for Auth—backed by Auth0. Serving JSON, Transit (msgpack, json), or EDN from PostgreSQL, and is hosted on Heroku.

## Course files

The code in this repo includes one folder - `increments` - code for the start of each video (if you get lost somewhere along the way just copy the content of the video you are starting and continue).

### Clone

```shell
$ git clone git@github.com:jacekschae/learn-reitit-course-files.git

$ cd learn-reitit-course-files/increments/<step-you-want-to-check-out>
```

### Install

Most probably your editor will take care of dependencies and if you want to do it on the command line run:

```shell
lein deps
```

### Run REPL

Probably you will run your REPL from your editor, and thre is nothing stopping you to run it from the command line:

```shell
lein repl
```

## License

Copyright © 2020 Jacek Schae
