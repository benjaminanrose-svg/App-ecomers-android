package com.example.teacherstore.fakes

import com.example.teacherstore.viewmodel.UsuarioViewModel
import com.example.teacherstore.repository.*
import kotlinx.coroutines.flow.MutableStateFlow

class FakeUsuarioViewModel : UsuarioViewModel(
    userRepo = FakeUserRepository(),
    productRepo = FakeProductRepository(),
    userManager = FakeUserManager()
)
