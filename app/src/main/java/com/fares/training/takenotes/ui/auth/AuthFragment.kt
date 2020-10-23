package com.fares.training.takenotes.ui.auth

import android.content.SharedPreferences
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.core.content.edit
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.fares.training.takenotes.R
import com.fares.training.takenotes.data.remote.BasicAuthInterceptor
import com.fares.training.takenotes.ui.BaseFragment
import com.fares.training.takenotes.util.Constants.Preferences.KEY_LOGGED_EMAIL
import com.fares.training.takenotes.util.Constants.Preferences.KEY_LOGGED_PASSWORD
import com.fares.training.takenotes.util.Constants.Preferences.NO_EMAIL
import com.fares.training.takenotes.util.Constants.Preferences.NO_PASSWORD
import com.fares.training.takenotes.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_auth.*
import javax.inject.Inject

@AndroidEntryPoint
class AuthFragment : BaseFragment(R.layout.fragment_auth) {

    private val vm: AuthViewModel by viewModels()

    @Inject
    lateinit var pref: SharedPreferences

    @Inject
    lateinit var basicAuthInterceptor: BasicAuthInterceptor


    private var loggedEmail: String? = null
    private var loggedPassword: String? = null



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isLoggedIn()) {
            authenticateUser(loggedEmail ?: "", loggedPassword ?: "")
            redirectLogin()
        }

        requireActivity().requestedOrientation = SCREEN_ORIENTATION_PORTRAIT
        subscribeToObservers()

        btnRegister.setOnClickListener {
            registerUser()
        }

        btnLogin.setOnClickListener {
            loginUser()
        }
    }


    private val isLoggedIn= {
        loggedEmail = pref.getString(KEY_LOGGED_EMAIL, NO_EMAIL) ?: NO_EMAIL
        loggedPassword = pref.getString(KEY_LOGGED_PASSWORD, NO_PASSWORD) ?: NO_PASSWORD

        loggedEmail != NO_EMAIL && loggedPassword != NO_PASSWORD
    }

    private fun subscribeToObservers() {
        vm.registerStatus.observe(viewLifecycleOwner) { status ->
            status.let {
                when (it) {
                    is Resource.Success<String> -> {
                        registerProgressBar.visibility = GONE
                        showSnackBar(it.data!!)
                    }
                    is Resource.Error<String> -> {
                        registerProgressBar.visibility = GONE
                        showSnackBar(it.message)
                    }
                    is Resource.Loading<String> -> {
                        registerProgressBar.visibility = VISIBLE
                    }
                }
            }
        }

        vm.loginStatus.observe(viewLifecycleOwner) { status ->
            status?.let {
                when (it) {
                    is Resource.Success<String> -> {
                        registerProgressBar.visibility = GONE
                        showSnackBar(it.data!!)
                        pref.edit {
                            putString(KEY_LOGGED_EMAIL, loggedEmail)
                            putString(KEY_LOGGED_PASSWORD, loggedPassword)
                        }
                        authenticateUser(loggedEmail ?: "", loggedPassword ?: "")
                        redirectLogin()
                    }
                    is Resource.Error -> {
                        registerProgressBar.visibility = GONE
                        showSnackBar(it.message)
                    }
                    is Resource.Loading<String> -> {
                        registerProgressBar.visibility = VISIBLE
                    }
                }
            }
        }
    }

    private fun redirectLogin() {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.authFragment, true).build()
        findNavController().navigate(
            AuthFragmentDirections.actionAuthFragmentToNotesFragment(),
            navOptions
        )
    }

    private fun registerUser() {
        val email = etRegisterEmail.text.toString()
        val password = etRegisterPassword.text.toString()
        val confirmedPassword = etRegisterPasswordConfirm.text.toString()
        vm.register(email, password, confirmedPassword)
    }

    private fun loginUser() {
        val email = etLoginEmail.text.toString()
        val password = etLoginPassword.text.toString()
        loggedEmail = email
        loggedPassword = password
        vm.login(email, password)
    }

    private fun authenticateUser(email: String, password: String) {
        basicAuthInterceptor.email = email
        basicAuthInterceptor.password = password
    }


}