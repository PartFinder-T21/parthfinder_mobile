package com.example.parthfinder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.config.Config
import com.example.parthfinder.api.Groups
import com.example.parthfinder.api.Access
import com.example.parthfinder.theme.ParthFinderTheme
import com.example.parthfinder.ui.screen.CampainScreen
import com.example.parthfinder.ui.screen.CharactersScreen
import com.example.parthfinder.ui.screen.HomeScreen
import com.example.parthfinder.ui.screen.LoginScreen

class MainActivity : ComponentActivity() {

    val config = Config()
    val groups = Groups(config.baseUrl)
    val access = Access(config.baseUrl)

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navBarController = rememberNavController()
            ParthFinderTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                titleContentColor = MaterialTheme.colorScheme.primary,
                            ),
                            title = {
                                Text("ParthFinder")
                            }
                        )
                    },
                    bottomBar = { Navbar(navBarController) }

                ){ innerpadding ->
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                        .fillMaxSize()
                        .padding(innerpadding)) {
                        NavHost(
                            navController = navBarController,
                            startDestination = MainRoute.Home.name,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            composable(MainRoute.Home.name) { HomeScreen(groups) }
                            composable(MainRoute.Characters.name) { CharactersScreen() }
                            composable(MainRoute.Campains.name) { LoginScreen(access) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Navbar(navBarController: NavHostController)
{
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.1f)
    ) {
        NavigationBarItem(
            label = { Text(text = "Campagne") },
            selected = false,
            onClick = { navBarController.navigate(MainRoute.Campains.name) },
            icon = { Icon(modifier = Modifier.fillMaxHeight(0.3f), painter = painterResource(id = R.drawable.campagne_button), contentDescription = "campagne") }
        )
        NavigationBarItem(
            label = { Text(text = "Home") },
            selected = false,
            onClick = { navBarController.navigate(MainRoute.Home.name) },
            icon = { Icon(modifier = Modifier.fillMaxHeight(0.3f), painter = painterResource(id = R.drawable.home_button), contentDescription = "home") }
        )
        NavigationBarItem(
            label = { Text(text = "Personaggi") },
            selected = false,
            onClick = { navBarController.navigate(MainRoute.Characters.name) },
            icon = { Icon(modifier = Modifier.fillMaxHeight(0.3f), painter = painterResource(id = R.drawable.personaggi_button), contentDescription = "personaggi") }

        )
    }
}

enum class MainRoute {
    Home,
    Characters,
    Campains
}