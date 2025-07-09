package com.annywalker.ipet.managers

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.ClearCredentialException
import com.annywalker.ipet.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseLoginManager @Inject constructor(
    @ApplicationContext val context: Context,
    private val auth: FirebaseAuth
) {

    companion object {
        private const val TAG = "FirebaseLoginManager"
    }

    private val credentialManager: CredentialManager = CredentialManager.create(context)

    suspend fun login(email: String, password: String): Result<Boolean> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signUp(email: String, password: String): Result<Boolean> {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            Result.success(true)
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao criar conta: ${e.message}")
            Result.failure(e)
        }
    }

    fun createGoogleSignInRequest(): GetCredentialRequest {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(context.getString(R.string.default_web_client_id))
            .build()

        return GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
    }

    suspend fun firebaseAuthWithCredential(credential: Any): Result<Boolean> {
        return try {
            if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
            } else {
                Log.w(TAG, "Credential is not of type Google ID!")
                Result.failure(Exception("Credencial inválida"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Falha no login Firebase com credencial Google", e)
            Result.failure(e)
        }
    }

    private suspend fun firebaseAuthWithGoogle(idToken: String): Result<Boolean> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential).await()
            Result.success(true)
        } catch (e: Exception) {
            Log.e(TAG, "Erro no login com credencial Google: ${e.message}")
            Result.failure(e)
        }
    }

    fun isUserLoggedIn(): Boolean = auth.currentUser != null

    suspend fun signOutAndClearCredentials() {
        auth.signOut()
        try {
            val clearRequest = androidx.credentials.ClearCredentialStateRequest()
            credentialManager.clearCredentialState(clearRequest)
            Log.d(TAG, "Estado de credenciais limpado com sucesso")
        } catch (e: ClearCredentialException) {
            Log.e(TAG, "Não foi possível limpar credenciais: ${e.localizedMessage}")
        }
    }

    fun getCurrentUser() = auth.currentUser
}
