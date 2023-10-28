package com.ruoyi.common.constant;

public class DouFeatures {
    /**
     * 最多斗数量
     */
    public static final int MAX_D = 20;

    /**
     * 最多斗分组
     */
    public static final int MAX_G = 1;

    /**
     * 斗数量和斗分组之和
     */
    public static final int MAX_DG = 21;

    /**
     * 每组最多斗数量
     */
    public static final int MAX_DPG = 20;

    /**
     * 斗分组加一
     */
    public static final int MAX_G1 = MAX_G + 1;

    /**
     * 斗数量和斗分组之和加一
     */
    public static final int MAX_DG1 = MAX_DG + 1;

    /**
     * 单层斗数量和斗分组之和
     */
    public static final int MAX_DGG1 = MAX_DG + MAX_G1;

    /**
     * 双层斗数量和斗分组之和
     */
    public static final int MAX_DDG = MAX_G + MAX_D * 2;
}
