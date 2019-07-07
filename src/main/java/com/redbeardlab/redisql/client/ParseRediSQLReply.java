package com.redbeardlab.redisql.client;

import java.util.List;

public class ParseRediSQLReply {
    public boolean done_reply(List<Object> reply) {
        if (reply.size() != 2) { return false; }
        if ((reply.get(0) instanceof byte[]) && (new String((byte[])reply.get(0)).equals("DONE"))) {
            return (reply.get(1) instanceof Long);
        }
        return false;
    }

    public Long how_many_done(List<Object> reply) throws NoDoneReply {
        if (this.done_reply(reply) == false) {
            throw new NoDoneReply();
        }
        return (Long) reply.get(1);
    }

    public boolean is_integer(Object o) {
        return (o instanceof Long);
    }

    public Long get_integer(Object o) {
        return (Long) o;
    }

    public boolean is_string(Object o) {
        return (o instanceof byte[]);
    }

    public String get_string(Object o) {
        return new String((byte[])o);
    }

    public boolean is_list(Object o) {
        return (o instanceof List);
    }


}
