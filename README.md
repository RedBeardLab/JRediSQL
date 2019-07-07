# JRediSQL

Java client for RediSQL, the fastest, simplest, in-memory database.

JRediSQL is a Java package that implements a basic client for [RediSQL][redisql].

The client is necessary, because the standard Java Client for Redis is not friendly towards Redis modules.


## Usage

The more complete way to know how to use the library is to look into the tests.

Below one of the biggest test in the test suite to give an idea of the look and feel of the library.

```java
public void getDataFromTable() {
    RediSQLClient r = new RediSQLClient();
    r.exec("DB", "CREATE TABLE foo(a int, b string);");
    r.exec("DB", "INSERT INTO foo VALUES(1, 'aaa'), (2, 'bbb');");
    r.exec("DB", "INSERT INTO foo VALUES(3, NULL);");
    List<Object> reply = r.exec("DB", "SELECT * from foo");

    assertFalse(parser.done_reply(reply));
    assertTrue(reply.get(0) instanceof  List);

    List<Object> firstRow = (List)reply.get(0);
    assertEquals( 1, (long)(Long) firstRow.get(0));
    assertEquals("aaa", (String)parser.get_string(firstRow.get(1)));

    List<Object> secondRow = (List)reply.get(1);
    assertEquals(2, (long)parser.get_integer(secondRow.get(0)));
    assertEquals("bbb", (String)parser.get_string(secondRow.get(1)));

    List<Object> thirdRow = (List)reply.get(2);
    assertEquals(3, (long)parser.get_integer(thirdRow.get(0)));
    assertNull(thirdRow.get(1));
}
```

[redisql]: https://redisql.com 