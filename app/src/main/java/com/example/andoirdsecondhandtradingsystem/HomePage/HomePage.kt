import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.andoirdsecondhandtradingsystem.ChatScreen
import com.example.andoirdsecondhandtradingsystem.HomePage.CategoryTransform
import com.example.andoirdsecondhandtradingsystem.HomePage.SearchPage
import com.example.andoirdsecondhandtradingsystem.ScreenPage


@Composable
fun HomePage(navController: NavController){

    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SearchPage()
            CategoryTransform(navController)
    }
}





