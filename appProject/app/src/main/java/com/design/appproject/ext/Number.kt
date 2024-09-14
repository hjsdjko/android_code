package com.design.appproject.ext

import java.math.RoundingMode
import java.text.DecimalFormat

//最多保留两位小数
inline val Number.twoDecimal
    get() = if (DecimalFormat("0.00").apply {
            roundingMode = RoundingMode.HALF_UP
        }.format(this).endsWith(".00")) DecimalFormat("0.00").apply {
        roundingMode = RoundingMode.HALF_UP
    }.format(this).replace(".00", "") else DecimalFormat("0.00").apply {
        roundingMode = RoundingMode.HALF_UP
    }.format(this)
