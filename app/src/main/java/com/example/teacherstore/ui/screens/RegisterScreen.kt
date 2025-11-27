package com.example.teacherstore.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.example.teacherstore.viewmodel.UsuarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: UsuarioViewModel,
    onRegisterSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

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
                .fillMaxWidth(0.9f)
                .wrapContentHeight(),
            elevation = CardDefaults.cardElevation(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {

            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Crear Cuenta",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFF1976D2)
                )

                Spacer(modifier = Modifier.height(22.dp))

                // NOMBRE
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null)
                    },
                    label = { Text("Nombre completo") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // CORREO
                OutlinedTextField(
                    value = correo,
                    onValueChange = { correo = it },
                    leadingIcon = {
                        Icon(Icons.Default.Email, contentDescription = null)
                    },
                    label = { Text("Correo") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // CONTRASEÑA
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

                Spacer(modifier = Modifier.height(12.dp))

                // DIRECCIÓN
                OutlinedTextField(
                    value = direccion,
                    onValueChange = { direccion = it },
                    leadingIcon = {
                        Icon(Icons.Default.Home, contentDescription = null)
                    },
                    label = { Text("Dirección") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // TELÉFONO
                OutlinedTextField(
                    value = telefono,
                    onValueChange = { telefono = it },
                    leadingIcon = {
                        Icon(Icons.Default.Phone, contentDescription = null)
                    },
                    label = { Text("Teléfono") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(22.dp))

                // BOTÓN REGISTRAR
                Button(
                    onClick = {
                        if (isLoading) return@Button
                        isLoading = true

                        scope.launch {
                            try {
                                val success = viewModel.register(
                                    nombre = nombre.trim(),
                                    correo = correo.trim(),
                                    contrasena = contrasena,
                                    direccion = direccion.trim(),
                                    telefono = telefono.trim()
                                )
                                if (success) {
                                    onRegisterSuccess()
                                } else {
                                    snackbarHostState.showSnackbar("No se pudo crear la cuenta")
                                }
                            } catch (t: Throwable) {
                                snackbarHostState.showSnackbar("Error al crear la cuenta")
                            } finally {
                                isLoading = false
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
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text("Registrar", color = Color.White)
                }

                Spacer(modifier = Modifier.height(14.dp))

                TextButton(onClick = onNavigateBack) {
                    Text(
                        "Volver",
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
