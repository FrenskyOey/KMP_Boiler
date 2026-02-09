package feature.onboarding.data.repository

import core.domain.model.Result
import core.util.HashingUtil
import feature.onboarding.data.datasource.AuthDataSource
import feature.onboarding.data.model.mapper.toDomain
import feature.onboarding.data.model.mapper.toEntity
import feature.onboarding.data.model.request.LoginRequest
import feature.onboarding.domain.model.LoginCredentials
import feature.onboarding.domain.model.User
import feature.onboarding.domain.repository.AuthRepository
import core.domain.model.AppException

class AuthRepositoryImpl(
    private val remoteDataSource: AuthDataSource.Remote,
    private val localDataSource: AuthDataSource.Local
) : AuthRepository {

    override suspend fun login(credentials: LoginCredentials): Result<User> {
        return try {
            val hashedPassword = HashingUtil.md5(credentials.password)
            val request = LoginRequest(credentials.email, hashedPassword)
            val response = remoteDataSource.login(request)
            
            if (response.isSuccess && response.data != null) {
                val user = response.data.toDomain()
                localDataSource.saveUser(user.toEntity())
                Result.Success(user)
            } else {
                Result.Error(AppException.AuthException(response.errorMessage ?: "Unknown login error"))
            }
        } catch (e: Exception) {
            val error = (e as? AppException) ?: AppException.UnknownError(e.message, e)
            Result.Error(error)
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            localDataSource.clearUser()
            Result.Success(Unit)
        } catch (e: Exception) {
            val error = (e as? AppException) ?: AppException.UnknownError(e.message, e)
            Result.Error(error)
        }
    }

    override suspend fun getCurrentUser(): Result<User?> {
        return try {
            val userEntity = localDataSource.getUser()
            Result.Success(userEntity?.toDomain())
        } catch (e: Exception) {
            val error = (e as? AppException) ?: AppException.UnknownError(e.message, e)
            Result.Error(error)
        }
    }

    override suspend fun isLoggedIn(): Boolean {
        return localDataSource.getToken() != null
    }
}
