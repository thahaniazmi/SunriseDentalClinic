package com.dental.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class JsonUtilTest {

    @Test
    public void parsesSimpleObject() {
        Object result = JsonUtil.parse("{\"name\":\"Kasun\",\"age\":30}");
        Map<String, Object> map = JsonUtil.asMap(result);
        assertEquals("Kasun", JsonUtil.asString(map.get("name")));
        assertEquals(30.0, JsonUtil.asDouble(map.get("age")));
    }

    @Test
    public void parsesNestedArray() {
        Object result = JsonUtil.parse("{\"items\":[{\"cost\":1500.0}]}");
        Map<String, Object> map = JsonUtil.asMap(result);
        List<Object> items = JsonUtil.asList(map.get("items"));
        assertEquals(1, items.size());
        assertEquals(1500.0, JsonUtil.asDouble(JsonUtil.asMap(items.get(0)).get("cost")));
    }

    @Test
    public void parsesNullAndBoolean() {
        Map<String, Object> map = JsonUtil.asMap(JsonUtil.parse("{\"a\":null,\"b\":true,\"c\":false}"));
        assertNull(map.get("a"));
        assertEquals(Boolean.TRUE, map.get("b"));
        assertEquals(Boolean.FALSE, map.get("c"));
    }

    @Test
    public void invalidJsonThrows() {
        assertThrows(Exception.class, () -> JsonUtil.parse("{not json"));
        assertThrows(Exception.class, () -> JsonUtil.parse(""));
    }

    @Test
    public void toJsonRoundTrip() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", "A \"quoted\" name");
        map.put("fee", 500.0);
        String json = JsonUtil.toJson(map);
        Map<String, Object> parsed = JsonUtil.asMap(JsonUtil.parse(json));
        assertEquals("A \"quoted\" name", JsonUtil.asString(parsed.get("name")));
        assertEquals(500.0, JsonUtil.asDouble(parsed.get("fee")));
    }

    @Test
    public void toJsonOfListAndNull() {
        assertEquals("[1.0,2.0]", JsonUtil.toJson(List.of(1.0, 2.0)));
        assertEquals("null", JsonUtil.toJson(null));
    }
}
