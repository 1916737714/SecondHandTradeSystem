package com.example.andoirdsecondhandtradingsystem

import androidx.annotation.StringRes

sealed class ScreenPage(
    @StringRes val resId: Int = 0, // 默认值是 0
    val iconSelect: Int,
    var isShowText: Boolean = true
) {
    object Home : ScreenPage(
        resId = R.string.str_main_title_home,
        iconSelect = R.drawable.home,

    )

    object Love : ScreenPage(
        resId = R.string.str_main_title_love,
        iconSelect = R.drawable.love,

    )

    object Capture : ScreenPage(
        //resId = R.string.str_main_title_add,//不修改为空，app闪退，资源未找到
        iconSelect = R.drawable.add,
        isShowText = false
    )

    object Message : ScreenPage(
        resId = R.string.str_main_title_message,
        iconSelect = R.drawable.message,

    )

    object Mine : ScreenPage(
        resId = R.string.str_main_title_mine,
        iconSelect = R.drawable.wode_1,
    )
}
