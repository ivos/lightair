---
title: Write a test
author:
  - Ivo Maixner
date: 2012-01-05
---

# Write a test

The following example tests updating a user's profile in an example application:

```java
package it.user;

import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Setup;
import net.sf.lightair.annotation.Verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(LightAir.class)
@Setup
@Verify
public class ProfileUpdateIT {

  @Test
  public void test() {
    // invoke the actual update of the user's profile
    // (call a REST API, call a SOAP web service, or trigger the UI).
  }
}
```

With TestNG, use the Listeners annotation instead of the ExtendWith annotation:

```java
import org.testng.annotations.Listeners;

@Listeners(LightAirNGListener.class)
@Setup
@Verify
public class ProfileUpdateIT {
...
```

Next to the class (in the same package), create `ProfileUpdateIT.xml` file
to set up the database before the test:

```xml
<?xml version='1.0' encoding='UTF-8'?>
<dataset xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:noNamespaceSchemaLocation="../../dataset.xsd">
  <user id="1" version="1" email="email1" name="Name 1" password="pwd1"/>
</dataset>
```

Finally, create `ProfileUpdateIT-verify.xml` file to verify the database
was properly updated:

```xml
<?xml version='1.0' encoding='UTF-8'?>
<dataset xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:noNamespaceSchemaLocation="../../dataset.xsd">
  <user id="1" version="2" email="email1" name="Name 1 updated" password="pwd1" />
</dataset>
```

<p>
<a href="features/setup.html" class="btn" style="float: right;">
Next: Features
</a>
</p>
