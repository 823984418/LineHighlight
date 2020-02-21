/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.dxzc.highlight;

/**
 *
 * @author 823984418@qq.com
 */
public class JavaScriptLineHighlight implements LineHighlight<Integer> {

    public static final int NOVALUE = 0;

    public static final int VALUE = 1;

    public static final int REGEXP = 2;

    public static final int DSTRING = 3;

    public static final int SSTRING = 4;

    public static final int NVCOMMIT = 5;

    public static final int VCOMMIT = 6;

    public static boolean isNumber(char c) {
        return c >= '0' && c <= '9';
    }

    public static boolean isDig(char c) {
        return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z';
    }

    public static boolean isId(char c) {
        return c > 0xFF;
    }

    public static boolean isBeginKin(char c) {
        switch (c) {
            case '(':
            case '[':
            case '{':
                return true;
        }
        return false;
    }

    public static boolean isEndKin(char c) {
        switch (c) {
            case ')':
            case ']':
            case '}':
                return true;
        }
        return false;
    }

    public static boolean isOp(char c) {
        switch (c) {
            case '+':
            case '-':
            case '*':
            case '/':
            case '|':
            case '&':
            case '~':
            case '!':
            case '%':
            case '^':
            case ',':
                return true;
        }
        return false;
    }

    public static boolean isKey(String id) {
        switch (id) {
            case "var":
            case "let":
            case "const":
            case "function":
            case "this":
            case "delete":
            case "return":
                return true;
        }
        return false;
    }

    @Override
    public Integer defaultData() {
        return NOVALUE;
    }

    @Override
    public Integer scan(Integer data, char[] text, int start, int end) {
        int d = data;
        for (int ptr = start; ptr < end; ptr++) {
            char c = text[ptr];
            switch (d) {
                case NOVALUE:
                case VALUE: {
                    if (c == '\'') {
                        d = SSTRING;
                        continue;
                    } else if (c == '\"') {
                        d = DSTRING;
                        continue;
                    } else if (c == '/') {
                        if (++ptr < end) {
                            char c1 = text[ptr];
                            if (c1 == '/') {
                                return d;
                            } else if (c1 == '*') {
                                d = VCOMMIT;
                                continue;
                            }
                            if (d == VALUE) {
                                d = NOVALUE;
                                ptr--;
                            } else {
                                d = REGEXP;
                            }
                            continue;
                        } else {
                            return NOVALUE;
                        }
                    } else if (isNumber(c) || isDig(c) || isId(c) || isEndKin(c)) {
                        d = VALUE;
                        continue;
                    } else if (isOp(c) || isBeginKin(c) || c == ';') {
                        d = NOVALUE;
                        continue;
                    } else {
                        continue;
                    }
                }
                case REGEXP: {
                    switch (c) {
                        case '\\':
                            ptr++;
                            continue;
                        case '/':
                            d = VALUE;
                            continue;
                        default:
                            continue;
                    }
                }
                case DSTRING:
                case SSTRING: {
                    if (c == '\\') {
                        ptr++;
                        continue;
                    } else if (d == DSTRING && c == '\"'
                            || d == SSTRING && c == '\'') {
                        d = VALUE;
                        continue;
                    } else {
                        continue;
                    }
                }
                case NVCOMMIT:
                case VCOMMIT: {
                    if (c == '*') {
                        if (++ptr < end) {
                            char c1 = text[ptr];
                            if (c1 == '/') {
                                if (d == VCOMMIT) {
                                    d = VALUE;
                                } else {
                                    d = NOVALUE;
                                }
                                continue;
                            } else {
                                ptr--;
                                continue;
                            }
                        }
                    } else {
                        continue;
                    }
                }
            }
        }
        return d;
    }

    public static final int L_STRING = 1;

    public static final int L_REGEXP = 2;

    public static final int L_COMMIT = 3;

    public static final int L_NUMBER = 4;

    public static final int L_KEY = 5;

    public static final int L_NAME = 6;

    public static final int L_OP = 7;

