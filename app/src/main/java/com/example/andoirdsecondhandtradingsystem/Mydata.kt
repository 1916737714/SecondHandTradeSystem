package com.example.andoirdsecondhandtradingsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.rememberImagePainter
import com.example.andoirdsecondhandtradingsystem.data.Data
import com.example.androidsecondhandtradingsystem.MyAmount
import com.example.androidsecondhandtradingsystem.MyMerchandise
import com.example.androidsecondhandtradingsystem.MyOrders
import com.example.androidsecondhandtradingsystem.MySoldItems
import com.example.andoirdsecondhandtradingsystem.Mymanage.MyProfile
import com.example.andoirdsecondhandtradingsystem.Mymanage.MyTransaction
import com.example.androidsecondhandtradingsystem.AppNavigation1
import com.example.androidsecondhandtradingsystem.AppNavigation2
import com.example.androidsecondhandtradingsystem.AppNavigation3
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import java.io.IOException

data class TotalAmount(
    val totalRevenue: Int,
    val totalSpending: Int
)

data class ApiResponse<T>(
    val code: Int,
    val msg: String,
    val data: T
)

@Composable
fun MineScreen(navController: NavHostController, user: Data.User, onShowBarsChanged: (Boolean) -> Unit) {
    val totalAmount = remember { mutableStateOf<TotalAmount?>(null) }
    val balance = remember { mutableStateOf(user.money) }
    val coroutineScope = rememberCoroutineScope()

    val currentUser = rememberUpdatedState(user)

    LaunchedEffect(currentUser.value) {
        coroutineScope.launch {
            val result = fetchTotalAmount(user.id.toString())
            totalAmount.value = result
        }
    }

    fun updateBalanceAndTotalAmount(newBalance: Int) {
        balance.value = newBalance
        coroutineScope.launch {
            val result = fetchTotalAmount(user.id.toString())
            totalAmount.value = result
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // 控制导航栏的显示
    LaunchedEffect(currentRoute) {
        onShowBarsChanged(currentRoute !in setOf("my_merchandise/{username}", "my_orders/{username}", "my_sold_items/{username}"))
    }

    Scaffold(
        bottomBar = {
            if (currentRoute !in setOf("my_merchandise/{username}", "my_orders/{username}", "my_sold_items/{username}")) {
                BottomNavigationBar(navController)
            }
        }
    ) { paddingValues ->
        NavHost(navController = navController, startDestination = "my_data", Modifier.padding(paddingValues)) {
            composable("my_data") {
                Mydata(
                    navController = navController,
                    user = user,
                    totalAmount = totalAmount.value,
                    balance = balance.value,
                    usernameFontSize = 24,
                    userIdFontSize = 18,
                    totalAmountFontSize = 18
                )
            }
            composable("my_profile/{username}") { backStackEntry ->
                val username = backStackEntry.arguments?.getString("username") ?: ""
                MyProfile(navController = navController, user = user)
            }
            composable("my_merchandise/{username}") { backStackEntry ->
                val username = backStackEntry.arguments?.getString("username") ?: ""
                AppNavigation1(navController = navController, user = user)
            }
            composable("my_orders/{username}") { backStackEntry ->
                val username = backStackEntry.arguments?.getString("username") ?: ""
                AppNavigation2(navController = navController, user = user)
            }
            composable("my_amount/{username}") { backStackEntry ->
                val username = backStackEntry.arguments?.getString("username") ?: ""
                MyAmount(navController = navController, user = user, onBalanceChanged = { newBalance ->
                    updateBalanceAndTotalAmount(newBalance)
                })
            }
            composable("my_sold_items/{username}") { backStackEntry ->
                val username = backStackEntry.arguments?.getString("username") ?: ""
                AppNavigation3(navController = navController, user = user)
            }
            composable("my_transaction/{username}") { backStackEntry ->
                val username = backStackEntry.arguments?.getString("username") ?: ""
                MyTransaction(navController = navController, user = user)
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    // 实现底部导航栏的内容
}

@Composable
fun Mydata(
    navController: NavHostController,
    user: Data.User,
    totalAmount: TotalAmount?,
    balance: Int,
    usernameFontSize: Int = 20,
    userIdFontSize: Int = 16,
    totalAmountFontSize: Int = 16
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Image(
                    painter = rememberImagePainter(data = user.avatar),
                    contentDescription = "User Avatar",
                    modifier = Modifier
                        .size(120.dp)
                        .padding(end = 16.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .clickable { navController.navigate("my_profile/${user.username}") },
                    contentScale = ContentScale.Crop
                )
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.padding(top = 5.dp)
                ) {
                    Text(
                        text = "用户名：${user.username}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = Color(0xFF333333),
                            fontSize = 22.sp
                        )
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "用户ID：${user.id}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color(0xFF333333),
                                    fontSize = 13.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        totalAmount?.let {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "总收入：${it.totalRevenue} 元",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color(0xFF333333),
                            fontSize = 15.sp
                        )
                    )
                    Text(
                        text = "总支出：${it.totalSpending} 元",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color(0xFF333333),
                            fontSize = 15.sp
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "钱包",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = Color(0xFF333333)
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "余额：$balance 元",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color(0xFF333333)
                        )
                    )
                    Button(
                        onClick = { navController.navigate("my_amount/${user.username}") },
                        modifier = Modifier.height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722)),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(text = "充值", color = Color.White, fontSize = 16.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { navController.navigate("my_merchandise/${user.username}") },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 4.dp)
                            .height(100.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_shopping_bag_24),
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier
                                    .width(50.dp)
                                    .height(40.dp)
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(text = "我发布的", color = Color.Black, fontSize = 12.sp)
                        }
                    }
                    Button(
                        onClick = { navController.navigate("my_orders/${user.username}") },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp)
                            .height(100.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_shopping_cart_24),
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.width(50.dp)
                                    .height(40.dp)
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(text = "我购买的", color = Color.Black, fontSize = 12.sp)
                        }
                    }
                    Button(
                        onClick = { navController.navigate("my_sold_items/${user.username}") },
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 4.dp)
                            .height(100.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_monetization_on_24),
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier
                                    .width(50.dp)
                                    .height(40.dp)
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(text = "我卖出的", color = Color.Black, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

suspend fun fetchTotalAmount(userId: String): TotalAmount? = withContext(Dispatchers.IO) {
    val url = "https://api-store.openguet.cn/api/member/tran/trading/allMoney?userId=$userId"
    val headers = Headers.Builder()
        .add("appId", "1c92edcbfd42414e8bfee284c6801259")
        .add("appSecret", "80042819ade5505a74c4cb196258423ed4bb0")
        .build()

    val request = Request.Builder()
        .url(url)
        .headers(headers)
        .get()
        .build()

    val client = OkHttpClient()
    try {
        val response = client.newCall(request).execute()
        val body = response.body?.string()
        if (body != null && response.isSuccessful) {
            val type = object : TypeToken<ApiResponse<TotalAmount>>() {}.type
            val apiResponse: ApiResponse<TotalAmount> = Gson().fromJson(body, type)
            if (apiResponse.code == 200) {
                return@withContext apiResponse.data
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return@withContext null
}