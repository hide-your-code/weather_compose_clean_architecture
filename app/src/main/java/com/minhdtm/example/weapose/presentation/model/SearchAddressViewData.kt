package com.minhdtm.example.weapose.presentation.model

import com.minhdtm.example.weapose.data.local.room.HistorySearchAddressEntity
import javax.inject.Inject

data class HistorySearchAddressViewData(
    val address: String = "",
    val timeSearch: Long = 0,
) : ViewData()

class HistorySearchAddressViewDataMapper @Inject constructor() :
    DataModelMapper<HistorySearchAddressEntity, HistorySearchAddressViewData> {
    override fun mapToModel(viewData: HistorySearchAddressViewData): HistorySearchAddressEntity =
        HistorySearchAddressEntity(
            addressName = viewData.address,
            timeSearch = System.currentTimeMillis(),
        )

    override fun mapToViewData(model: HistorySearchAddressEntity): HistorySearchAddressViewData =
        HistorySearchAddressViewData(
            address = model.addressName,
            timeSearch = model.timeSearch,
        )
}
