package me.laotang.carry.demo.di

import android.content.Context
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import me.laotang.carry.demo.app.ResponseJsonAdapter
import me.laotang.carry.demo.model.adapter.UserTypeAdapter
import me.laotang.carry.demo.model.entity.UserType
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object ComponentDi {
    @Singleton
    @Provides
    fun provideMoshi(
        @ApplicationContext context: Context
    ): Moshi {
        return Moshi.Builder()
            .add(ResponseJsonAdapter.FACTORY)
            .add(UserType::class.java, UserTypeAdapter())
            .build()
    }
}