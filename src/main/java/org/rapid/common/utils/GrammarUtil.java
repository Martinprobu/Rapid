package org.rapid.common.utils;

public class GrammarUtil {
    /**
     * 类或字段名转换 (按照java 陀峰风格)
     */
    public static String nameCovert_(String name, boolean firstUpper) {
        if (name.isEmpty()) {
            return "";
        }
        String[] classNamePath = name.split("_");
        StringBuffer className = new StringBuffer();
        int firstLetter = 0;
        for (int i = 0; i < classNamePath.length; i++) {
            firstLetter = Integer.valueOf(classNamePath[i].charAt(0));
            if (firstLetter >= 97 && firstLetter <= 122) {
                char c;
                if (i == 0) {
                    if (firstUpper) {
                        c = (char) (firstLetter - 32);
                    } else {
                        c = (char) firstLetter;
                    }
                } else {
                    c = (char) (firstLetter - 32);
                }
                className.append(String.valueOf(c) + classNamePath[i].substring(1));
            } else {
                className.append(classNamePath[i]);
            }
        }
        return className.toString();
    }
}