    @Override
    public void highlight(Integer data, char[] text, int start, int end, OnHighlight callback) {
        int d = data;
        boolean ru = true;
        textChar:
        for (int ptr = start; ru && ptr < end; ptr++) {
            char c = text[ptr];
            int lptr = ptr;
            switch (d) {
                case NOVALUE:
                case VALUE: {
                    if (isNumber(c)) {
                        while (++ptr < end) {
                            char c1 = text[ptr];
                            if (isNumber(c1) || isDig(c1)) {
                                continue;
                            } else {
                                d = VALUE;
                                ru = callback.onHighlight(lptr, --ptr, L_NUMBER);
                                continue textChar;
                            }
                        }
                        ru = callback.onHighlight(lptr, end, L_NUMBER);
                        continue;
                    } else if (isDig(c) || isId(c)) {
                        while (++ptr < end) {
                            char c1 = text[ptr];
                            if (isDig(c1) || isId(c1) || isNumber(c1)) {
                                continue;
                            } else {
                                d = VALUE;
                                String id = new String(text, lptr, ptr - lptr);
                                if (isKey(id)) {
                                    ru = callback.onHighlight(lptr, ptr, L_KEY);
                                } else {
                                    ru = callback.onHighlight(lptr, ptr, L_NAME);
                                }
                                continue textChar;
                            }
                        }
                    } else if (c == '/') {
                        if (++ptr < end) {
                            char c1 = text[ptr];
                            if (c1 == '/') {
                                ru = callback.onHighlight(lptr, end, L_COMMIT);
                                continue;
                            } else if (c1 == '*') {
                                if (d == NOVALUE) {
                                    d = NVCOMMIT;
                                } else {
                                    d = VCOMMIT;
                                }
                                ru = callback.onHighlight(lptr, ptr + 1, L_COMMIT);
                                continue;
                            } else {
                                if (d == NOVALUE) {
                                    d = REGEXP;
                                    ru = callback.onHighlight(lptr, ptr + 1, L_REGEXP);
                                    continue;
                                } else {
                                    d = NOVALUE;
                                    ru = callback.onHighlight(ptr, ptr + 1, L_OP);
                                    continue;
                                }
                            }
                        }
                    } else if (isOp(c) || isBeginKin(c) || c == ';') {
                        d = NOVALUE;
                        ru = callback.onHighlight(ptr, ptr + 1, L_OP);
                        continue;
                    } else if (isEndKin(c)) {
                        d = VALUE;
                        ru = callback.onHighlight(ptr, ptr + 1, L_OP);
                        continue;
                    } else if (c == '\'') {
                        d = SSTRING;
                        ru = callback.onHighlight(ptr, ptr + 1, L_STRING);
                        continue;
                    } else if (c == '\"') {
                        d = DSTRING;
                        ru = callback.onHighlight(ptr, ptr + 1, L_STRING);
                        continue;
                    } else {
                        continue;
                    }
                }
                case REGEXP: {
                    while (++ptr < end) {
                        char c1 = text[ptr];
                        switch (c1) {
                            case '\\':
                                ptr++;
                                continue;
                            case '/':
                                d = VALUE;
                                ru = callback.onHighlight(lptr, ptr + 1, L_REGEXP);
                                continue textChar;
                        }
                    }
                    ru = callback.onHighlight(lptr, end, L_REGEXP);
                    continue;
                }
                case DSTRING:
                case SSTRING: {
                    while (++ptr < end) {
                        char c1 = text[ptr];
                        if (c1 == '\\') {
                            ptr++;
                            continue;
                        } else if (d == DSTRING && c1 == '\"'
                                || d == SSTRING && c1 == '\'') {
                            d = VALUE;
                            ru = callback.onHighlight(lptr, ptr + 1, L_STRING);
                            continue textChar;
                        }
                    }
                    ru = callback.onHighlight(lptr, end, L_STRING);
                    continue;
                }
                case NVCOMMIT:
                case VCOMMIT: {
                    while (++ptr < end) {
                        char c1 = text[ptr];
                        if (c1 == '*') {
                            if (++ptr < end) {
                                if (text[ptr] == '/') {
                                    if (d == VCOMMIT) {
                                        d = VALUE;
                                    } else {
                                        d = NOVALUE;
                                    }
                                    ru = callback.onHighlight(lptr, ptr + 1, L_COMMIT);
                                    continue textChar;
                                }
                            }
                        }
                    }
                    ru = callback.onHighlight(lptr, end, L_COMMIT);
                    continue;
                }
            }
        }
    }

}
