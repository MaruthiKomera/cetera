package com.cetera.domain;

import java.util.List;

/**
 * utils to change list to string
 * Created by danni on 3/28/16.
 */
public class JoinUtils {
    public static String joins(List list) {

        StringBuilder stringBuilder = new StringBuilder();
        String loopDelim = "";
        for (Object ans: list) {
            stringBuilder.append(loopDelim);
            if (ans instanceof List) {
                stringBuilder.append("[");
                String innerLoopDelim = "";
                for (Object innerValue: (List) ans) {
                    stringBuilder.append(innerLoopDelim);
                    stringBuilder.append(String.valueOf(innerValue));
                    innerLoopDelim = ",";
                }
                stringBuilder.append("]");
                loopDelim = ",";
            } else {
                stringBuilder.append(String.valueOf(ans));
                loopDelim = ",";
            }
        }
        return stringBuilder.toString();
    }
}
