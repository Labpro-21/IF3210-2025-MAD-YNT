package com.ynt.purrytify.ui.login
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.ynt.purrytify.R
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ynt.purrytify.data.model.LoginResponse

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit = {},
    viewModel: LoginViewModel = viewModel()
) {
    val context = LocalContext.current
    val loginState = viewModel.state.collectAsStateWithLifecycle().value

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .height(100.dp)
                    .width(100.dp)
            )
            TitleText()

            EmailTextField(email = email, onEmailChange = { email = it })
            PasswordTextField(password = password, onPasswordChange = { password = it })

            LoginButton(onClick = {
                viewModel.login(email, password, context)
            })

            when (loginState) {
                is LoginState.Loading -> {
                    CircularProgressIndicator(color = Color.White)
                }

                is LoginState.Error -> {
                    Text(
                        text = (loginState as LoginState.Error).message,
                        color = Color.Red
                    )
                }

                is LoginState.Success -> {
                    val loginResponse = loginState.data
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Access Token:", color = Color.White)
                        Text(text = loginResponse.accessToken, color = Color.Green)

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(text = "Refresh Token:", color = Color.White)
                        Text(text = loginResponse.refreshToken, color = Color.Green)
                    }

                    // LaunchedEffect(Unit) { onLoginSuccess() }
                }

                else -> {}
            }
        }
    }
}