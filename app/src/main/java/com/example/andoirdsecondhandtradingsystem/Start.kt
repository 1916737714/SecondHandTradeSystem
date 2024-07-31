package com.example.andoirdsecondhandtradingsystem

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class Start {

    @SuppressLint("NotConstructor")
    @Composable
    fun Start(modifier: Modifier = Modifier,onLoginClick:()->Unit,onRegisterClick:()->Unit){

        Box(modifier = modifier){
            //图片
            Image(
                painter = painterResource(id = R.drawable.login),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                alpha = 0.8f,
            )
            Column ( modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally

            ){
                Spacer(modifier = Modifier.weight(2f))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(2 / 3f)
                        .height(48.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)

                ) {
                    //注册按钮
                    OutlinedButton(
                        onClick = { /*TODO*/ onRegisterClick ()},
                        border = BorderStroke(1.dp, Color.White) ,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text("注册", color = Color.White, fontSize = 20.sp)
                    }
                    //登录按钮
                    Button(
                        onClick = {/*TODO*/onLoginClick () },
                        colors = ButtonDefaults.buttonColors(Color.White),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text("登录", color = Color.Black, fontSize = 20.sp)
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}