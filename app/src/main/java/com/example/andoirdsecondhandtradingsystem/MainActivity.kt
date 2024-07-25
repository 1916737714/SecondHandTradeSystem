package com.example.andoirdsecondhandtradingsystem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.andoirdsecondhandtradingsystem.ui.theme.AndoirdSecondHandTradingSystemTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndoirdSecondHandTradingSystemTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    GreetingText(
//                        message = "郭佳灵生日快乐！！",
//                        from = "from: 2023",
//                        modifier = Modifier.padding(innerPadding)
//                    )
                    Login(
                        message = "郭佳灵生日快乐",
                        from = "from: 2023",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )

                }
            }
        }
    }
}


@Composable
fun Login(message: String,from: String, modifier: Modifier = Modifier) {


    Box(modifier = modifier) {
        Image(
            painter = painterResource(id = R.drawable.image5),
            contentDescription = null,
            contentScale = ContentScale.Crop,//全屏填充
            modifier = Modifier.fillMaxSize(),//填充全屏
            alpha = 0.5f,//透明度
        )
        Column() {
            Spacer(modifier = Modifier
                .weight(2f)
                .clip(RoundedCornerShape(16.dp)))
            Column (
                modifier = Modifier
                    .weight(3f)
                    .background(color = Color.White)
                    .padding(40.dp)
                    .fillMaxSize()
            ){
                Column() {
                    TextField(modifier = Modifier.fillMaxWidth(),
                        value = "name",
                        placeholder = { Text("请输入账户")},
                        onValueChange = {}

                    )
                    TextField(value = "psw",
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("请输入密码")},
                        onValueChange = {} )
                }
                Spacer(modifier = Modifier.height(20.dp))
                Row(horizontalArrangement = Arrangement.SpaceBetween,modifier = Modifier.fillMaxWidth()) {
                    Text(text = "记住密码", fontSize = 16.sp, color = Color.Gray)
                    Text(text = "注册", fontSize = 16.sp, color = Color.Gray)
                }
                Spacer(modifier = Modifier.height(20.dp))

                Button(modifier = Modifier.fillMaxWidth(),
                    onClick = { /*TODO*/ },
                    shape = RoundedCornerShape(50),
                    contentPadding = PaddingValues(12.dp,16.dp)
                    ) {
                    Text("登录",color = Color.White,fontSize = 16.sp)

                }

            }


        }
    }
}

@Preview(showBackground = true)
@Composable
fun Login() {
    AndoirdSecondHandTradingSystemTheme {
        Login(message = "郭生日快乐", from = "from: 2023" )
    }
}
