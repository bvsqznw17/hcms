package com.ruoyi.common.utils.modbus;

import java.util.Arrays;

public class MRuleTools {
    public static int getPrec(int sn, int sysDotNum, int prmWorP) {

        if ((sn >= 0 && sn <= 655
                && !isInRange(sn, new int[] { 60, 61, 76, 81, 128, 140, 163, 185, 204, 427, 431, 504, 527, 637, 652 })))
            return 0;
        else if (isInRange(sn,
                new int[] { 60, 61, 76, 81, 128, 140, 163, 185, 204, 427, 431, 504, 511, 512, 527, 637, 652 }))
            return sysDotNum % 4;
        else if (sn == 78)
            return 3;
        else if (isInRange(sn, new int[] { 80, 82, 165, 166 }) || (sn >= 527 && sn <= 799))
            return (sysDotNum == 0 || sysDotNum == 1) ? 2 : ((sysDotNum == 2) ? 3 : 1);
        else if (isInRange(sn, new int[] { 99, 101, 397, 441 }))
            return (prmWorP == 1 || prmWorP == 2) ? 0 : sysDotNum % 4;
        else if (isInRange(sn, new int[] { 104, 141, 167 }))
            return (prmWorP == 2) ? 0 : sysDotNum % 4;
        else if (isInRange(sn, new int[] { 133, 139, 210, 219, 270, 271 }))
            return 1;
        else
            return 0;
    }

    private static boolean isInRange(int value, int[] range) {
        return Arrays.stream(range).anyMatch(num -> num == value);
    }

}
