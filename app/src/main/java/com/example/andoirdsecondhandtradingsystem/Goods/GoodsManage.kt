package com.example.andoirdsecondhandtradingsystem.Goods


import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.andoirdsecondhandtradingsystem.R


@Composable
fun GoodsManage(){
    var selectedTab by remember { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("发布商品") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("待发布商品") }
            )
        }

        when (selectedTab) {
            0 -> PublishProductScreen()
            1 -> PendingProductScreen()
        }
    }
}

@Composable
fun PublishProductScreen() {
    var productName by remember { mutableStateOf("") }
    var productDescription by remember { mutableStateOf("") }
    var productPrice by remember { mutableStateOf("") }
    var productImage: ImageBitmap? by remember { mutableStateOf(null) }

    val context = LocalContext.current
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data
            uri?.let {
                val inputStream = context.contentResolver.openInputStream(it)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                productImage = bitmap.asImageBitmap()
            }
        }
    }
    Column (modifier = Modifier
        .fillMaxSize()
        .padding(12.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ){

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
//        elevation = 4.dp,  // 设置卡片阴影
        shape = RoundedCornerShape(16.dp)  // 设置卡片圆角
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 上传图片
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("上传图片", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.width(16.dp))
                Box(
                    modifier = Modifier
                        .size(100.dp, 100.dp)
                        .background(Color.White)
                        .clickable {
                            val intent =
                                Intent(
                                    Intent.ACTION_PICK,
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                                )
                            imagePickerLauncher.launch(intent)
                        }
                ) {
                    productImage?.let {
                        Image(
                            painter = BitmapPainter(it),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } ?: run {
                        Icon(
                            painter = painterResource(id = R.drawable.add_1),
                            contentDescription = null,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 商品描述
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("商品描述", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.width(16.dp))
                BasicTextField(
                    value = productDescription,
                    onValueChange = { productDescription = it },
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp)
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    textStyle = TextStyle(color = Color.Black, fontSize = 16.sp),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 商品名称
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("商品名称", style = MaterialTheme.typography.bodyLarge)

                Spacer(modifier = Modifier.width(16.dp))

                BasicTextField(
                    value = productName,
                    onValueChange = { productName = it },
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    textStyle = TextStyle(color = Color.Black, fontSize = 16.sp),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
                )

            }

            Spacer(modifier = Modifier.height(16.dp))


            // 商品价格
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("商品价格", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.width(16.dp))
                BasicTextField(
                    value = productPrice,
                    onValueChange = { productPrice = it },
                    modifier = Modifier
                        .width(100.dp)
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    textStyle = TextStyle(color = Color.Black, fontSize = 16.sp),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Number
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            // Handle onDone event
                        }
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("¥", style = TextStyle(color = Color.Black, fontSize = 16.sp))
            }

            Spacer(modifier = Modifier.height(16.dp))


            //商品类型
            var productType by remember { mutableStateOf("") }
            var expandedType by remember { mutableStateOf(false) }
            val categories = listOf("类型1", "类型2", "类型3", "类型4", "类型5")

            var dropdownWidth by remember { mutableStateOf(0) }
            var boxOffset by remember { mutableStateOf(Offset.Zero) }
            val density = LocalDensity.current

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("商品类型", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.width(16.dp))

                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(48.dp)
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .clickable { expandedType = true }
                        .padding(16.dp)
                        .onGloballyPositioned { coordinates ->
                            dropdownWidth = coordinates.size.width
                            boxOffset = coordinates.positionInRoot()
                        }
                ) {
                    Row(
                        Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = productType,
                            style = TextStyle(color = Color.Black, fontSize = 16.sp),
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null
                        )
                    }

                    DropdownMenu(
                        expanded = expandedType,
                        onDismissRequest = { expandedType = false },
                        modifier = Modifier.width(with(density) { dropdownWidth.toDp() }),
                        offset = DpOffset(x = 0.dp, y = with(density) { -boxOffset.y.toDp() - 48.dp })
                    ) {
                        Column(
                            modifier = Modifier
                                .heightIn(max = 200.dp)  // 设置最大高度
                                .verticalScroll(rememberScrollState())  // 添加垂直滚动
                        ) {
                            categories.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category) },
                                    onClick = {
                                        productType = (category)
                                        expandedType = false
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            //商品地址
            var productLocation by remember { mutableStateOf("") }
            var expandedLocation by remember { mutableStateOf(false) }
            val locations = listOf("北京", "上海", "广州", "深圳", "杭州","广西壮族自治区")


            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("商品地址", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.width(16.dp))

                Box(
                    modifier = Modifier
                        .width(200.dp)
                        .height(48.dp)
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .clickable { expandedLocation = true }
                        .padding(16.dp)
                        .onGloballyPositioned { coordinates ->
                            dropdownWidth = coordinates.size.width
                            boxOffset = coordinates.positionInRoot()
                        }
                ) {
                    Row(
                        Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = productLocation,
                            style = TextStyle(color = Color.Black, fontSize = 16.sp),
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null
                        )
                    }

                    DropdownMenu(
                        expanded = expandedLocation,
                        onDismissRequest = { expandedLocation = false },
                        modifier = Modifier.width(with(density) { dropdownWidth.toDp() }),
                        offset = DpOffset(x = 0.dp, y = with(density) { -boxOffset.y.toDp() - 48.dp })
                    ) {
                        Column(
                            modifier = Modifier
                                .heightIn(max = 200.dp)  // 设置最大高度
                                .verticalScroll(rememberScrollState())  // 添加垂直滚动
                        ) {
                            locations.forEach { location ->
                                DropdownMenuItem(
                                    text = { Text(location) },
                                    onClick = {
                                        productLocation =(location)
                                        expandedLocation = false
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))


            //详细地址
            var detailAddrress by remember { mutableStateOf("") }
            Row {
                Text("                   ")
                Spacer(modifier = Modifier.height(16.dp))
                BasicTextField(
                    value = detailAddrress,
                    onValueChange = { detailAddrress = it },
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    textStyle = TextStyle(color = Color.Black, fontSize = 16.sp),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                    decorationBox = { innerTextField ->
                        if (detailAddrress.isEmpty()) {
                            Text(
                                text = "详细地址",
                                style = TextStyle(color = Color.Gray, fontSize = 16.sp)
                            )
                        }
                        innerTextField()
                    }
                )
            }


            var mergedAddress by remember { mutableStateOf("") }

            // 合并地址逻辑,任意一方变化，则更新合并地址
            LaunchedEffect(productLocation, detailAddrress) {
                mergedAddress = "$productLocation $detailAddrress"
            }





        }
        Spacer(modifier = Modifier.height(16.dp))
    }




    // 操作按钮

        val gradientBrush = Brush.horizontalGradient(colors = listOf(Color(0xFFFFEB3B), Color(0xFFFF9800))) // 渐变背景


    Row(
        horizontalArrangement = Arrangement.Center,
//        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedButton(
            onClick = {
                // Handle later publish
            },
            modifier = Modifier
                .width(128.dp)
                .height(48.dp)
                .padding(end = 16.dp) // 调整间距以确保两个按钮之间有适当的空隙
        ) {
            Text("稍后发布", color = Color.Black, fontSize = 14.sp)
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50)) // 圆角
                .background(gradientBrush) // 渐变背景
                .width(128.dp)
                .height(48.dp)
        ) {
            Button(
                onClick = {
                    // Handle immediate publish
                },
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(Color.Transparent)
            ) {
                Text("立即发布", color = Color.Black, fontSize = 16.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPublishProductScreen() {
    PublishProductScreen()
}
//待发布商品界面
@Composable
fun PendingProductScreen() {
    // 示例商品数据
    val products = List(10) { index ->
        Product(
            name = "商品名称 $index",
            price = "￥${index * 10 + 0.99}",
            imageRes = R.drawable.image5 // 替换为实际的图片资源ID
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(products) { product ->
            ProductCard(product = product)
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}
//待发布商品卡片
@Composable
fun ProductCard(product: Product) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Box {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(16.dp))
                    ) {
                        Image(
                            painter = painterResource(id = product.imageRes),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Text(product.name, style = MaterialTheme.typography.titleLarge)
                        Text(product.price, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}

// 数据类表示商品
data class Product(
    val name: String,
    val price: String,
    val imageRes: Int,
    val id: Int = name.hashCode() // 确保每个产品有唯一的 ID
)