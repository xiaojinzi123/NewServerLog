package com.xiaojinzi.util;

import com.xiaojinzi.anno.Emptyable;
import com.xiaojinzi.anno.NotEmpty;

public class AiTeContentUtil {

    public static final char AITE = '@';

    @Emptyable
    public static String getContent(@NotEmpty String text) {
        int index = text.indexOf(AITE);
        if (index < 0 || index == text.length() - 1) {
            return null;
        }
        return text.substring(index + 1);
    }

}
