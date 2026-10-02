package com.example.incidentscompose.di

import com.example.incidentscompose.data.api.AuthApi
import com.example.incidentscompose.data.api.IncidentApi
import com.example.incidentscompose.data.api.UserApi
import com.example.incidentscompose.data.repository.AuthRepository
import com.example.incidentscompose.data.repository.IncidentRepository
import com.example.incidentscompose.data.repository.UserRepository
import com.example.incidentscompose.data.store.IncidentDataStore
import com.example.incidentscompose.data.store.TokenPreferences
import com.example.incidentscompose.viewmodel.IncidentDetailViewModel
import com.example.incidentscompose.viewmodel.IncidentManagementViewModel
import com.example.incidentscompose.viewmodel.LoginViewModel
import com.example.incidentscompose.viewmodel.MyIncidentDetailViewModel
import com.example.incidentscompose.viewmodel.MyIncidentListViewModel
import com.example.incidentscompose.viewmodel.RegisterViewModel
import com.example.incidentscompose.viewmodel.ReportIncidentViewModel
import com.example.incidentscompose.viewmodel.UserManagementViewModel
import com.example.incidentscompose.viewmodel.UserViewModel
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val networkModule = module {
    single {
        HttpClient(CIO) {
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }
    }
}

val dataModule = module {
    single { TokenPreferences(androidContext()) }
    single { IncidentDataStore(androidContext()) }
    single { AuthApi(get()) }
    single { UserApi(get(), get()) }
    single { IncidentApi(get(), get()) }
    single { AuthRepository(get(), get()) }
    single { UserRepository(get()) }
    single { IncidentRepository(get()) }
}

val viewModelModule = module {
    viewModel { LoginViewModel(get()) }
    viewModel { MyIncidentListViewModel (get(), get(), get(), get(), get()) }
    viewModel { MyIncidentDetailViewModel(get(), get()) }
    viewModel { RegisterViewModel(get()) }
    viewModel { ReportIncidentViewModel(get(), get()) }
    viewModel { UserViewModel(get()) }
    viewModel { UserManagementViewModel(get(), get())}
    viewModel { IncidentManagementViewModel(get(), get())}
    viewModel { IncidentDetailViewModel(get(), get(), get()) }
}

