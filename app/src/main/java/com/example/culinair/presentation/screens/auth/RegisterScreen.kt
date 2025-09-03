package com.example.culinair.presentation.screens.auth

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Divider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.culinair.R
import com.example.culinair.data.repository.RegisterResult
import com.example.culinair.presentation.components.CulinairButton
import com.example.culinair.presentation.components.CulinairTextField
import com.example.culinair.presentation.components.CircularLogoWithLoadingRing
import com.example.culinair.presentation.theme.BrandBackground
import com.example.culinair.presentation.theme.BrandGold
import com.example.culinair.presentation.viewmodel.auth.AuthViewModel
import kotlinx.coroutines.delay

/**
 * Created by John Ralph Dela Rosa on 7/22/2025.
 */
@Composable
fun RegisterScreen(navController: NavHostController, viewModel: AuthViewModel = hiltViewModel()) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val registerResult = viewModel.registerState
    val context = LocalContext.current

    val googleSignInResult = viewModel.googleSignInState

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->

        Log.d("RegisterScreen", "=== ACTIVITY RESULT RECEIVED ===")
        Log.d("RegisterScreen", "Result code: ${result.resultCode}")
        Log.d("RegisterScreen", "RESULT_OK: ${Activity.RESULT_OK}")
        Log.d("RegisterScreen", "RESULT_CANCELED: ${Activity.RESULT_CANCELED}")

        // Extract extras to see error details
        result.data?.extras?.let { extras ->
            Log.d("RegisterScreen", "=== INTENT EXTRAS ===")
            for (key in extras.keySet()) {
                Log.d("RegisterScreen", "$key: ${extras.get(key)}")
            }
        }

        // Always try to handle the result, even if cancelled
        Log.d("RegisterScreen", "Calling handleGoogleSignInResult regardless of result code")
        viewModel.handleGoogleSignInResult(result.data)
    }

    LaunchedEffect(googleSignInResult) {
        googleSignInResult?.onSuccess {
            delay(300)
            navController.navigate("main") {
                popUpTo("signup") { inclusive = true }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BrandBackground)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.culinair_logo),
                contentDescription = "Culinair Logo",
                modifier = Modifier
                    .size(128.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Create Account",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2F4F4F)
            )

            Spacer(Modifier.height(32.dp))

            CulinairTextField(value = email, onValueChange = { email = it }, label = "Email")
            Spacer(Modifier.height(16.dp))
            CulinairTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                isPassword = true
            )

            Spacer(Modifier.height(24.dp))
            CulinairButton(text = "Sign Up", onClick = {
                viewModel.register(email, password)
            })

            // --- OR separator ---
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(modifier = Modifier.weight(1f))
                Text(
                    text = "OR",
                    modifier = Modifier.padding(horizontal = 8.dp),
                    color = Color.Gray
                )
                Divider(modifier = Modifier.weight(1f))
            }

            // Add Google Sign-In Button
            Spacer(Modifier.height(16.dp))
            CulinairButton(
                text = "Continue with Google",
                iconPainter = painterResource(R.drawable.ic_google),
                onClick = {
                    Log.d("LoginScreen", "🚀 Google Sign-In button clicked")
                    val intent = viewModel.getGoogleSignInIntent()
                    Log.d("LoginScreen", "Launching Google Sign-In intent")
                    googleSignInLauncher.launch(intent)
                }
            )

            Spacer(Modifier.height(16.dp))
            TextButton(onClick = {
                navController.navigate("login")
            }) {
                Text("Already have an account? Log in", color = BrandGold)
            }

            registerResult?.onSuccess { result ->
                if (result is RegisterResult.EmailConfirmationSent) {
                    Text(
                        result.message,
                        color = Color(0xFF2F4F4F),
                        fontSize = 14.sp
                    )
                }
            }

            registerResult?.onFailure {
                Text(
                    text = "Sign-up failed: ${it.message}",
                    color = Color(0xFFD32F2F),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }

        // Fullscreen overlay loading
        if (viewModel.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x80000000)), // semi-transparent dark
                contentAlignment = Alignment.Center
            ) {
                CircularLogoWithLoadingRing()
            }
        }

        if (viewModel.isRegistering) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x80000000)), // semi-transparent dark overlay
                contentAlignment = Alignment.Center
            ) {
                CircularLogoWithLoadingRing()
            }
        }
    }
}