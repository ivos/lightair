---
title: Setup database
author:
  - Ivo Maixner
date: 2011-09-26
---

# Set up database

Set up the database content before each test to make tests repeatable.

Use separate and specific setup dataset for each test - set up exactly the data required by the test.\
Avoid "kitchen-sink" single shared setup for all the tests -
it quickly gets unmaintained as it is not obvious
which data is used by which test (and which data is NOT used anymore)
and becomes a major roadblock on tests refactoring.

Rows are inserted into the database in the order specified in the dataset.
Tables are deleted before that in the reverse order.

**Default @Setup annotation**\
When a test method or a test class is annotated with `@Setup`,
the following files are located sequentially in the package of the class.
The first file found wins:

- `<test class name>.<test method name>.xml`
- `<test method name>.xml`
- `<test class name>.xml`

Then a **"clean insert"** database operation is performed with the dataset located.

_Example:_

```java
@Setup
public class MyTest {
  @Test
  public void test() {
  }
}
```

**Specify dataset file names in @Setup**\
When the `@Setup` annotation contains **file names** of datasets, they are used
to locate the datasets in the package of the test class,
and the "clean insert" database operation is performed with all such datasets located.

_Example:_

```java
@Setup({ "setup-1.xml", "setup-2.xml" })
public class MyTest {
  @Test
  public void test() {
  }
}
```

**Specify profile in @Setup**\
Use `profile` attribute of `@Setup` annotation to instruct Light Air to
apply the datasets on the **connection defined by the profile**.\
See the Profiles section in [Configure Light Air](configure.html) for details
on how to set up profiles.

_Example:_

```java
@Setup(value = { "setup-1.xml", "setup-2.xml" }, profile = "oracle")
public class MyTest {
  @Test
  public void test() {
  }
}
```

**Specify multiple @Setup annotations**\
Use `@Setup.List` annotation to define **multiple** `@Setup` **annotations**
for the same test class or test method.

_Example:_

```java
@Setup.List({
  @Setup("setup-h2.xml"),
  @Setup(value = "setup-hsql.xml", profile = "hsql"),
  @Setup(value = { "setup-ora-1.xml", "setup-ora-2.xml" }, profile = "oracle") })
public class MyTest {
  @Test
  public void test() {
  }
}
```

**Empty row**\
Use "empty row", i.e. element with no attributes, to clean the table without inserting any rows.

_Example:_

```xml
<dataset>
  <my_table />
</dataset>
```

**Token @null - null value**\
Use `@null` to set up a column to **null
value** explicitly. (By omitting the column on the row, you can also set it up to null value implicitly,
unless there is a default value defined at the database level.)

**Token @date - current date**\
Use `@date` to set up column to **current date** midnight value.\
The current date value includes `yyyy-MM-dd`
as in _2012-12-31_.\
The time part is set to midnight (00:00:00.000).

**Token @time - current time**\
Use `@time` to set up column to **current time** value.\
The current time value includes `HH:mm:ss` as in _23:59:58_.\
The date part is set to 1970-01-01.
The milliseconds are set to 0 (zero).

**Token @timestamp - current timestamp**\
Use `@timestamp` to set up column to **current timestamp** value.\
The current timestamp value includes `yyyy-MM-ddTHH:mm:ss.SSS`
as in _2012-12-31T23:59:58.123_.

**Modify the temporal tokens with a duration**\
You can modify the temporal tokens `@date`, `@time` and `@timestamp` by
appending a sign (+ or -) and a
[duration in ISO 8601 format](http://en.wikipedia.org/wiki/ISO_8601#Durations).
This will move the time instant represented by the temporal token
in the direction of the sing by the duration specified.

_Examples:_

- The bare token `@date` represents the last midnight.
- This `@date+P1D` represents the first following midnight.
- This `@date-P1M` represents the midnight one month before the last one.
- This `@date+PT12H` represents noon today.
- This `@timestamp+PT1H` represents one hour in the future.
- This `@timestamp-P2Y3M4DT5H6M7S` represents
  2 years, 3 months, 4 days, 5 hours, 6 minutes and 7 seconds in the past.

**Token @auto - generate unique value**\
Use `@auto` as a column value to have the actual value **generated automatically**.\
The value is guaranteed to be **unique**, with the obvious exception of boolean columns.

_Example:_

```xml
<dataset>
  <user id="@auto" email="@auto" name="@auto" password="@auto" version="@auto" />
  <user id="@auto" email="@auto" name="@auto" password="@auto" version="@auto" />
  <user id="@auto" email="@auto" name="@auto" password="@auto" version="@auto" />
</dataset>
```

<p>
<a href="verify.html" class="btn" style="float: right;">
Next: Verify database
</a>
</p>
