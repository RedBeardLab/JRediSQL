package com.redbeardlab.redisql.client;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Client;
import redis.clients.jedis.Jedis;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RediSQLClientTest {

    ParseRediSQLReply parser = new ParseRediSQLReply();

    @BeforeEach
    public void createSimpleDatabase() {
        RediSQLClient r = new RediSQLClient();
        r.create_db("DB");
    }

    @AfterEach
    public void deleteSimpleDatabase() {
        RediSQLClient r = new RediSQLClient();
        r.del("DB");
    }

    @Test
    public void creatingDatabaseWorks() {
        RediSQLClient r = new RediSQLClient();
        String result = r.create_db("TestDB");
        assertEquals("OK", result);
        r.del("TestDB");
    }

    @Test
    public void creatingATable() {
        RediSQLClient r = new RediSQLClient();
        List<Object> reply = r.exec("DB", "create table foo(a int, b string);");
        assertTrue(parser.done_reply(reply));
        Long done = null;
        try {
            done = parser.how_many_done(reply);
        } catch (NoDoneReply noDoneReply) {
            noDoneReply.printStackTrace();
        }
        assertEquals(0, done.intValue());
    }

    @Test
    public void insertDataIntoATable() {
        RediSQLClient r = new RediSQLClient();
        r.exec("DB", "CREATE TABLE foo(a int, b string);");
        List<Object> reply = r.exec("DB", "INSERT INTO foo VALUES(1, 'aaa');");
        assertTrue(parser.done_reply(reply));
        try {
            assertEquals((long) 1, (long)parser.how_many_done(reply));
        } catch (NoDoneReply noDoneReply) {
            noDoneReply.printStackTrace();
        }
    }

    @Test
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

    @Test
    public void withoutRediSQLClientClass() {
        Jedis j = new Jedis();
        Client r = j.getClient();
        r.sendCommand(RediSQLCommand.ModuleCommand.CREATE_DB, "NEW_DB");
        String create_reply = r.getBulkReply();
        assertEquals("OK", create_reply);

        r.sendCommand(RediSQLCommand.ModuleCommand.EXEC, "NEW_DB", "SELECT 1, 2, 41+1;");
        List<Object> select_reply = r.getObjectMultiBulkReply();

        // just one row
        assertEquals(1, select_reply.size());

        List<Object> firstRow = (List)select_reply.get(0);
        // two columns
        assertEquals(3, firstRow.size());

        assertEquals(Long.valueOf(1), (Long)firstRow.get(0));
        assertEquals(Long.valueOf(2), (Long)firstRow.get(1));
        assertEquals(Long.valueOf(42), (Long)firstRow.get(2));

        j.del("NEW_DB");

    }

}
