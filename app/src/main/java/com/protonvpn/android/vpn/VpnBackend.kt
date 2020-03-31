/*
 * Copyright (c) 2019 Proton Technologies AG
 * 
 * This file is part of ProtonVPN.
 * 
 * ProtonVPN is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ProtonVPN is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with ProtonVPN.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.protonvpn.android.vpn

import androidx.lifecycle.MutableLiveData
import com.protonvpn.android.models.config.UserData
import com.protonvpn.android.models.profiles.Profile
import com.protonvpn.android.models.vpn.ConnectionParams
import com.protonvpn.android.models.vpn.Server
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull

data class RetryInfo(val timeoutSeconds: Int, val retryInSeconds: Int)

data class PrepareResult(val backend: VpnBackend, val connectionParams: ConnectionParams)

interface VpnBackendProvider {
    suspend fun prepareConnection(profile: Profile, server: Server, userData: UserData): PrepareResult?
}

abstract class VpnBackend(val name: String) : VpnStateSource {

    abstract suspend fun prepareForConnection(profile: Profile, server: Server, scan: Boolean): PrepareResult?
    abstract suspend fun connect()
    abstract suspend fun disconnect()
    abstract suspend fun reconnect()
    abstract val retryInfo: RetryInfo?

    protected suspend fun waitForDisconnect() {
        withTimeoutOrNull(DISCONNECT_WAIT_TIMEOUT) {
            do {
                delay(200)
            } while (selfState != VpnStateMonitor.State.Disabled)
        }
        if (selfState == VpnStateMonitor.State.Disconnecting)
            setSelfState(VpnStateMonitor.State.Disabled)
    }

    var active = false
    override val selfStateObservable = MutableLiveData<VpnStateMonitor.State>(VpnStateMonitor.State.Disabled)

    companion object {
        private const val DISCONNECT_WAIT_TIMEOUT = 3000L
    }
}
