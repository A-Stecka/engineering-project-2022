package com.zpi.view.profile

import com.zpi.R

class ImageMapper {

    companion object {

        @JvmStatic
        fun getIndex(resource: Int): Int {
            return when (resource) {
                R.drawable.avatar_1 -> 1
                R.drawable.avatar_2 -> 2
                R.drawable.avatar_3 -> 3
                R.drawable.avatar_4 -> 4
                R.drawable.avatar_5 -> 5
                R.drawable.avatar_6 -> 6
                R.drawable.avatar_7 -> 7
                R.drawable.avatar_8 -> 8
                R.drawable.avatar_9 -> 9
                R.drawable.avatar_10 -> 10
                R.drawable.avatar_11 -> 11
                R.drawable.avatar_12 -> 12
                R.drawable.avatar_13 -> 13
                R.drawable.avatar_14 -> 14
                R.drawable.avatar_15 -> 15
                R.drawable.avatar_16 -> 16
                R.drawable.avatar_17 -> 17
                R.drawable.avatar_18 -> 18
                R.drawable.avatar_19 -> 19
                R.drawable.avatar_20 -> 20
                R.drawable.avatar_21 -> 21
                R.drawable.avatar_22 -> 22
                R.drawable.avatar_23 -> 23
                R.drawable.avatar_24 -> 24
                R.drawable.avatar_25 -> 25
                R.drawable.avatar_26 -> 26
                R.drawable.avatar_27 -> 27
                R.drawable.avatar_28 -> 28
                R.drawable.avatar_29 -> 29
                R.drawable.avatar_30 -> 30
                R.drawable.avatar_31 -> 31
                R.drawable.avatar_32 -> 32
                else -> 0
            }
        }

        @JvmStatic
        fun getResource(index: Int): Int {
            return when (index) {
                1 -> R.drawable.avatar_1
                2 -> R.drawable.avatar_2
                3 -> R.drawable.avatar_3
                4 -> R.drawable.avatar_4
                5 -> R.drawable.avatar_5
                6 -> R.drawable.avatar_6
                7 -> R.drawable.avatar_7
                8 -> R.drawable.avatar_8
                9 -> R.drawable.avatar_9
                10 -> R.drawable.avatar_10
                11 -> R.drawable.avatar_11
                12 -> R.drawable.avatar_12
                13 -> R.drawable.avatar_13
                14 -> R.drawable.avatar_14
                15 -> R.drawable.avatar_15
                16 -> R.drawable.avatar_16
                17 -> R.drawable.avatar_17
                18 -> R.drawable.avatar_18
                19 -> R.drawable.avatar_19
                20 -> R.drawable.avatar_20
                21 -> R.drawable.avatar_21
                22 -> R.drawable.avatar_22
                23 -> R.drawable.avatar_23
                24 -> R.drawable.avatar_24
                25 -> R.drawable.avatar_25
                26 -> R.drawable.avatar_26
                27 -> R.drawable.avatar_27
                28 -> R.drawable.avatar_28
                29 -> R.drawable.avatar_29
                30 -> R.drawable.avatar_30
                31 -> R.drawable.avatar_31
                32 -> R.drawable.avatar_32
                else -> R.drawable.icon_transparent
            }
        }
    }
}