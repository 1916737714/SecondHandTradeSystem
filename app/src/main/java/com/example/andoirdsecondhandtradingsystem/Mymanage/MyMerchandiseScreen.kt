package com.example.andoirdsecondhandtradingsystem.Mymanage

import androidx.compose.runtime.*

import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.navigation.NavController
import com.example.andoirdsecondhandtradingsystem.data.Data
import com.example.androidsecondhandtradingsystem.Merchandise
import com.example.androidsecondhandtradingsystem.MyMerchandise
import com.example.androidsecondhandtradingsystem.fetchMerchandise
@Composable
fun MyMerchandiseScreen(navController: NavController, user: Data.User) {
    var merchandiseList by remember { mutableStateOf(listOf<Merchandise>()) }
    var errorMessage by remember { mutableStateOf("") }

    // 在Composable中启动网络请求
    LaunchedEffect(Unit) {
        fetchMerchandise(user, { list ->
            merchandiseList = list
        }, { error ->
            errorMessage = error
        })
    }

    MyMerchandise(navController = navController, merchandiseList = merchandiseList, errorMessage = errorMessage)
}