package com.example.frontendapp.ui.theme.viewmodels.fakeViewModel

import com.example.frontendapp.data.remote.RetrofitInstance
import com.example.frontendapp.data.remote.source.AuthRepo
import com.example.frontendapp.ui.theme.viewmodels.UsuarioViewModel

class FakeUsuarioViewModel: UsuarioViewModel(AuthRepo(RetrofitInstance.userApi)) {
    init {

    }
}