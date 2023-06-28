package com.qw73.shaders

import android.app.Application
import android.content.res.AssetManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
object AppModule {
    @Provides
    fun provideAssetManager(application: Application): AssetManager {
        return application.assets
    }
}