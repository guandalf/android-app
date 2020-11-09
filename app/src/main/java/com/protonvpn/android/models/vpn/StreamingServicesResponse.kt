/*
 * Copyright (c) 2020 Proton Technologies AG
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
package com.protonvpn.android.models.vpn

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class StreamingServicesResponse(
    @SerialName(value = "ResourceBaseURL") val resourceBaseURL: String,
    @SerialName(value = "StreamingServices") val countryToServices: Map<String, Map<String, List<StreamingService>>>
) {
    fun filter(userTier: Int, country: String) = countryToServices[country]?.filter {
        val tier = it.key.toIntOrNull()
        tier != null && tier <= userTier
    }?.flatMap {
        it.value
    }?.distinctBy { it.name }
}

@Serializable
class StreamingService(
    @SerialName(value = "Name") val name: String,
    @SerialName(value = "Icon") val iconName: String
)
