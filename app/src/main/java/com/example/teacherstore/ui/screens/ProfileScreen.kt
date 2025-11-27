package com.example.teacherstore.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.teacherstore.viewmodel.UsuarioViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(vm: UsuarioViewModel, onBack: () -> Unit) {
    val userState by vm.userState.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var editing by remember { mutableStateOf(false) }
    var nombre by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }

    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var selectedUriString by remember { mutableStateOf<String?>(null) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            selectedUri = uri
            selectedUriString = uri.toString()
        }
    }

    LaunchedEffect(userState) {
        nombre = userState?.nombre ?: ""
        direccion = userState?.direccion ?: ""
        telefono = userState?.telefono ?: ""
        selectedUriString = userState?.photoUri
        selectedUri = userState?.photoUri?.let { Uri.parse(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    if (userState != null) {
                        if (editing) {
                            TextButton(onClick = {
                                nombre = userState?.nombre ?: ""
                                direccion = userState?.direccion ?: ""
                                telefono = userState?.telefono ?: ""
                                selectedUriString = userState?.photoUri
                                selectedUri = userState?.photoUri?.let { Uri.parse(it) }
                                editing = false
                            }) { Text("Cancelar") }
                        } else {
                            TextButton(onClick = { editing = true }) { Text("Editar") }
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (userState == null) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("No hay usuario cargado")
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = onBack) { Text("Volver") }
                }
                return@Box
            }

            val user = userState!!

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val painter = rememberAsyncImagePainter(selectedUriString ?: user.photoUri)
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .clickable(enabled = editing) {
                            launcher.launch("image/*")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (!((selectedUriString ?: user.photoUri).isNullOrEmpty())) {
                        Image(
                            painter = painter,
                            contentDescription = "Foto de perfil",
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Surface(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = CircleShape
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = user.nombre.takeIf { it.isNotEmpty() }?.firstOrNull()?.toString() ?: "U")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (!editing) {
                    Text(text = user.nombre, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = user.correo, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Dirección: ${user.direccion}", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Teléfono: ${user.telefono}", style = MaterialTheme.typography.bodySmall)

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(onClick = onBack) {
                        Text("Volver")
                    }
                } else {
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "Correo: ${user.correo}", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = direccion,
                        onValueChange = { direccion = it },
                        label = { Text("Dirección") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = telefono,
                        onValueChange = { telefono = it },
                        label = { Text("Teléfono") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (isSaving) return@Button
                            isSaving = true
                            scope.launch {
                                try {
                                    val photoToSave = selectedUriString ?: user.photoUri
                                    vm.updateUser(
                                        nombre = nombre.trim(),
                                        correo = user.correo,
                                        direccion = direccion.trim(),
                                        telefono = telefono.trim(),
                                        photoUri = photoToSave
                                    )
                                    snackbarHostState.showSnackbar("Perfil guardado")
                                    editing = false
                                } catch (t: Throwable) {
                                    snackbarHostState.showSnackbar("Error al guardar")
                                } finally {
                                    isSaving = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text("Guardar")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(onClick = {
                        nombre = user.nombre
                        direccion = user.direccion
                        telefono = user.telefono
                        selectedUriString = user.photoUri
                        selectedUri = user.photoUri?.let { Uri.parse(it) }
                        editing = false
                    }, modifier = Modifier.fillMaxWidth()) {
                        Text("Cancelar")
                    }
                }
            }
        }
    }
}
