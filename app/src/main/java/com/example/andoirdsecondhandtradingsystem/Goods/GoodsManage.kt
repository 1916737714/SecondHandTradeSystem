package com.example.andoirdsecondhandtradingsystem.Goods


import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.andoirdsecondhandtradingsystem.R
import com.example.andoirdsecondhandtradingsystem.data.AuthViewModel
import com.example.andoirdsecondhandtradingsystem.data.Data
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


@Composable
fun GoodsManage(user: Data.User) {
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
            0 -> PublishProductScreen(user)
            1 -> PendingProductScreen()
        }
    }
}

@Composable
fun PublishProductScreen(user: Data.User, viewModel: AuthViewModel = viewModel()) {

    //商品类型列表
    var goodsTypeList by remember { mutableStateOf<List<Data.goodsType>>(emptyList()) }
    //商品名称
    var productName by remember { mutableStateOf("") }
    //商品描述
    var productDescription by remember { mutableStateOf("") }
    //商品价格
    var productPrice by remember { mutableStateOf("") }
    //商品图片
    var productImage: ImageBitmap? by remember { mutableStateOf(null) }

    //详细地址
    var mergedAddress by remember { mutableStateOf("") }
    var productLocation by remember { mutableStateOf("") }
    var detailAddrress by remember { mutableStateOf("") }

    var imageUri: Uri? by remember { mutableStateOf(null) }

    //商品类型
    var expandedType by remember { mutableStateOf(false) }
    //商品类型ID
    var selectedId by remember { mutableIntStateOf(0) }
    //商品类型
    var selectedType by remember { mutableStateOf("") }

    //下拉栏参数
    var dropdownWidth by remember { mutableStateOf(0) }
    var boxOffset by remember { mutableStateOf(Offset.Zero) }
    val density = LocalDensity.current
    //图片码
    var imageCode by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }


    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data
            uri?.let {
                imageUri = it // 保存 Uri 以便稍后上传使用
                val inputStream = context.contentResolver.openInputStream(it)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                productImage = bitmap.asImageBitmap()

                // 上传图片
                val file = File(getPathFromUri(context, it))
                val filePart = prepareFilePart("fileList", file)

                Log.d("Upload", "File path: ${file}")
                Log.d("Upload", "File path: ${filePart}")
                Log.d("Upload", "File path: ${imageUri}")


                viewModel.uploadFiles(listOf(filePart),
                    onSuccess = { upLoadFile ->
                        // 处理成功逻辑
                        imageCode = upLoadFile.imageCode
                        imageUrl = upLoadFile.imageUrlList[0]
                        Log.d("Upload", "Upload successful")
                    },
                    onError = { error ->
                        // 处理错误逻辑
                        Log.e("Upload", "Upload failed: $error")
                    }
                )
            }
        }
    }

    // 定义权限请求的 launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // 权限被授予，打开图片选择器
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            imagePickerLauncher.launch(intent)
        } else {
            // 权限被拒绝，处理拒绝逻辑
            Toast.makeText(context, "需要存储权限以选择图片", Toast.LENGTH_SHORT).show()
        }
    }

    fun checkAndRequestPermission() {
        when {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                // 权限已经被授予，直接打开图片选择器
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                imagePickerLauncher.launch(intent)
            }

            else -> {
                // 请求权限
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

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
//                            val intent =
//                                Intent(
//                                    Intent.ACTION_PICK,
//                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//                                )
//                            imagePickerLauncher.launch(intent)
                                checkAndRequestPermission()
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
                            .clickable {
                                expandedType = true;

                                //动态获取商品类型
                                viewModel.getAllGoodsType(
                                    onSuccess = { list ->
                                        goodsTypeList = list
                                    },
                                    onError = {
                                        // 处理错误逻辑
                                        Log.e("GoodsMangeError", it)
                                    }
                                )
                            }
                            .padding(16.dp)
                            .onGloballyPositioned { coordinates ->
                                dropdownWidth = coordinates.size.width
                                boxOffset = coordinates.positionInRoot()
                            }
                    ) {
                        Row(
                            Modifier
                                .fillMaxSize()
                                .padding(0.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = selectedType,
                                style = TextStyle(color = Color.Black, fontSize = 12.sp),
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
                            offset = DpOffset(
                                x = 0.dp,
                                y = with(density) { -boxOffset.y.toDp() - 48.dp })
                        ) {
                            Column(
                                modifier = Modifier
                                    .heightIn(max = 200.dp)  // 设置最大高度
                                    .verticalScroll(rememberScrollState())  // 添加垂直滚动
                            ) {
                                //将获取的商品类型列表遍历
                                goodsTypeList.forEach { goodsType ->
                                    DropdownMenuItem(
                                        text = { Text(goodsType.type) },
                                        onClick = {
                                            selectedId = goodsType.id
                                            selectedType = goodsType.type
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

                var expandedLocation by remember { mutableStateOf(false) }
                val locations = listOf(
                    LocalContext.current.getString(R.string.province_beijing),
                    LocalContext.current.getString(R.string.province_shanghai),
                    LocalContext.current.getString(R.string.province_tianjin),
                    LocalContext.current.getString(R.string.province_chongqing),
                    LocalContext.current.getString(R.string.province_hebei),
                    LocalContext.current.getString(R.string.province_shanxi),
                    LocalContext.current.getString(R.string.province_liaoning),
                    LocalContext.current.getString(R.string.province_jilin),
                    LocalContext.current.getString(R.string.province_heilongjiang),
                    LocalContext.current.getString(R.string.province_jiangsu),
                    LocalContext.current.getString(R.string.province_zhejiang),
                    LocalContext.current.getString(R.string.province_anhui),
                    LocalContext.current.getString(R.string.province_fujian),
                    LocalContext.current.getString(R.string.province_jiangxi),
                    LocalContext.current.getString(R.string.province_shandong),
                    LocalContext.current.getString(R.string.province_henan),
                    LocalContext.current.getString(R.string.province_hubei),
                    LocalContext.current.getString(R.string.province_hunan),
                    LocalContext.current.getString(R.string.province_guangdong),
                    LocalContext.current.getString(R.string.province_hainan),
                    LocalContext.current.getString(R.string.province_sichuan),
                    LocalContext.current.getString(R.string.province_guizhou),
                    LocalContext.current.getString(R.string.province_yunnan),
                    LocalContext.current.getString(R.string.province_shaanxi),
                    LocalContext.current.getString(R.string.province_gansu),
                    LocalContext.current.getString(R.string.province_qinghai),
                    LocalContext.current.getString(R.string.province_taiwan),
                    LocalContext.current.getString(R.string.province_neimenggu),
                    LocalContext.current.getString(R.string.province_guangxi),
                    LocalContext.current.getString(R.string.province_xizang),
                    LocalContext.current.getString(R.string.province_ningxia),
                    LocalContext.current.getString(R.string.province_xinjiang),
                    LocalContext.current.getString(R.string.province_hongkong),
                    LocalContext.current.getString(R.string.province_macao)
                )

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
                            Modifier
                                .fillMaxSize()
                                .padding(0.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = productLocation,
                                style = TextStyle(color = Color.Black, fontSize = 12.sp),
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                            )
                        }

                        DropdownMenu(
                            expanded = expandedLocation,
                            onDismissRequest = { expandedLocation = false },
                            modifier = Modifier.width(with(density) { dropdownWidth.toDp() }),
                            offset = DpOffset(
                                x = 0.dp,
                                y = with(density) { -boxOffset.y.toDp() - 48.dp })
                        ) {
                            Column(
                                modifier = Modifier
                                    .heightIn(max = 200.dp)
                                    .verticalScroll(rememberScrollState())
                            ) {
                                locations.forEach { location ->
                                    DropdownMenuItem(
                                        text = { Text(location) },
                                        onClick = {
                                            productLocation = location
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
                Row {
                    Text("                        ")
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

                // 合并地址逻辑,任意一方变化，则更新合并地址
                LaunchedEffect(productLocation, detailAddrress) {
                    mergedAddress = "$productLocation $detailAddrress"
                }

            }
            Spacer(modifier = Modifier.height(16.dp))
        }


        // 操作按钮

        val gradientBrush =
            Brush.horizontalGradient(colors = listOf(Color(0xFFFFEB3B), Color(0xFFFF9800))) // 渐变背景


        Row(
            horizontalArrangement = Arrangement.Center,
//        modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(
                onClick = {
                    // Handle later publish

                    // 检查必要字段是否为空
                    if (productDescription =="" || productPrice=="" || selectedId == 0) {
                        Log.d("productDescriptionisNULL", "${productDescription}")
                        Log.d("productPriceisNULL", productPrice)
                        Log.d("selectedIdisNULL", selectedId.toString())
                        Log.d("user.id isNULL", user.id.toString())

                        Toast.makeText(context, "请填写所有必要信息", Toast.LENGTH_SHORT).show()
                    }else {
                        //待发布商品提交
                        viewModel.saveGoodsInfo(
                            mergedAddress,
                            productDescription,
                            imageCode.toIntOrNull() ?: 0, // 防止 imageCode 不是有效的整数
                            productPrice.toIntOrNull() ?: 0, // 防止 productPrice 不是有效的整数
                            selectedId,
                            selectedType,
                            user.id,
                            onSuccess = {
                                Log.d("提交商品信息", "保存成功，清空所有输入内容")
                                // 清空所有输入内容
                                productName = ""
                                productDescription = ""
                                productPrice = ""
                                productImage = null
                                selectedId = 0
                                selectedType = ""
                                mergedAddress = ""
                                detailAddrress = ""
                                productLocation = ""
                                imageCode = ""
                                imageUrl = ""

                                Toast.makeText(context, "保存商品成功", Toast.LENGTH_SHORT).show()
                            },
                            onError = {
                                Log.e("保存商品信息", "网络错误，请稍后重试: $it")
                                Toast.makeText(context, "网络错误，请稍后重试", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        )
                    }
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
                        try {
                            // Handle immediate publish
                            Log.d("打印详细地址", "这不是详细地址 ${mergedAddress}")


                            // 检查必要字段是否为空
                            if (productDescription =="" || productPrice=="" || selectedId == 0) {
                                Log.d("productDescriptionisNULL", "${productDescription}")
                                Log.d("productPriceisNULL", productPrice)
                                Log.d("selectedIdisNULL", selectedId.toString())
                                Log.d("user.id isNULL", user.id.toString())

                                Toast.makeText(context, "请填写所有必要信息", Toast.LENGTH_SHORT).show()
                            }else {
                                //发布商品提交
                                viewModel.addGoodsInfo(
                                    mergedAddress,
                                    productDescription,
                                    imageCode.toIntOrNull() ?: 0, // 防止 imageCode 不是有效的整数
                                    productPrice.toIntOrNull() ?: 0, // 防止 productPrice 不是有效的整数
                                    selectedId,
                                    selectedType,
                                    user.id,
                                    onSuccess = {
                                        Log.d("提交商品信息", "发布成功，清空所有输入内容")
                                        // 清空所有输入内容
                                        productName = ""
                                        productDescription = ""
                                        productPrice = ""
                                        productImage = null
                                        selectedId = 0
                                        selectedType = ""
                                        mergedAddress = ""
                                        imageCode = ""
                                        imageUrl = ""
                                        productLocation = ""
                                        detailAddrress = ""

                                        Toast.makeText(context, "发布成功", Toast.LENGTH_SHORT)
                                            .show()
                                    },
                                    onError = {
                                        Log.e("提交商品信息", "网络错误，请稍后重试: $it")
                                        Toast.makeText(
                                            context,
                                            "网络错误，请稍后重试",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                )
                            }

                            Log.d("打印详细地址", "这是详细地址 ${mergedAddress}")
                        } catch (e: Exception) {
                            Log.e("提交商品信息", "发生异常: ${e.message}", e)
                            Toast.makeText(context, "发生异常，请稍后重试", Toast.LENGTH_SHORT).show()
                        }
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

//@Preview(showBackground = true)
//@Composable
//fun PreviewPublishProductScreen() {
//    PublishProductScreen(viewModel: AuthViewModel)
//}
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

//获取图片的uri
fun getPathFromUri(context: Context, uri: Uri): String {
    var filePath = ""
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        it.moveToFirst()
        val index = it.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        filePath = it.getString(index)
    }
    return filePath
}

fun prepareFilePart(partName: String, file: File): MultipartBody.Part {
    val requestFile = RequestBody.create(
        "image/*".toMediaTypeOrNull(),
        file
    )
    return MultipartBody.Part.createFormData(partName, file.name, requestFile)
}

