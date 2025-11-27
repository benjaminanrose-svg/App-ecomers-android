package com.example.teacherstore.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.example.teacherstore.viewmodel.UsuarioViewModel

@Composable
fun LoginScreen(
    viewModel: UsuarioViewModel,
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1976D2),
                        Color(0xFF42A5F5)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth(0.88f)
                .wrapContentHeight(),
            elevation = CardDefaults.cardElevation(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Mi Tiendita",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color(0xFF1976D2)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Iniciar Sesión",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(22.dp))

                OutlinedTextField(
                    value = correo,
                    onValueChange = { correo = it },
                    leadingIcon = {
                        Icon(Icons.Default.Email, contentDescription = null)
                    },
                    label = { Text("Correo electrónico") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = contrasena,
                    onValueChange = { contrasena = it },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null)
                    },
                    label = { Text("Contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        errorMsg = null
                        scope.launch {
                            try {
                                val success = viewModel.login(correo.trim(), contrasena)
                                if (success) {
                                    onLoginSuccess()
                                } else {
                                    errorMsg = "Credenciales inválidas"
                                    snackbarHostState.showSnackbar(errorMsg!!)
                                }
                            } catch (t: Throwable) {
                                errorMsg = "Error al iniciar sesión"
                                snackbarHostState.showSnackbar(errorMsg!!)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1976D2)
                    )
                ) {
                    Text("Entrar", color = Color.White)
                }

                Spacer(modifier = Modifier.height(18.dp))

                TextButton(onClick = onNavigateToRegister) {
                    Text(
                        text = "Crear cuenta nueva",
                        color = Color(0xFF1976D2)
                    )
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}
