package com.example.employe_manage.Modals

object CurrentUserManager {
    private var currentUser: User? = null

    fun getCurrentUser(): User? {
        return currentUser
    }

    fun setCurrentUser(user: User?) {
        currentUser = user
    }
}
