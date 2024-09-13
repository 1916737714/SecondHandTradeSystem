import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.example.andoirdsecondhandtradingsystem.HomePage.CategoryTransform
import com.example.andoirdsecondhandtradingsystem.HomePage.SearchPage



@Composable
fun HomePage(navController: NavController){
    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SearchPage()
            CategoryTransform(navController)
    }
}



