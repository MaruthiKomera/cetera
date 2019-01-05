# m-svc #

micro-service api

## dev environment setup - mac ##

1. Install [jdk 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
2. Install [vagrant](https://www.vagrantup.com/)
3. Download Oracle 11G XE & Ojdbc6.jar from [Oracle](http://www.oracle.com)
4. You can download Oracle developer tools to manage table schema and data

## running on mac laptop ##

In your terminal (preferably using [iTerm 2](https://www.iterm2.com/))

    $ git clone https://srohatgi@bitbucket.org/ramganesan/cetera.git; cd cetera
    $ git submodule init
    $ git submodule update
    $ cd env; setup.sh pathtoyourfile/oracle-xe-11.2.0-1.0.x86_64.rpm.zip ojdbc6.jar
    $ cd ora; vagrant up
    get latest schema in libs folder, and import schema to oracle db
    $ cd ../../
    $ ./gradlew bRun

Navigate to [http://localhost:8080/api/people](http://localhost:8080/api/people) in your browser
