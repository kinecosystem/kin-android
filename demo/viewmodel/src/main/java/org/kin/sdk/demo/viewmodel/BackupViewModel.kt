package org.kin.sdk.demo.viewmodel

import org.kin.sdk.design.viewmodel.tools.ViewModel

interface BackupViewModel: ViewModel<BackupViewModel.NavigationArgs, BackupViewModel.State> {
    data class NavigationArgs(val kinAccountId: String)
    class State
}

interface RestoreViewModel : ViewModel<RestoreViewModel.NavigationArgs, RestoreViewModel.State> {
    class NavigationArgs
    class State
}
