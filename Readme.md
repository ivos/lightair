# Light Air

>
> Test database applications declaratively
>

Light Air is a tool that supports **black-box system (integration) tests of database applications
for developers in a declarative style**.

It strives for intuitive usage and common-sense behavior.

Light Air removes the need to write code to setup database for tests,
you simply declare the required state of the database
and Light Air sets it up automatically.

Similarly, you declare the expected state of the database at the end of a test
and Light Air verifies that it matches the actual database state.

The removal of lengthy, tedious and error prone test code to setup and verify database
not only simplifies the tests, but as a result:

* encourages proper test design (e.g. separate setup for each test), and

* increases test coverage:

* it is extremely easy to verify more database columns, so more data gets actually verified,

* it is easy to write a new test, so more tests get written.

Go to [Light Air website](http://lightair.sourceforge.net/).
