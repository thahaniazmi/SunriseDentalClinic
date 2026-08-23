package com.dental.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JsonUtil {

    public static Object parse(String text) {
        return new Parser(text).parseValue();
    }

    private static class Parser {
        private final String text;
        private int i;

        Parser(String text) {
            this.text = text;
        }

        Object parseValue() {
            skip();
            char c = text.charAt(i);
            if (c == '{') {
                return parseObject();
            }
            if (c == '[') {
                return parseArray();
            }
            if (c == '"') {
                return parseString();
            }
            if (c == 't' || c == 'f') {
                return parseBoolean();
            }
            if (c == 'n') {
                i += 4;
                return null;
            }
            return parseNumber();
        }

        Map<String, Object> parseObject() {
            Map<String, Object> map = new LinkedHashMap<>();
            i++;
            skip();
            if (text.charAt(i) == '}') {
                i++;
                return map;
            }
            while (true) {
                skip();
                String key = parseString();
                skip();
                i++;
                map.put(key, parseValue());
                skip();
                char c = text.charAt(i);
                i++;
                if (c == '}') {
                    return map;
                }
            }
        }

        List<Object> parseArray() {
            List<Object> list = new ArrayList<>();
            i++;
            skip();
            if (text.charAt(i) == ']') {
                i++;
                return list;
            }
            while (true) {
                list.add(parseValue());
                skip();
                char c = text.charAt(i);
                i++;
                if (c == ']') {
                    return list;
                }
            }
        }

        String parseString() {
            i++;
            StringBuilder sb = new StringBuilder();
            while (text.charAt(i) != '"') {
                char c = text.charAt(i);
                if (c == '\\') {
                    i++;
                    char esc = text.charAt(i);
                    if (esc == 'n') {
                        sb.append('\n');
                    } else if (esc == 't') {
                        sb.append('\t');
                    } else if (esc == 'r') {
                        sb.append('\r');
                    } else {
                        sb.append(esc);
                    }
                    i++;
                } else {
                    sb.append(c);
                    i++;
                }
            }
            i++;
            return sb.toString();
        }

        Boolean parseBoolean() {
            if (text.charAt(i) == 't') {
                i += 4;
                return Boolean.TRUE;
            }
            i += 5;
            return Boolean.FALSE;
        }

        Double parseNumber() {
            int start = i;
            while (i < text.length() && "0123456789.-+eE".indexOf(text.charAt(i)) >= 0) {
                i++;
            }
            return Double.parseDouble(text.substring(start, i));
        }

        void skip() {
            while (i < text.length() && Character.isWhitespace(text.charAt(i))) {
                i++;
            }
        }
    }

    // building json

    public static String toJson(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof String) {
            return quote((String) value);
        }
        if (value instanceof Boolean || value instanceof Number) {
            return String.valueOf(value);
        }
        if (value instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) value;
            StringBuilder sb = new StringBuilder("{");
            boolean first = true;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (!first) {
                    sb.append(",");
                }
                first = false;
                sb.append(quote(String.valueOf(entry.getKey()))).append(":").append(toJson(entry.getValue()));
            }
            return sb.append("}").toString();
        }
        if (value instanceof List) {
            List<?> list = (List<?>) value;
            StringBuilder sb = new StringBuilder("[");
            boolean first = true;
            for (Object item : list) {
                if (!first) {
                    sb.append(",");
                }
                first = false;
                sb.append(toJson(item));
            }
            return sb.append("]").toString();
        }
        throw new IllegalArgumentException("Cannot convert " + value.getClass() + " to JSON");
    }

    public static String quote(String value) {
        StringBuilder sb = new StringBuilder("\"");
        for (char c : value.toCharArray()) {
            if (c == '"' || c == '\\') {
                sb.append('\\');
            }
            sb.append(c);
        }
        return sb.append("\"").toString();
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> asMap(Object value) {
        return (Map<String, Object>) value;
    }

    @SuppressWarnings("unchecked")
    public static List<Object> asList(Object value) {
        return (List<Object>) value;
    }

    public static String asString(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    public static double asDouble(Object value) {
        return value == null ? 0 : ((Number) value).doubleValue();
    }
}