import androidx.annotation.StringRes
import com.example.andoirdsecondhandtradingsystem.R

sealed class ScreenPage(
    val route: String,
    @StringRes val resId: Int = 0, // 默认值是 0
    val iconSelect: Int,
    val iconUnselect: Int,
    var isShowText: Boolean = true
) {
    object Home : ScreenPage(
        route = "home",
        resId = R.string.str_main_title_home,
        iconSelect = R.drawable.home,
        iconUnselect = R.drawable.home
    )

    object Love : ScreenPage(
        route = "love",
        resId = R.string.str_main_title_love,
        iconSelect = R.drawable.love,
        iconUnselect = R.drawable.love
    )

    object Capture : ScreenPage(
        route = "add",
        //resId = R.string.str_main_title_add,//不修改为空，app闪退，资源未找到
        iconSelect = R.drawable.add,
        iconUnselect = R.drawable.add,
        isShowText = false
    )

    object Message : ScreenPage(
        route = "message",
        resId = R.string.str_main_title_message,
        iconSelect = R.drawable.message,
        iconUnselect = R.drawable.message
    )

    object Mine : ScreenPage(
        route = "mine",
        resId = R.string.str_main_title_mine,
        iconSelect = R.drawable.wode_1,
        iconUnselect = R.drawable.wode_1
    )
}
