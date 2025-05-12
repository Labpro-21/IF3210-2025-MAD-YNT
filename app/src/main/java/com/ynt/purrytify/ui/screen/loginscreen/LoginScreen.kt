package com.ynt.purrytify.ui.screen.loginscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ynt.purrytify.R
import com.ynt.purrytify.ui.screen.loginscreen.component.EmailTextField
import com.ynt.purrytify.ui.screen.loginscreen.component.LoginButton
import com.ynt.purrytify.ui.screen.loginscreen.component.PasswordTextField
import com.ynt.purrytify.ui.screen.loginscreen.component.TitleText
import com.ynt.purrytify.utils.auth.SessionManager

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit = {},
    sessionManager: SessionManager,
    viewModel: LoginViewModel = viewModel()
) {
    val context = LocalContext.current
    val loginState = viewModel.state.collectAsStateWithLifecycle().value
    val scrollState = rememberScrollState()

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var isCheckingToken by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        if (sessionManager.isLoggedIn()) {
            sessionManager.refreshExpired()
            onLoginSuccess()
        } else {
            sessionManager.clearTokens()
            sessionManager.clearUser()
            isCheckingToken = false
        }
    }

    if (isCheckingToken) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = Color(0xFF1BB452)
            )
        }
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp)
                .imePadding(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .height(100.dp)
                    .width(100.dp)
                    .padding(bottom = 20.dp)
            )
            TitleText()
            Spacer(modifier = Modifier.height(20.dp))
            EmailTextField(email = email, onEmailChange = { email = it })
            Spacer(modifier = Modifier.height(12.dp))
            PasswordTextField(password = password, onPasswordChange = { password = it })
            Spacer(modifier = Modifier.height(24.dp))
            LoginButton(onClick = {
                viewModel.login(email, password, context)
            })
            Spacer(modifier = Modifier.height(16.dp))
            when (loginState) {
                is LoginState.Loading -> {
                    CircularProgressIndicator(color = Color.White)
                }

                is LoginState.Error -> {
                    Text(
                        text = loginState.message,
                        color = Color.Red
                    )
                }

                is LoginState.Success -> {
                    val loginResponse = loginState.data
                    LaunchedEffect(loginState) {
                        sessionManager.saveTokens(
                            accessToken = loginResponse.accessToken,
                            refreshToken = loginResponse.refreshToken
                        )
                        sessionManager.setUser(email)
                        onLoginSuccess()
                    }
                }
                else -> {}
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}