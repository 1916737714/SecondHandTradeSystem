package com.example.andoirdsecondhandtradingsystem.HomePage

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.*

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.example.andoirdsecondhandtradingsystem.R
import com.example.andoirdsecondhandtradingsystem.TopBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.HorizontalPageIndicator
import coil.compose.rememberImagePainter
import com.google.accompanist.pager.*
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

@OptIn(ExperimentalPagerApi::class)
@Composable
fun GoodsDetail(navController: NavController,product: Product){

    Scaffold(
        topBar= {
            TopBar(
                title = "商品详情",
            )
        },
        bottomBar={
            BottomAppBar(
                content= {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(onClick = { /*TODO*/ }) {
                            Image(
                                painter = painterResource(id = R.drawable.love),
                                contentDescription = "收藏"
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))

                        Button("聊一聊", modifier = Modifier.weight(1f), onClick = {})
                        Button("立即购买",modifier=Modifier.weight(1f), onClick = {})
                    }
                })

            },
        content={innnerPadding->
            Column(modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(innnerPadding)){
                    Text(
                        text = product.username
                    )
                if(product.imageUrlList.isEmpty()){
                    Image(painter = painterResource(id = R.drawable.baseline_crop_original_24),
                        modifier = Modifier.fillMaxSize() ,
                        contentDescription = "默认图片",
                        contentScale = ContentScale.Crop)
                }else{
                val pagerState = rememberPagerState()
                HorizontalPager(
                    count = product.imageUrlList.size,
                    state = pagerState,
                    modifier = Modifier.fillMaxWidth()
                    ) {page->
                            Image(
                                painter = rememberImagePainter(product.imageUrlList[page]),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                }

               HorizontalPagerIndicator(
                    pagerState=pagerState,
                    modifier= Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp)
                )}

                Text(text = "￥：${product.price}",
                    modifier = Modifier,
                    color = Color.Red ,
                    style = TextStyle(fontSize = 18.sp)
                    )
}
        }
    )
}





