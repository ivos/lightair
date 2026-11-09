---
title: Programmatic API
author:
  - Ivo Maixner
date: 2016-09-19
---

# Programmatic API

Light Air provides programmatic API with the
[net.sf.lightair.LightAirApi class](https://lightair.sourceforge.net/apidocs/net/sf/lightair/LightAirApi.html).

The general contract is to
[initialize](https://lightair.sourceforge.net/apidocs/net/sf/lightair/LightAirApi.html#initialize(java.lang.String))
and
[shutdown](https://lightair.sourceforge.net/apidocs/net/sf/lightair/LightAirApi.html#shutdown())
and in between perform either
[generateXsd](https://lightair.sourceforge.net/apidocs/net/sf/lightair/LightAirApi.html#generateXsd())
call or (a series of)
[setup](https://lightair.sourceforge.net/apidocs/net/sf/lightair/LightAirApi.html#setup(java.util.Map))
and
[verify](https://lightair.sourceforge.net/apidocs/net/sf/lightair/LightAirApi.html#verify(java.util.Map))
or
[await](https://lightair.sourceforge.net/apidocs/net/sf/lightair/LightAirApi.html#await(java.util.Map))
calls.

To generate XSD files:

```java
LightAirApi.initialize(LightAirApi.getPropertiesFileName());
LightAirApi.generateXsd();
LightAirApi.shutdown();
```

To set up and verify the database:

```java
Map<String, List<String>> fileNames = new HashMap<>();
fileNames.put(Keywords.DEFAULT_PROFILE, List.of("my-dataset-1.xml", "my-dataset-2.xml"));

LightAirApi.initialize(LightAirApi.getPropertiesFileName());
LightAirApi.setup(fileNames);
// ...execute function under test...
LightAirApi.verify(fileNames);
// ...or for asynchronous functionality:
LightAirApi.await(fileNames);
// ...possibly repeat setup and verify / await for other tests...
LightAirApi.shutdown();
```

<p>
<a href="../database-support.html" class="btn" style="float: right;">
Next: Database support
</a>
</p>
