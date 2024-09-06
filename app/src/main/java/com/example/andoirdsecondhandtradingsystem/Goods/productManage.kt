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
import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
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
    var productType by remember { mutableStateOf("") }
    var productLocation by remember { mutableStateOf("") }
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
                    .background(Color.Gray)
                    .clickable {
                        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        imagePickerLauncher.launch(intent)
                    }
            ) {
                productImage?.let {
                    Image(painter = BitmapPainter(it), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                } ?: run {
                    Icon(painter = painterResource(id = R.drawable.image5), contentDescription = null, modifier = Modifier.align(Alignment.Center))
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
                    .background(Color.LightGray)
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
                    .weight(1f)
                    .background(Color.LightGray)
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

        // 商品名称
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("商品名称", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.width(16.dp))
            BasicTextField(
                value = productName,
                onValueChange = { productName = it },
                modifier = Modifier
                    .weight(1f)
                    .background(Color.LightGray)
                    .padding(8.dp),
                textStyle = TextStyle(color = Color.Black, fontSize = 16.sp),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 商品类型
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("商品类型", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.width(16.dp))
            var expandedType by remember { mutableStateOf(false) }
            val categories = listOf("类型1", "类型2", "类型3", "类型4", "类型5")
            Box {
                BasicTextField(
                    value = productType,
                    onValueChange = { productType = it },
                    modifier = Modifier

                        .background(Color.LightGray)
                        .padding(8.dp)
                        .clickable { expandedType = true },
                    textStyle = TextStyle(color = Color.Black, fontSize = 16.sp),
                    readOnly = true
                )
                DropdownMenu(
                    expanded = expandedType,
                    onDismissRequest = { expandedType = false }
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            onClick = {
                                productType = category
                                expandedType = false
                            },
                            text = { Text(category) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 商品所在地
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("商品所在地", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.width(16.dp))
            var expandedLocation by remember { mutableStateOf(false) }
            val locations = listOf("北京", "上海", "广州", "深圳", "杭州")
            Box {
                BasicTextField(
                    value = productLocation,
                    onValueChange = { productLocation = it },
                    modifier = Modifier

                        .background(Color.LightGray)
                        .padding(8.dp)
                        .clickable { expandedLocation = true },
                    textStyle = TextStyle(color = Color.Black, fontSize = 16.sp),
                    readOnly = true
                )
                DropdownMenu(
                    expanded = expandedLocation,
                    onDismissRequest = { expandedLocation = false }
                ) {
                    locations.forEach { location ->
                        DropdownMenuItem(
                            onClick = {
                                productLocation = location
                                expandedLocation = false
                            },
                            text = { Text(location) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 操作按钮
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    // Handle later publish
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp) // 调整间距以确保两个按钮之间有适当的空隙
            ) {
                Text("稍后发布")
            }
            Button(
                onClick = {
                    // Handle immediate publish
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("立即发布")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPublishProductScreen() {
    PublishProductScreen()
}

@Composable
fun PendingProductScreen() {
    // 示例：展示待发布商品的列表
    val pendingProducts = listOf("商品1", "商品2", "商品3")

    Column(modifier = Modifier.padding(16.dp)) {
        Text("待发布商品", style = MaterialTheme.typography.displayLarge)

        Spacer(modifier = Modifier.height(8.dp))

        pendingProducts.forEach { product ->
            Text(product, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}