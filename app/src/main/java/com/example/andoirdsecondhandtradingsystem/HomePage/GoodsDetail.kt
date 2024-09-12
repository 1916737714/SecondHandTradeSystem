package com.example.andoirdsecondhandtradingsystem.HomePage

import androidx.compose.foundation.content.MediaType.Companion.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable

@Composable
fun GoodsDetail(product: Product){
    Column{
        Text(
            text = product.publisher,
        )
        Text(
            text = product.description
        )
        RoundedImage(product = product)
    }
    Text(
        text = "买家评价"
    )
    Text(
        text="很好吃"
        )
}
