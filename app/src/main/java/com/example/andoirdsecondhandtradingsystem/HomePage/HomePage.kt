import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.andoirdsecondhandtradingsystem.HomePage.CategoryTransform
import com.example.andoirdsecondhandtradingsystem.HomePage.SearchPage
import com.example.andoirdsecondhandtradingsystem.data.AuthViewModel
import com.example.andoirdsecondhandtradingsystem.data.Data

@Composable
fun HomePage(navController: NavController,viewModel: AuthViewModel = viewModel(),user: Data.User){

    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SearchPage(navController)
            CategoryTransform(navController,viewModel,user)
    }
}





