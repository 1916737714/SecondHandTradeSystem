package com.example.andoirdsecondhandtradingsystem.HomePage

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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

                        Button("聊一聊", modifier = Modifier.weight(1f))
                        Button("我想要",modifier=Modifier.weight(1f))
                    }
                })

            },
        content={innnerPadding->
            Column(modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(innnerPadding)){
    Text(
        text = product.publisher
    )
    DetailImage(product = product)
    Text(text = product.description)
    Text(text = "评论信息：")
    Text(text = "好吃爱吃，太好吃了哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈")
    Text(text = "爱吃爱吃爱吃啊爱吃爱吃爱吃哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈git")
}
        }
    )
}





