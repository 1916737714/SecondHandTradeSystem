package com.example.andoirdsecondhandtradingsystem.Home

import android.content.Context
import android.os.NetworkOnMainThreadException
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.key.Key.Companion.Home
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.andoirdsecondhandtradingsystem.BottomNavigationBar
import com.example.andoirdsecondhandtradingsystem.HomePage.CategoryList
import com.example.andoirdsecondhandtradingsystem.HomePage.CategoryTransform
import com.example.andoirdsecondhandtradingsystem.HomePage.SearchPage
import com.example.andoirdsecondhandtradingsystem.HomePage.SearchResult
import com.example.andoirdsecondhandtradingsystem.data.AuthViewModel
import com.example.andoirdsecondhandtradingsystem.data.Data
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.selects.select
import okhttp3.*
import java.io.IOException
import java.lang.reflect.Type

// 商品数据类
data class GoodsItem(
    val id: String,
    val appKey: String,
    val tUserId: String,
    val imageCode: String,
    val content: String,
    val price: Int,
    val addr: String,
    val typeId: Int,
    val typeName: String,
    val status: Int,
    val createTime: String,
    val username: String,
    val avatar: String,
    val imageUrlList: List<String>,
    val appIsShare: Int,
    val tuserId: String
)

// 接收服务器响应的泛型数据类
data class GoodsResponse(
    val records: List<GoodsItem>,
    val total: Int,
    val size: Int,
    val current: Int
)

// 更改类名为 ApiResponse
data class ApiResponse<T>(
    val code: Int,
    val msg: String,
    val data: T
)

//@ExperimentalMaterial3Api
@Composable
fun AppNavigation(navController: NavHostController, user: Data.User,typeId: MutableState<Int>,selectedCategory: MutableState<String>,onShowBarsChanged: (Boolean) -> Unit) {
//    val navController = rememberNavController()
    val queryState=remember{ mutableStateOf("")}

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route


    LaunchedEffect(currentRoute) {
        onShowBarsChanged(currentRoute !in setOf("appNavigation9/{goodsId}", "searchResult?query={query}"))
    }

    Scaffold(

//        bottomBar = {
//            if (currentRoute !in setOf("appNavigation9/{goodsId}", "searchResult?query={query}")) {
//                BottomNavigationBar(navController)
//            }
//        }

    ) { paddingValues ->
    NavHost(navController = navController, startDestination = "home",Modifier.padding(paddingValues)) {
        composable("home") {
            Home(navController, user,typeId, selectedCategory)
        }
        composable("appNavigation9/{goodsId}") { backStackEntry ->
            val goodsId = backStackEntry.arguments?.getString("goodsId")
            goodsId?.let {
                AppNavigation9(navController, user, it)
            }
        }
        composable("searchResult?query={query}"){
                backStackEntry->
            val query = backStackEntry.arguments?.getString("query")?:""
            queryState.value=query
            SearchResult(user = user, navController = navController, searchText =queryState )
    }
}
}
    }

@Composable
fun Home(navController: NavController, user: Data.User,typeId: MutableState<Int>,selectedCategory: MutableState<String>,viewModel: AuthViewModel = viewModel()) {
    // 使用 remember 保存商品列表的状态
    var goodsItems by rememberSaveable { mutableStateOf(listOf<GoodsItem>()) }
    var errorMessage by rememberSaveable { mutableStateOf("") }
    var isRefreshing by rememberSaveable { mutableStateOf(false) }
    var currentPage by rememberSaveable { mutableStateOf(1) }
    var isLoading by rememberSaveable { mutableStateOf(false) }
    var hasMore by rememberSaveable { mutableStateOf(true) }

    val swipeRefreshState = remember { SwipeRefreshState(isRefreshing) }
    val gridState = rememberLazyGridState()

    var goodsTypeList by remember { mutableStateOf<List<Data.goodsType>>(emptyList()) }
    viewModel.getAllGoodsType(
        onSuccess = { list ->
            goodsTypeList = list
        },
        onError = {
            // 处理错误逻辑
            Log.e("GoodsMangeError", it)
        }
    )

//    // 获取 SharedPreferences 实例
//    val sharedPreferences = LocalContext.current.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
//
//// 恢复选中的分类
//    val savedCategory = sharedPreferences.getString("selectedCategory", "") ?: ""
//
//    selectedCategory.value=savedCategory


    // 获取商品数据
    fun fetchGoods(page: Int,typeId: Int) {
        if (isLoading || !hasMore) return

        isLoading = true
        fetchGoods(user, page, typeId,{ list, total ->
            if (page == 1) {
                goodsItems = list
            } else {
                goodsItems = goodsItems + list
            }
            hasMore = goodsItems.size < total
            isRefreshing = false
            isLoading = false
        }, { error ->
            errorMessage = error
            isRefreshing = false
            isLoading = false
        })
    }


    // 在Composable中启动网络请求
    LaunchedEffect(typeId.value) {
        currentPage=1
        hasMore=true
        fetchGoods(1,typeId.value)
    }

    // 监听列表滚动事件以实现分页加载
    LaunchedEffect(gridState) {
        snapshotFlow { gridState.layoutInfo }
            .map { it.visibleItemsInfo }
            .filter { it.isNotEmpty() }
            .map { it.lastOrNull()?.index }
            .distinctUntilChanged()
            .collect { lastVisibleItemIndex ->
                if (lastVisibleItemIndex == goodsItems.lastIndex && !isLoading && hasMore) {
                    currentPage += 1
                    fetchGoods(currentPage,typeId.value)
                }
            }
    }
    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SearchPage(navController)
        CategoryList(goodsTypeList, { selectedCategory.value= it
//                                    with(sharedPreferences.edit()){
//                                        putString("selectedCategory",it)
//                                        apply()
//                                    }
                                    }, selectedCategory,typeId)

        SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = {
            currentPage = 1
            hasMore = true
            fetchGoods(1,typeId.value)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF0F0F0))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
