package fes.aragon.agendaapp.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import fes.aragon.agendaapp.data.remote.ContactDataSource
import fes.aragon.agendaapp.data.remote.ContactDataSourceImpl
import fes.aragon.agendaapp.repository.database.ContactRepoImpl
import fes.aragon.agendaapp.repository.database.ContactsRepo

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class ActivityModule {
    @Binds
    abstract fun bindRepoImpl(repoImpl: ContactRepoImpl): ContactsRepo

    @Binds
    abstract fun bindContactDataSourceImpl(contactDataSourceImpl: ContactDataSourceImpl): ContactDataSource
}