//            Text(
//                text = "商品列表",
//                style = MaterialTheme.typography.titleLarge.copy(
//                    fontWeight = FontWeight.Bold,
//                    color = Color(0xFF333333)
//                ),
//                modifier = Modifier.padding(bottom = 16.dp)
//            )

            if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage, color = Color.Red)
            } else {
                LazyVerticalGrid(
                    state = gridState,
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(0.5.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(goodsItems) { goodsItem ->
                        GoodsItemView(goodsItem, navController)
                    }
                    item {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                        }
                    }
                }
            }
        }
        }
    }
}

// 获取商品数据的网络请求
fun fetchGoods(
    user: Data.User,
    page: Int,
    typeId:Int,
    onSuccess: (List<GoodsItem>, Int) -> Unit,
    onError: (String) -> Unit
) {
    val gson = Gson()
    val url = "https://api-store.openguet.cn/api/member/tran/goods/all?userId=${user.id}&current=$page&size=100&typeId=$typeId" // 修改URL参数

    val headers = Headers.Builder()
        .add("appId", "1c92edcbfd42414e8bfee284c6801259")
        .add("appSecret", "80042819ade5505a74c4cb196258423ed4bb0")
        .add("Accept", "application/json, text/plain, */*")
        .build()

    val request = Request.Builder()
        .url(url)
        .headers(headers)
        .get()
        .build()

    try {
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                onError("请求失败: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let { responseBody ->
                    val body = responseBody.string()
                    val jsonType: Type = object : TypeToken<ApiResponse<GoodsResponse>>() {}.type
                    val dataResponseBody: ApiResponse<GoodsResponse> = gson.fromJson(body, jsonType)

                    if (dataResponseBody.code == 200) {
                        onSuccess(dataResponseBody.data.records, dataResponseBody.data.total)
                    } else {
                        onError("请求失败: ${dataResponseBody.msg}")
                    }
                } ?: onError("请求失败: 没有响应体")
            }
        })
    } catch (ex: NetworkOnMainThreadException) {
        ex.printStackTrace()
        onError("请求失败: ${ex.message}")
    }
}

@Composable
fun GoodsItemView(goodsItem: GoodsItem, navController: NavController, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { navController.navigate("appNavigation9/${goodsItem.id}") }
            .padding(8.dp)
            .scale(1.05f),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            AsyncImage(
                model = goodsItem.imageUrlList.firstOrNull(),
                contentDescription = goodsItem.content,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(Color.Gray)
                    .clip(RoundedCornerShape(16.dp)) // 可调节的圆角
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = goodsItem.content,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "卖家地址: ${goodsItem.addr}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF666666)
                )
            )


            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "价格: ${goodsItem.price}",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF4500) // 橙红色
                    ),
                    modifier = Modifier.weight(1f)  // 占据剩余空间
                )

                Spacer(modifier = Modifier.width(8.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                AsyncImage(
                    model = goodsItem.avatar,
                    contentDescription = "用户头像",
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)  // 将头像变为圆角
                        .background(Color.Gray)
                )
                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = goodsItem.username,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF666666)
                    )
                )
            }
        }
    }
}